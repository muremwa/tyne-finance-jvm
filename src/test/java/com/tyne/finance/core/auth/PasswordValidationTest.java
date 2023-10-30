package com.tyne.finance.core.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tyne.finance.configurations.ConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class PasswordValidationTest {
    PasswordValidatorService validator;
    ConfigProperties properties;

    @BeforeEach
    void setUp() {
        ConfigProperties.Security security = new ConfigProperties.Security();
        security.setPasswordMinLen(9);
        security.setMaxPasswordSimilarity(0.7);
        security.setCommonPasswordsFile("src/test/java/test/resources/static/auth/common-passwords.txt");
        properties = new ConfigProperties();
        properties.setSecurity(security);

        validator = new PasswordValidatorService(properties);
    }

    @Test
    void testMinLengthValidator() {
        assertTrue(validator.validate("short", null).contains(
                "This password is too short. It must contain at least 9 characters"
        ));
    }

    @Test
    void testNumericPassword() {
        List<String> invalidPasswords = List.of("1234", "32562635243242", "2");
        for (String password: invalidPasswords) {
            assertTrue(validator.validate(password, null).contains("This password is entirely numeric"));
        }

        List<String> validPasswords = List.of("password123", "123password", "pass123word", "pa1ss2wor3d");
        for (String password: validPasswords) {
            assertFalse(validator.validate(password, null).contains("This password is entirely numeric"));
        }
    }

    @Test
    void testAttributeSimilarity() {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("username", "jim");
        attributes.put("FirstName", "Jim");
        attributes.put("LastName", "Halpert");
        attributes.put("email", "jimwallace@gmail.com");

        assertTrue(validator.validate("jim", attributes).contains(
                "The password is too similar to the FirstName, username"
        ));
    }

    @Test
    void testSetUpPasswords() {
        List<String> passwords = List.of(
                "111111", "123456", "123456789", "benjamin", "diosesfiel", "million2", "password", "qwerty", "tudelft"
        );
        assertThrows(RuntimeException.class, () -> PasswordValidatorService.setUpCommonPasswords(""));

        assertEquals(passwords, PasswordValidatorService.setUpCommonPasswords(
                "src/test/java/test/resources/static/auth/common-passwords.txt"
        ));
    }

    @Test
    void testCommonPasswords() {
        assertTrue(validator.validate("benjamin", null).contains("This password is too common"));
        assertFalse(validator.validate("jim", null).contains("This password is too common"));
    }
}
