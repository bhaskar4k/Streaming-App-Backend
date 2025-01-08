package com.app.authentication.model;

public class LogoutInfoWebSocket {
    private Long t_mst_user_id;
    private String email;
    private Long device_count;

    public LogoutInfoWebSocket(Long t_mst_user_id, String email, Long device_count) {
        this.t_mst_user_id = t_mst_user_id;
        this.email = email;
        this.device_count = device_count;
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

    public Long getDevice_count() {
        return device_count;
    }

    public void setDevice_count(Long device_count) {
        this.device_count = device_count;
    }

    @Override
    public String toString() {
        return "LogoutMessageWebSocket{" +
                "t_mst_user_id=" + t_mst_user_id +
                ", email='" + email + '\'' +
                ", device_count=" + device_count +
                '}';
    }
}
