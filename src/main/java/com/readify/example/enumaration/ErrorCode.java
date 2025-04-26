package com.readify.example.enumaration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
@Getter
public enum ErrorCode {
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND),
    ACCESS_DENIED(HttpStatus.FORBIDDEN),
    REQUEST_ERROR(HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED);
    private HttpStatus httpStatus;
}
