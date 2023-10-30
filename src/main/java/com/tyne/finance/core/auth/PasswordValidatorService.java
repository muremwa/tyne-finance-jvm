package com.tyne.finance.core.auth;


import com.tyne.finance.configurations.ConfigProperties;
import com.tyne.finance.core.auth.i.PasswordValidator;
import com.tyne.finance.exceptions.core.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@FunctionalInterface
interface PasswordValidatorFunction {
    void apply() throws ValidationException;
}

@Service
@Slf4j
public class PasswordValidatorService implements PasswordValidator {
    private final ConfigProperties properties;
    private final List<String> commonPasswords;

    public PasswordValidatorService(ConfigProperties properties) {
        this.properties = properties;
        this.commonPasswords = PasswordValidatorService.setUpCommonPasswords(
                this.properties.getSecurity().getCommonPasswordsFile()
        );
    }

    @Override
    public List<String> validate(String password, Map<String, String> attributes) {
        log.info("Validating password: {}", password);
        PasswordValidatorFunction[] validators = new PasswordValidatorFunction[4];
        validators[0] = () -> this.userAttributeSimilarityValidator(password, attributes);
        validators[1] = () -> this.minimumLengthValidator(password);
        validators[2] = () -> this.numericPasswordValidator(password);
        validators[3] = () -> this.commonPasswordsValidator(password);

        List<String> violations = new ArrayList<>();
        for(PasswordValidatorFunction validator: validators) {
            try {
                validator.apply();
            } catch (ValidationException e) {
                violations.add(e.getLocalizedMessage());
            }
        }

        if (!violations.isEmpty()) {
            log.info("Password not valid: {}", String.join("; ", violations));
        }
        log.info("Password validation complete");
        return violations;
    }

    private void userAttributeSimilarityValidator(String password, Map<String, String> attributes) throws ValidationException {
        if(attributes != null && !attributes.isEmpty()) {
            List<String> similarity = new ArrayList<>();
            LevenshteinDistance levenshteinDistance = new LevenshteinDistance();

            for(Map.Entry<String, String> attribute: attributes.entrySet()) {
                int editDistance = levenshteinDistance.apply(password, attribute.getValue().toLowerCase());
                double editDistanceRatio = editDistance / (double) Math.max(attribute.getValue().length(), password.length());

                double similarityRatio = 1 - editDistanceRatio;
                if (similarityRatio > this.properties.getSecurity().getMaxPasswordSimilarity()) {
                    similarity.add(attribute.getKey());
                }
            }

            if (!similarity.isEmpty()) {
                throw new ValidationException(
                        String.format("The password is too similar to the %s", String.join(", ", similarity))
                );
            }
        }
    }

    private void minimumLengthValidator(String password) throws ValidationException {
        int minLen = this.properties.getSecurity().getPasswordMinLen();
        if (password.length() < minLen) {
            throw new ValidationException(
                    "This password is too short. It must contain at least " + minLen + " characters"
            );
        }
    }

    private void commonPasswordsValidator(String password) throws ValidationException {
        if (Collections.binarySearch(this.commonPasswords, password) > -1) {
            throw new ValidationException("This password is too common");
        }
    }

    private void numericPasswordValidator(String password) throws ValidationException {
        if (password.matches("^\\d*$")) {
            throw new ValidationException("This password is entirely numeric");
        }
    }

    /** Read the common passwords file and set them up and sort them in ascending order **/
    public static List<String> setUpCommonPasswords(String commonPasswordsFile) {
        try {
            List<String> lines = Files.readAllLines(Path.of(commonPasswordsFile));
            lines.sort(null);
            return lines;
        } catch (Exception e) {
            log.error("Could not load common passwords from: {}: {}", commonPasswordsFile, e.getLocalizedMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
