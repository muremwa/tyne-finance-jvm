package com.tyne.finance.core.auth.i;


import java.util.List;
import java.util.Map;

public interface PasswordValidator {
    List<String> validate(String password, Map<String, String> attributes);
}
