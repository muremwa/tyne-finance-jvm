package com.tyne.finance.core.auth;

import com.tyne.finance.configurations.ConfigProperties;
import com.tyne.finance.core.auth.i.JwtProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class CoreJwtProvider implements JwtProvider {
    private final ConfigProperties properties;
    private final SecretKey encryptionKey;

    public CoreJwtProvider(ConfigProperties properties) {
        this.properties = properties;
        this.encryptionKey = Keys.hmacShaKeyFor(this.properties.getSecurity().getSecretKey().getBytes());
    }

    @Override
    public String generateToken(UserDetails details) {
        Date now = new Date();
        Date expirationTime = new Date(now.getTime() + this.properties.getSecurity().getTokenExpirationTime());

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", details.getUsername());
        claims.put("iat", now);
        claims.put("roles", details.getAuthorities());
        claims.put("jit", details.getUsername());

        return Jwts.builder()
                .claims(claims)
                .subject(details.getUsername())
                .expiration(expirationTime)
                .issuedAt(now)
                .signWith(this.encryptionKey)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(this.encryptionKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
