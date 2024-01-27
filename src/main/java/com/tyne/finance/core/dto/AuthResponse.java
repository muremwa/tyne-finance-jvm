package com.tyne.finance.core.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AuthResponse {
    private String username;
    private String token;
    private Long tokenTTL;
}
