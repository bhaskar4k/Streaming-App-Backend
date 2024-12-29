package com.app.authentication.model;

import java.time.LocalDateTime;

public class JwtUserDetails {
    private Long id;
    private String email;
    private int is_subscribed ;
    private int is_active;

    public JwtUserDetails(){

    }

    public JwtUserDetails(Long id, String email, int is_subscribed, int is_active) {
        this.id = id;
        this.email = email;
        this.is_subscribed = is_subscribed;
        this.is_active = is_active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIs_subscribed() {
        return is_subscribed;
    }

    public void setIs_subscribed(int is_subscribed) {
        this.is_subscribed = is_subscribed;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }
}
