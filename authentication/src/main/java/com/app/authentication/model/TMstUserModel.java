package com.app.authentication.model;

import java.time.LocalDateTime;

public class TMstUserModel {
    private Long id;
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private int is_subscribed ;
    private int is_active;
    private LocalDateTime trans_datetime;

    public TMstUserModel() {

    }

    public TMstUserModel(String first_name, String last_name, String email, String password, int is_subscribed, int is_active, LocalDateTime trans_datetime) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.is_subscribed = is_subscribed;
        this.is_active = is_active;
        this.trans_datetime = trans_datetime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public LocalDateTime getTrans_datetime() {
        return trans_datetime;
    }

    public void setTrans_datetime(LocalDateTime trans_datetime) {
        this.trans_datetime = trans_datetime;
    }

    @Override
    public String toString() {
        return "T_mst_user{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", is_subscribed=" + is_subscribed +
                ", is_active=" + is_active +
                ", trans_datetime=" + trans_datetime +
                '}';
    }
}
