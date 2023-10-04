package com.tyne.finance.core.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.tyne.finance.configurations.ConfigProperties;
import com.tyne.finance.exceptions.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthenticationSecurityTest {
    ConfigProperties.Security security;
    @Mock
    ConfigProperties properties;

    @InjectMocks
    AuthenticationSecurity authSecurity;

    @BeforeEach
    void setUp() {
        security = new ConfigProperties.Security();
        security.setHashAlgorithm("PBKDF2WithHmacSHA256");
        security.setHashLen(256);
        security.setHashIterations(870000);
        security.setSaltLen(16);
    }

    @Test
    void testPasswordHashing() {
        Mockito.when(properties.getSecurity()).thenReturn(security);
        String passwordValid = authSecurity.generatePasswordHash("lètmein".toCharArray(), "seasalt2".getBytes(), 870000);
        assertEquals("nxgnNHRsZWSmi4hRSKq2MRigfaRmjDhH1NH4g2sQRbU=", passwordValid);
        assertTrue(authSecurity.isPasswordUsable(passwordValid));

        Mockito.when(properties.getSecurity()).thenReturn(null);
        String passwordInvalid = authSecurity.generatePasswordHash("lètmein".toCharArray(), "seasalt2".getBytes(), 870000);
        assertEquals("!", passwordInvalid);
        assertFalse(authSecurity.isPasswordUsable(passwordInvalid));
    }

    @Test
    void testAuthModel() {
        AuthModel model = AuthModel.builder()
                .salt("salt1")
                .password("pass")
                .algorithm("SM")
                .iterations(400)
                .build();

        assertArrayEquals("pass".toCharArray(), model.getPasswordCharArray());
        assertArrayEquals("salt1".getBytes(), model.getSaltByteArray());
    }

    @Test
    void testAuthModelDerivation() throws AuthenticationException {
        String sampleStoredPassword = "pbkdf2_sha256$870000$seasalt$wJSpLMQRQz0Dhj/pFpbyjMj71B2gUYp6HJS5AU+32Ac=";
        AuthModel model = AuthModel.builder()
                .password("wJSpLMQRQz0Dhj/pFpbyjMj71B2gUYp6HJS5AU+32Ac=")
                .salt("seasalt")
                .iterations(870000)
                .algorithm("pbkdf2_sha256")
                .build();
        assertEquals(model, authSecurity.deconstructDBPassword(sampleStoredPassword));
        assertThrows(AuthenticationException.class, () -> authSecurity.deconstructDBPassword(""));
    }

    @Test
    void testConstructPassword() {
        assertEquals(
                "pbkdf2_sha256$870000$seasalt$wJSpLMQRQz0Dhj/pFpbyjMj71B2gUYp6HJS5AU+32Ac=",
                authSecurity.constructDBPasswordString(
                        "seasalt",
                        "wJSpLMQRQz0Dhj/pFpbyjMj71B2gUYp6HJS5AU+32Ac=",
                        870000
                )
        );
    }

    @Test
    void testCreateNewPasswordKey() throws AuthenticationException {
        Mockito.when(properties.getSecurity()).thenReturn(security);
        assertDoesNotThrow(() -> authSecurity.createNewPasswordKey("sample"));

        String newPasswordKey = authSecurity.createNewPasswordKey("sample");
        assertDoesNotThrow(() -> authSecurity.deconstructDBPassword(newPasswordKey));
    }

    @Test
    void testMatchPassword() {
        Mockito.when(properties.getSecurity()).thenReturn(security);
        assertTrue(authSecurity.checkPasswordIsCorrect(
                "lètmein",
                "pbkdf2_sha256$870000$seasalt$wJSpLMQRQz0Dhj/pFpbyjMj71B2gUYp6HJS5AU+32Ac="
        ));
        assertFalse(authSecurity.checkPasswordIsCorrect(
                "lètmen",
                "pbkdf2_sha256$870000$seasalt$wJSpLMQRQz0Dhj/pFpbyjMj71B2gUYp6HJS5AU+32Ac="
        ));
        assertFalse(authSecurity.checkPasswordIsCorrect(
                "lètmein",
                "pbkdf2_sha256$870000$seasalt$wJSpLMQRQz0Dhj/pFpbyjMj71B2gUYp6HJt5AU+32Ac="
        ));
        assertFalse(authSecurity.checkPasswordIsCorrect(
                "lètmein",
                "some_algo$870000$seasalt$wJSpLMQRQz0Dhj/pFpbyjMj71B2gUYp6HJt5AU+32Ac="
        ));
        assertFalse(authSecurity.checkPasswordIsCorrect(
                "lètmein",
                "/pFpbyjMj71B2gUYp6HJt5AU+32Ac="
        ));
    }

    @Test
    void testPasswordCreatedIsUsable() throws AuthenticationException {
        Mockito.when(properties.getSecurity()).thenReturn(security);
        String password = authSecurity.createNewPasswordKey("sample");
        assertTrue(authSecurity.checkPasswordIsCorrect("sample", password));
        assertFalse(authSecurity.checkPasswordIsCorrect("not_sample", password));
    }

}
