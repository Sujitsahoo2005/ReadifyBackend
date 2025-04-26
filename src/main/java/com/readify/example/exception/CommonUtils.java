package com.readify.example.exception;
//import com.mo.sishu.Jwt.service.CustomUserDetails;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;

public class CommonUtils {
    public static ResponseEntity<?> createBuildResponse(Object data, HttpStatus status){
        GenericResponse response=new GenericResponse();
        response.setData(data);
        response.setResponseStatus(status);
        response .setMessage("success");
        response.setStatus("success");

        return response.create();
    }
    public static ResponseEntity<?> createBuildResponseMessage(String message, HttpStatus status){
        GenericResponse response=GenericResponse.builder()
                .responseStatus(status)
                .message(message)
                .status("success")
                .build();
        return response.create();
    }
    public static ResponseEntity<?> createErrorResponseMessage(String message, HttpStatus status){
        GenericResponse response=GenericResponse.builder()
                .responseStatus(status)
                .data(null)
                .message(message)
                .status("failed")
                .build();
        return response.create();
    }
    public static ResponseEntity<?> createErrorResponse(Object data, HttpStatus status){
        GenericResponse response=GenericResponse.builder()
                .responseStatus(status)
                .data(data)
                .message("failed")
                .status("failed")
                .build();
        return response.create();
    }

}