package com.tyne.finance.configurations;

import com.tyne.finance.dto.TyneResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;


@RestControllerAdvice
public class ControllerAdvice {
    private TyneResponse<String> generateTyneResponse(String message) {
        return TyneResponse.<String>builder().status(false).data(null).message(message).build();
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<TyneResponse<String>> userNotFound() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(this.generateTyneResponse("User not found"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<TyneResponse<String>> methodNotAllowed(Exception e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(this.generateTyneResponse(e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<TyneResponse<String>> unreadableRequest() {
        return ResponseEntity.badRequest().body(this.generateTyneResponse("Required request body is missing or invalid"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<TyneResponse<String>> argumentListInvalid(MethodArgumentNotValidException exception) {
        String[] errors = new String[exception.getErrorCount()];

        for(int i = 0; i < exception.getErrorCount(); i++) {
            errors[i] = exception.getAllErrors().get(i).getDefaultMessage();
        }

        return ResponseEntity.badRequest().body(this.generateTyneResponse(String.join("; ", errors)));
    }
}
