package com.readify.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "application")
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommonApplicationProperties {
    @Value("${application.baseUrl}")
    String baseUrl;
    @Value("${application.assetPath}")
    String assetPath;
}