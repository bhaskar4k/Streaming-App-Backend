package com.app.middleware.environment;

import java.util.Arrays;
import java.util.List;

public class ApiEndpointInfo {
    private List<String> unauthenticatedEndpoints = Arrays.asList(
            "/api/authentication/do_signup",
            "/api/authentication/do_login",
            "/api/authentication/verify_request",
            "/api/authentication-websocket",
            "/api/upload/update_video_processing_status",
            "/api/streaming/video_file/*/*"
    );

    public List<String> getUnauthenticatedEndpoints() {
        return this.unauthenticatedEndpoints;
    }
}
