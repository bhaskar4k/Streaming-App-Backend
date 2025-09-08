package com.app.middleware.controller;

import com.app.middleware.environment.Environment;
import com.app.middleware.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiGatewayController {

    private final WebClient webClient;
    private Environment environment;

    @Autowired
    private AuthService authService;

    public ApiGatewayController(WebClient webClient) {
        this.webClient = webClient;
        this.environment = new Environment();
    }

    @RequestMapping("/{serviceName}/**")
    public ResponseEntity<byte[]> routeRequest(
            @PathVariable String serviceName,
            HttpServletRequest request,
            @RequestBody(required = false) byte[] body
    ) {
        try {
            String forwardUri = extractForwardUri(request, serviceName);
            String targetUrl = getServiceUrl(serviceName) + "/" + serviceName + forwardUri;

            HttpMethod method = HttpMethod.valueOf(request.getMethod());

            // Copy original request headers (frontend headers)
            HttpHeaders originalHeaders = new HttpHeaders();
            Collections.list(request.getHeaderNames())
                    .forEach(headerName -> {
                        Collections.list(request.getHeaders(headerName))
                                .forEach(headerValue -> originalHeaders.add(headerName, headerValue));
                    });

            // Prepare headers for middleware -> microservice
            HttpHeaders forwardHeaders = new HttpHeaders();
            forwardHeaders.putAll(originalHeaders);
            forwardHeaders.remove(HttpHeaders.HOST);
            forwardHeaders.remove(HttpHeaders.CONTENT_LENGTH);
            forwardHeaders.set(HttpHeaders.ORIGIN, environment.getMiddlewareOrigin());

            ObjectMapper objectMapper = new ObjectMapper();
            String jwtDetailsJson = objectMapper.writeValueAsString(authService.getAuthenticatedUserFromContext());

            forwardHeaders.set("JwtDetails", jwtDetailsJson);

            if (!forwardHeaders.containsKey(HttpHeaders.CONTENT_TYPE)) {
                forwardHeaders.setContentType(MediaType.APPLICATION_JSON);
            }

            WebClient.RequestBodySpec spec = webClient
                    .method(method)
                    .uri(targetUrl)
                    .headers(httpHeaders -> httpHeaders.addAll(forwardHeaders));

            WebClient.ResponseSpec responseSpec = (body != null && body.length > 0) ?
                    spec.body(BodyInserters.fromValue(body)).retrieve() :
                    spec.retrieve();

            // Inter microservice API call
            ResponseEntity<byte[]> responseEntity = responseSpec.toEntity(byte[].class).block();

            return ResponseEntity
                    .status(responseEntity.getStatusCode())
                    .body(responseEntity.getBody());

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500)
                    .body(("Error forwarding request: " + ex.getMessage()).getBytes());
        }
    }

    private HttpHeaders filterResponseHeaders(HttpHeaders originalHeaders) {
        HttpHeaders filtered = new HttpHeaders();
        originalHeaders.forEach((key, values) -> {
            if (!key.equalsIgnoreCase(HttpHeaders.TRANSFER_ENCODING) &&
                    !key.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                filtered.put(key, values);
            }
        });
        return filtered;
    }

    private String extractForwardUri(HttpServletRequest request, String serviceName) {
        String originalUri = request.getRequestURI();  // e.g., /api/authentication/do_login
        return originalUri.substring(("/api/" + serviceName).length());  // e.g., /do_login
    }

    private String getServiceUrl(String serviceName) {
        Map<String, String> serviceMap = Map.of(
                "authentication", "http://localhost:8090",
                "dashboard", "http://localhost:8091",
                "streaming", "http://localhost:8092",
                "upload", "http://localhost:8093"
        );
        return serviceMap.get(serviceName);
    }
}