package com.readify.example.exception;

import com.readify.example.enumaration.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BackendException extends Exception{
    private ErrorCode errorCode;
    private  String message;

}
