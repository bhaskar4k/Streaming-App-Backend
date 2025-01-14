package com.app.authentication.environment;

import java.util.Arrays;
import java.util.List;

public class ApiEndpointInfo {
    private List<String> unauthenticatedEndpoints = Arrays.asList(
            "/authentication/do_signup",
            "/authentication/do_login",
            "/authentication-websocket"
    );

    private List<String> authenticatedEndpoints = Arrays.asList(
            "/authentication/get_userid_from_jwt"
    );

    public List<String> getUnauthenticatedEndpoints() {
        return this.unauthenticatedEndpoints;
    }

    public List<String> getAuthenticatedEndpoints() {
        return this.authenticatedEndpoints;
    }
}
