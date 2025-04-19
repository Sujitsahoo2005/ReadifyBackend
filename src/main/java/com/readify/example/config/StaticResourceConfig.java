package com.readify.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${application.assetPath}")
    private String assetPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Make sure assetPath ends with "/"
        if (!assetPath.endsWith("/")) {
            assetPath += "/";
        }

        registry.addResourceHandler("/assets/**")
                .addResourceLocations("file:" + assetPath);
    }
}
