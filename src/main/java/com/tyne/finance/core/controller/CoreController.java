package com.tyne.finance.core.controller;

import com.tyne.finance.configurations.ConfigProperties;
import com.tyne.finance.core.auth.AuthenticationSecurity;
import com.tyne.finance.core.auth.CoreUserDetailsService;
import com.tyne.finance.core.auth.i.JwtProvider;
import com.tyne.finance.core.dto.AuthRequest;
import com.tyne.finance.core.dto.AuthResponse;
import com.tyne.finance.dto.TyneResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("/core")
@RequiredArgsConstructor
public class CoreController {
    private final CoreUserDetailsService userDetailsService;
    private final JwtProvider jwt;
    private final ConfigProperties properties;
    private final AuthenticationSecurity security;

    @PostMapping("/sign-in")
    public ResponseEntity<TyneResponse<AuthResponse>> signIn(@Valid @RequestBody AuthRequest request) {
        UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
        boolean isPasswordCorrect = this.security.checkPasswordIsCorrect(request.getPassword(), user.getPassword());
        AuthResponse.AuthResponseBuilder response = AuthResponse.builder().username(request.getUsername());

        if (isPasswordCorrect) {
            response
                    .tokenTTL(this.properties.getSecurity().getTokenExpirationTime())
                    .token(this.jwt.generateToken(user));
        }

        return new ResponseEntity<>(
                TyneResponse.<AuthResponse>builder()
                        .status(isPasswordCorrect)
                        .message(isPasswordCorrect? "SUCCESS": "Invalid Credentials")
                        .data(response.build())
                        .build(),
                isPasswordCorrect? HttpStatus.OK: HttpStatus.UNAUTHORIZED
        );
    }
}
