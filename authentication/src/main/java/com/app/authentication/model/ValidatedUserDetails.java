package com.app.authentication.model;

public class ValidatedUserDetails {
    String logout_ws_endpoint;
    String jwt;

    public ValidatedUserDetails(String logout_ws_endpoint, String JWT) {
        this.logout_ws_endpoint = logout_ws_endpoint;
        this.jwt = JWT;
    }

    public String getLogout_ws_endpoint() {
        return logout_ws_endpoint;
    }

    public void setLogout_ws_endpoint(String logout_ws_endpoint) {
        this.logout_ws_endpoint = logout_ws_endpoint;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
