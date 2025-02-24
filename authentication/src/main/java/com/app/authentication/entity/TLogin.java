package com.app.authentication.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_login")
public class TLogin {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long t_mst_user_id;
    @Column(length = 1000)
    private String jwt_token;
    private String ip_address;
    private Long device_count;
    private int is_active;
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime trans_datetime = LocalDateTime.now();

    public TLogin(){

    }

    public TLogin(Long t_mst_user_id, String jwt_token, String ip_address, Long device_count) {
        this.t_mst_user_id = t_mst_user_id;
        this.jwt_token = jwt_token;
        this.ip_address = ip_address;
        this.device_count = device_count;
    }

    public TLogin(Long t_mst_user_id, String jwt_token, String ip_address, Long device_count, int is_active) {
        this.t_mst_user_id = t_mst_user_id;
        this.jwt_token = jwt_token;
        this.ip_address = ip_address;
        this.device_count = device_count;
        this.is_active = is_active;
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

    public Long getDevice_count() {
        return device_count;
    }

    public void setDevice_count(Long device_count) {
        this.device_count = device_count;
    }

    public LocalDateTime getTrans_datetime() {
        return trans_datetime;
    }

    public void setTrans_datetime(LocalDateTime trans_datetime) {
        this.trans_datetime = trans_datetime;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    @Override
    public String toString() {
        return "TLogin{" +
                "id=" + id +
                ", t_mst_user_id=" + t_mst_user_id +
                ", jwt_token='" + jwt_token + '\'' +
                ", ip_address='" + ip_address + '\'' +
                ", device_count=" + device_count +
                ", trans_datetime=" + trans_datetime +
                '}';
    }
}
