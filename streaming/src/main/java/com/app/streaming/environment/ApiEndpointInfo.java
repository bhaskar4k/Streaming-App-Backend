package com.app.streaming.environment;

import java.util.Arrays;
import java.util.List;

public class ApiEndpointInfo {
    private List<String> unauthenticatedEndpoints = Arrays.asList(
            "/streaming/video_file/*"
    );

    public List<String> getUnauthenticatedEndpoints() {
        return this.unauthenticatedEndpoints;
    }
}
