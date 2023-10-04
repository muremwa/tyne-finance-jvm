package com.tyne.finance.core.auth;

import com.tyne.finance.configurations.ConfigProperties;
import com.tyne.finance.exceptions.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Imitates the DJANGO password hashing and matching.
 * Ensures passwords created by django can be matched here.
 * Ensures passwords created here can be matched by django.
 * **/
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationSecurity {
    private final ConfigProperties properties;
    private final String ALGORITHM_ALIAS = "pbkdf2_sha256";


    /**
     *  get the details of a DB store password (algorithm, salt, hash, iterations),
     *  <algorithm>$<iterations>$<salt>$<hash>
     * **/
    public AuthModel deconstructDBPassword(String password) throws AuthenticationException {
        log.info("Deconstructing DB password");
        String[] passwordParts = password.split("\\$");

        if (passwordParts.length == 4) {
            log.info("Deconstructed DB password");
            return AuthModel.builder()
                    .algorithm(passwordParts[0])
                    .iterations(Integer.parseInt(passwordParts[1]))
                    .salt(passwordParts[2])
                    .password(passwordParts[3])
                    .build();
        } else {
            log.error("DB Password is has an invalid format");
            throw new AuthenticationException("Invalid format DB password");
        }
    }

    /**
     *  create a DB store password (algorithm, salt, hash, iterations),
     *  <algorithm>$<iterations>$<salt>$<hash>
     * **/
    public String constructDBPasswordString(String salt, String password, int iterations) {
        log.info("Constructing DB password");
        return String.format("%s$%d$%s$%s", this.ALGORITHM_ALIAS, iterations, salt, password);
    }

    public boolean isPasswordUsable(String password) {
        return !password.equals("!");
    }

    /** Uses PBKDF2 to derive a key from a password **/
    public String generatePasswordHash(char[] password, byte[] salt, int iterations) {
        try {
            log.info("Hashing initiating");
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, this.properties.getSecurity().getHashLen());
            SecretKeyFactory factory = SecretKeyFactory.getInstance(this.properties.getSecurity().getHashAlgorithm());
            byte[] encodedKey = factory.generateSecret(spec).getEncoded();
            log.info("Hashing success");
            return Base64.getEncoder().encodeToString(encodedKey);
        } catch (Exception e) {
            log.error("Exception hashing password: {}", e.getLocalizedMessage());
            return "!";
        }
    }

    /** create a key from a new password **/
    public String createNewPasswordKey(String newPassword) throws AuthenticationException {
        log.info("Creating new password key");
        byte[] randomSalt = new byte[this.properties.getSecurity().getSaltLen()];
        (new SecureRandom()).nextBytes(randomSalt);
        String saltAsString = Base64.getEncoder().encodeToString(randomSalt);

        String password = this.generatePasswordHash(
                newPassword.toCharArray(), saltAsString.getBytes(), this.properties.getSecurity().getHashIterations()
        );

        if (this.isPasswordUsable(password)) {
            log.info("Completed password creation");
            return this.constructDBPasswordString(
                    saltAsString, password, this.properties.getSecurity().getHashIterations()
            );
        } else {
            log.info("Generated password is unusable");
            throw new AuthenticationException("Password could not be saved");
        }
    }

    /** Check password is correct/ Authenticate **/
    public boolean checkPasswordIsCorrect(String password, String dbPassword) {
        try {
            log.info("Matching passwords");
            AuthModel passwordModel = this.deconstructDBPassword(dbPassword);

            if (!passwordModel.getAlgorithm().equals(this.ALGORITHM_ALIAS)) {
                log.error("Unsupported Hashing algorithm: {}", passwordModel.getAlgorithm());
                throw new AuthenticationException();
            }

            String hashedPassword = this.generatePasswordHash(
                    password.toCharArray(), passwordModel.getSaltByteArray(), passwordModel.getIterations()
            );
            return Arrays.equals(hashedPassword.toCharArray(), passwordModel.getPasswordCharArray());
        } catch (AuthenticationException e) {
            return false;
        }
    }
}
