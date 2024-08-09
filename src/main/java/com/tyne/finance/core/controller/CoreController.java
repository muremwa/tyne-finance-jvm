package com.tyne.finance.core.controller;

import com.tyne.finance.configurations.ConfigProperties;
import com.tyne.finance.core.auth.AuthenticationSecurity;
import com.tyne.finance.core.auth.CoreUserDetails;
import com.tyne.finance.core.auth.CoreUserDetailsService;
import com.tyne.finance.core.auth.PasswordValidatorService;
import com.tyne.finance.core.auth.i.JwtProvider;
import com.tyne.finance.core.dto.AuthRequest;
import com.tyne.finance.core.dto.AuthResponse;
import com.tyne.finance.core.dto.UserCreationRequest;
import com.tyne.finance.core.models.Currency;
import com.tyne.finance.core.models.Group;
import com.tyne.finance.core.models.User;
import com.tyne.finance.core.repositories.CoreCurrencyRepository;
import com.tyne.finance.core.repositories.CoreUserRepository;
import com.tyne.finance.dto.TyneResponse;
import com.tyne.finance.exceptions.AuthenticationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController()
@RequestMapping("/core")
@RequiredArgsConstructor
@Slf4j
public class CoreController {
    private final CoreUserDetailsService userDetailsService;
    private final JwtProvider jwt;
    private final ConfigProperties properties;
    private final AuthenticationSecurity security;
    private final CoreCurrencyRepository currencyRepository;
    private final CoreUserRepository userRepository;
    private final PasswordValidatorService passwordValidatorService;
    private final Group defaultUserGroup;

    private ResponseEntity<TyneResponse<AuthResponse>> simpleBadRequest(String message) {
        return ResponseEntity
                .badRequest()
                .body(TyneResponse.<AuthResponse>builder().data(null).status(false).message(message).build());
    }

    @PostMapping("/sign-in")
    public ResponseEntity<TyneResponse<AuthResponse>> signIn(@Valid @RequestBody AuthRequest request) {
        log.info("Authenticating: {}", request.getUsername());
        UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
        boolean isPasswordCorrect = this.security.checkPasswordIsCorrect(request.getPassword(), user.getPassword());
        AuthResponse.AuthResponseBuilder response = AuthResponse.builder().username(request.getUsername());

        if (isPasswordCorrect) {
            response
                    .tokenTTL(this.properties.getSecurity().getTokenExpirationTime())
                    .token(this.jwt.generateToken(user));
        }
        log.info("Authentication status: {}", isPasswordCorrect);

        return new ResponseEntity<>(
                TyneResponse.<AuthResponse>builder()
                        .status(isPasswordCorrect)
                        .message(isPasswordCorrect? "SUCCESS": "Invalid Credentials")
                        .data(response.build())
                        .build(),
                isPasswordCorrect? HttpStatus.OK: HttpStatus.UNAUTHORIZED
        );
    }

    @PostMapping("/sign-up")
    public ResponseEntity<TyneResponse<AuthResponse>> signUp(@Valid @RequestBody UserCreationRequest request) throws AuthenticationException, URISyntaxException {
        /* Register a user
        provide the fields; "username", "email_address", "password", "currency", "first_name", "last_name"
        "username", "password", "currency" - required */
        if (this.userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmailAddress())) {
            return this.simpleBadRequest("Username or email address already exists");
        }

        Optional<Currency> currency = this.currencyRepository.findCurrencyByCode(request.getCurrency());
        if (currency.isEmpty()) {
            log.error("Currency invalid: {}", request.getCurrency());
            return this.simpleBadRequest("Invalid currency");
        }

        // validate password
        Map<String, String> similarityAttributes = new HashMap<>();
        similarityAttributes.put("username", request.getUsername());
        similarityAttributes.put("firstName", request.getFirstName());
        similarityAttributes.put("lastName", request.getLastName());
        similarityAttributes.put("email", request.getEmailAddress());
        List<String> violations = this.passwordValidatorService.validate(request.getPassword(), similarityAttributes);
        if (!violations.isEmpty()) {
            return this.simpleBadRequest(
                    String.format("Password not valid: %s", String.join("; ", violations))
            );
        }

        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.userRepository.createNewUser(
                request,
                this.security.createNewPasswordKey(request.getPassword()),
                date.format(new Date()),
                currency.get().getCurrencyID().intValue()
        );
        User newUser = this.userRepository.findUserByUsername(request.getUsername());

        if (newUser == null) {
            return ResponseEntity.internalServerError().body(
                    TyneResponse.<AuthResponse>builder().status(false).message("Could not create user").build()
            );
        }

        // add to default group
        this.userRepository.addUserToGroup(newUser.getUserID().intValue(), this.defaultUserGroup.getGroupID());
        log.info("User added to group {}", this.defaultUserGroup.getGroupID());

        AuthResponse response = AuthResponse.builder()
                .token(this.jwt.generateToken(new CoreUserDetails(newUser)))
                .tokenTTL(this.properties.getSecurity().getTokenExpirationTime())
                .username(newUser.getUsername())
                .build();

        return ResponseEntity
                .created(new URI("core/user"))
                .body(
                        TyneResponse.<AuthResponse>builder()
                                .message("Created user successfully")
                                .status(true)
                                .data(response)
                                .build()
                );
    }


    @GetMapping("/account")
    public ResponseEntity<String> accountDetails(Principal principal) {
        System.out.println(">>>>>>>>>>>>>>>>>");
        System.out.println(principal);
        System.out.println(">>>>>>>>>>>>>>>>>");

        return ResponseEntity.ok("SOMETHING");
    }
}
