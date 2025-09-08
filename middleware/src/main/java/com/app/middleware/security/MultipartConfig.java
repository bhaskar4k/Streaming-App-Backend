package com.app.middleware.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver() {
            @Override
            public boolean isMultipart(HttpServletRequest request) {
                String path = request.getRequestURI();
                if (Objects.equals(path, "/api/upload/upload_video")) {
                    return false;
                }
                return super.isMultipart(request);
            }
        };
    }
}

