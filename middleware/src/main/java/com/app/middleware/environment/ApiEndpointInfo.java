package com.app.middleware.environment;

import java.util.Arrays;
import java.util.List;

public class ApiEndpointInfo {
    private List<String> unauthenticatedEndpoints = Arrays.asList(
            "/upload/update_video_processing_status"
    );

    public List<String> getUnauthenticatedEndpoints() {
        return this.unauthenticatedEndpoints;
    }
}
