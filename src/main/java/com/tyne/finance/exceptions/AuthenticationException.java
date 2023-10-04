package com.tyne.finance.exceptions;

public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException() {
        super();
    }
}
