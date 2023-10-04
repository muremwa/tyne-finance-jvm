package com.tyne.finance.core.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthModel {
    private String algorithm;
    private int iterations;
    private String salt;
    private String password;

    byte[] getSaltByteArray() {
        return this.salt.getBytes();
    }

    char[] getPasswordCharArray() {
        return this.password.toCharArray();
    }
}
