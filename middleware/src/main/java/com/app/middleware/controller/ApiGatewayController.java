package com.app.middleware.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiGatewayController {

    @RequestMapping("/{serviceName}/**")
    public String routeRequest(
            @PathVariable String serviceName,
            HttpServletRequest request,
            @RequestBody(required = false) String body
    ) {
        // Extract remaining URI path
        String forwardUri = extractForwardUri(request, serviceName);

        // Build target service URL
        String targetUrl = getServiceUrl(serviceName) + "/" + forwardUri;

        return targetUrl;
    }

    private String extractForwardUri(HttpServletRequest request, String serviceName) {
        String originalUri = request.getRequestURI();  // e.g., /api/student/getAll
        return serviceName + originalUri.substring(("/api/" + serviceName).length());  // e.g., /getAll
    }

    private String getServiceUrl(String serviceName) {
        // Use a config or hardcoded mapping
        Map<String, String> serviceMap = Map.of(
                "student", "http://localhost:8081",
                "order", "http://localhost:8082",
                "payment", "http://localhost:8083"
        );
        return serviceMap.get(serviceName);
    }

    private void copyHeaders(HttpServletRequest request, HttpHeaders headers) {
        Collections.list(request.getHeaderNames())
                .forEach(headerName -> headers.add(headerName, request.getHeader(headerName)));
    }
}
