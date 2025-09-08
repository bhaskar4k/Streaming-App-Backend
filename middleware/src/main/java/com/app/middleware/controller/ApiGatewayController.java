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
import reactor.core.publisher.Mono;


import java.io.InputStream;
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
            HttpServletRequest request
    ) {
        try {
            String forwardUri = extractForwardUri(request, serviceName);
            String targetUrl = getServiceUrl(serviceName) + "/" + serviceName + forwardUri;
            HttpMethod method = HttpMethod.valueOf(request.getMethod());

            // Copy original headers
            HttpHeaders forwardHeaders = new HttpHeaders();
            Collections.list(request.getHeaderNames())
                    .forEach(headerName -> {
                        Collections.list(request.getHeaders(headerName))
                                .forEach(headerValue -> forwardHeaders.add(headerName, headerValue));
                    });
            forwardHeaders.remove(HttpHeaders.HOST);
            forwardHeaders.remove(HttpHeaders.CONTENT_LENGTH);
            forwardHeaders.set(HttpHeaders.ORIGIN, environment.getMiddlewareOrigin());

            ObjectMapper objectMapper = new ObjectMapper();
            String jwtDetailsJson = objectMapper.writeValueAsString(authService.getAuthenticatedUserFromContext());
            forwardHeaders.set("JwtDetails", jwtDetailsJson);

            InputStream inputStream = request.getInputStream();

            WebClient.RequestBodySpec spec = webClient
                    .method(method)
                    .uri(targetUrl)
                    .headers(httpHeaders -> httpHeaders.addAll(forwardHeaders));

            Mono<ResponseEntity<byte[]>> responseMono = spec
                    .body(BodyInserters.fromDataBuffers(
                            org.springframework.core.io.buffer.DataBufferUtils.readInputStream(
                                    () -> inputStream,
                                    new org.springframework.core.io.buffer.DefaultDataBufferFactory(),
                                    4096)))
                    .retrieve()
                    .toEntity(byte[].class);

            // Block to get the response synchronously
            ResponseEntity<byte[]> responseEntity = responseMono.block();

            return ResponseEntity
                    .status(responseEntity.getStatusCode())
                    .body(responseEntity.getBody());

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500)
                    .body(("Error forwarding request: " + ex.getMessage()).getBytes());
        }
    }

    private String extractForwardUri(HttpServletRequest request, String serviceName) {
        String originalUri = request.getRequestURI();
        return originalUri.substring(("/api/" + serviceName).length());
    }

    private String getServiceUrl(String serviceName) {
        Map<String, String> serviceMap = Map.of(
                "authentication", "http://localhost:8090",
                "dashboard", "http://localhost:8091",
                "streaming", "http://localhost:8092",
                "upload", "http://localhost:8093",
                "manage_video", "http://localhost:8093"
        );
        return serviceMap.get(serviceName);
    }
}