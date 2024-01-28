package com.tyne.finance.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class UserCreationRequest {
    @NotNull(message = "username is required")
    private String username;

    @JsonProperty("email_address")
    @Email(message = "Enter a valid email address")
    private String emailAddress;

    @NotNull(message = "password is required")
    private String password;

    @NotNull(message = "currency is required")
    private String currency;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;
}
