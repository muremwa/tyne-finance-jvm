package com.tyne.finance.core.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


@Component
public class JWTFilterClass extends OncePerRequestFilter {
    private final CoreJwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final ArrayList<String> whiteListedEndpoints;

    public JWTFilterClass(CoreJwtProvider jwtProvider, UserDetailsService userDetailsService,
                          @Value("${tyne.auth.white-endpoints}") ArrayList<String> whiteListedEndpoints) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
        this.whiteListedEndpoints = whiteListedEndpoints;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (!this.whiteListedEndpoints.contains(request.getServletPath())) {
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                Map<String, Object> claims = this.jwtProvider.validateToken(token);

                if (claims != null) {
                    String username = claims.get("sub").toString();

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails details = this.userDetailsService.loadUserByUsername(username);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                details,
                                null,
                                details.getAuthorities()
                        );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Authentication Failed\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
