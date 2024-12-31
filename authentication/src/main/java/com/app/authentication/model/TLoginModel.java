package com.app.authentication.model;

import jakarta.persistence.Column;

import java.time.LocalDateTime;

public class TLoginModel {
    private Long id;
    private Long t_mst_user_id;
    private String jwt_token;
    private String ip_address;
    private int device_count;
    private LocalDateTime trans_datetime;

    public TLoginModel(Long id, Long t_mst_user_id, String jwt_token, String ip_address, int device_count, LocalDateTime trans_datetime) {
        this.id = id;
        this.t_mst_user_id = t_mst_user_id;
        this.jwt_token = jwt_token;
        this.ip_address = ip_address;
        this.device_count = device_count;
        this.trans_datetime = trans_datetime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getT_mst_user_id() {
        return t_mst_user_id;
    }

    public void setT_mst_user_id(Long t_mst_user_id) {
        this.t_mst_user_id = t_mst_user_id;
    }

    public String getJwt_token() {
        return jwt_token;
    }

    public void setJwt_token(String jwt_token) {
        this.jwt_token = jwt_token;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public int getDevice_count() {
        return device_count;
    }

    public void setDevice_count(int device_count) {
        this.device_count = device_count;
    }

    public LocalDateTime getTrans_datetime() {
        return trans_datetime;
    }

    public void setTrans_datetime(LocalDateTime trans_datetime) {
        this.trans_datetime = trans_datetime;
    }

    @Override
    public String toString() {
        return "TLoginModel{" +
                "id=" + id +
                ", t_mst_user_id=" + t_mst_user_id +
                ", jwt_token='" + jwt_token + '\'' +
                ", ip_address='" + ip_address + '\'' +
                ", device_count=" + device_count +
                ", trans_datetime=" + trans_datetime +
                '}';
    }
}
