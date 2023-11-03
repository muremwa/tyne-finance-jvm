package com.tyne.finance.core.auth.i;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtProvider {
    public String generateToken(UserDetails details);

    public boolean validateToken(String token);
}
