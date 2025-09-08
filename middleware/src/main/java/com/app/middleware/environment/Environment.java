package com.app.middleware.environment;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Environment {
    private List<String> allowedOrigins = Arrays.asList("http://localhost:5173", "http://localhost:4200");
    private String authServiceUrl = "http://localhost:8090/authentication/verify_request";

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public String getAuthServiceUrl() {
        return authServiceUrl;
    }
}