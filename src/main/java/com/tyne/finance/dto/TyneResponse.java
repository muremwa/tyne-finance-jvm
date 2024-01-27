package com.tyne.finance.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TyneResponse<T> {
    private boolean status;
    private String message;
    private T data;
}
