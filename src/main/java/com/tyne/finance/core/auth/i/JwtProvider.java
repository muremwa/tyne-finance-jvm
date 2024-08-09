package com.tyne.finance.core.auth.i;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtProvider {
    public String generateToken(UserDetails details);

    public Map<String, Object> validateToken(String token);
}
