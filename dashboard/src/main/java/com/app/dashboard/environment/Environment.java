package com.app.dashboard.environment;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Environment {
    private String dashboardMenuKey = "menu";
    private List<String> allowedOrigins = Arrays.asList("http://localhost:8096");
    private List<String> unauthenticatedEndpoints = Arrays.asList();

    public String getDashboardMenuKey() {
        return dashboardMenuKey;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public List<String> getUnauthenticatedEndpoints() {
        return this.unauthenticatedEndpoints;
    }
}