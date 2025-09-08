package com.app.dashboard.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import com.app.dashboard.model.JwtUserDetails;

public abstract class BaseController {

    protected JwtUserDetails getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof JwtUserDetails) {
            return (JwtUserDetails) principal;
        }
        return null;
    }
}