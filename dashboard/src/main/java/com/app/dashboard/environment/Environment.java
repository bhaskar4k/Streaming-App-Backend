package com.app.dashboard.environment;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Environment {
    private String dashboardMenuKey = "menu";
    private List<String> allowedOrigins = Arrays.asList("http://localhost:8096");

    public String getDashboardMenuKey() {
        return dashboardMenuKey;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }
}