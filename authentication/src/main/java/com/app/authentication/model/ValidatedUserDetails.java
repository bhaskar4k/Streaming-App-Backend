package com.app.authentication.model;

public class ValidatedUserDetails {
    String device_endpoint;
    String jwt;

    public ValidatedUserDetails(String logout_ws_endpoint, String JWT) {
        this.device_endpoint = logout_ws_endpoint;
        this.jwt = JWT;
    }

    public String getDevice_endpoint() {
        return device_endpoint;
    }

    public void setDevice_endpoint(String device_endpoint) {
        this.device_endpoint = device_endpoint;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
