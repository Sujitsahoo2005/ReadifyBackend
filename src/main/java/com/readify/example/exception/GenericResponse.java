package com.readify.example.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GenericResponse {
    private HttpStatus responseStatus;
    private String status;
    private String message;
    private Object data;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public ResponseEntity<?> create(){
        Map<String,Object> map=new LinkedHashMap<>();
        map.put("status",status);
        map.put("message",message);
        map.put("timestamp", LocalDateTime.now());
        if (!ObjectUtils.isEmpty(data)){
            map.put("data",data);

        }
        return new ResponseEntity<>(map,responseStatus);
    }
}