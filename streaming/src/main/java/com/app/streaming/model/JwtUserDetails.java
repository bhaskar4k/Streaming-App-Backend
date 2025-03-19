package com.app.streaming.model;

public class JwtUserDetails {
    private Long t_mst_user_id;
    private String full_name;
    private String email;
    private int is_subscribed ;
    private String ip_address;
    private Long device_count;

    public JwtUserDetails(){

    }

    public JwtUserDetails(Long t_mst_user_id, String full_name, String email, int is_subscribed, String ip_address, Long device_count) {
        this.t_mst_user_id = t_mst_user_id;
        this.full_name = full_name;
        this.email = email;
        this.is_subscribed = is_subscribed;
        this.ip_address = ip_address;
        this.device_count = device_count;
    }

    public Long getT_mst_user_id() {
        return t_mst_user_id;
    }

    public void setT_mst_user_id(Long t_mst_user_id) {
        this.t_mst_user_id = t_mst_user_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
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

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public Long getDevice_count() {
        return device_count;
    }

    public void setDevice_count(Long device_count) {
        this.device_count = device_count;
    }
}