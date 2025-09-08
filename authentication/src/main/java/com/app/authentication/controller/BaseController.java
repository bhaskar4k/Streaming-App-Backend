package com.app.authentication.controller;

import com.app.authentication.model.JwtUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController {

    @Autowired
    protected HttpServletRequest request;

    protected JwtUserDetails getJwtUserDetails() {
        try {
            String jwtDetailsJson = request.getHeader("JwtDetails");
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jwtDetailsJson, JwtUserDetails.class);
        } catch (Exception e) {
            return null;
        }
    }
}
