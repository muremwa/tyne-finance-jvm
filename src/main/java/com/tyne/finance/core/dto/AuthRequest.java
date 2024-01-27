package com.tyne.finance.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthRequest {
    @NotNull(message = "username is required")
    private String username;

    @NotNull(message = "password is required")
    private String password;
}
