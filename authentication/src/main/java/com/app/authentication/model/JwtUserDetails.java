package com.app.authentication.model;

public class JwtUserDetails {
    private Long t_mst_user_id;
    private String email;
    private int is_subscribed ;
    private int is_active;

    public JwtUserDetails(){

    }

    public JwtUserDetails(Long t_mst_user_id, String email, int is_subscribed, int is_active) {
        this.t_mst_user_id = t_mst_user_id;
        this.email = email;
        this.is_subscribed = is_subscribed;
        this.is_active = is_active;
    }

    public Long getT_mst_user_id() {
        return t_mst_user_id;
    }

    public void setT_mst_user_id(Long t_mst_user_id) {
        this.t_mst_user_id = t_mst_user_id;
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
