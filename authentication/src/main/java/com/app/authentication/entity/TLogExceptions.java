package com.app.authentication.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_log_exceptions")
public class TLogExceptions {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String package_name;
    private String class_name;
    private String function_name;
    @Column(length = 8000)
    private String exception_message;
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime trans_datetime = LocalDateTime.now();

    public TLogExceptions(){

    }

    public TLogExceptions(String package_name, String class_name, String function_name, String exception_message) {
        this.package_name = package_name;
        this.class_name = class_name;
        this.function_name = function_name;
        this.exception_message = exception_message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getFunction_name() {
        return function_name;
    }

    public void setFunction_name(String function_name) {
        this.function_name = function_name;
    }

    public String getException_message() {
        return exception_message;
    }

    public void setException_message(String exception_message) {
        this.exception_message = exception_message;
    }

    public LocalDateTime getTrans_datetime() {
        return trans_datetime;
    }

    public void setTrans_datetime(LocalDateTime trans_datetime) {
        this.trans_datetime = trans_datetime;
    }

    @Override
    public String toString() {
        return "TLogExceptions{" +
                "id=" + id +
                ", package_name='" + package_name + '\'' +
                ", class_name='" + class_name + '\'' +
                ", function_name='" + function_name + '\'' +
                ", exception_message='" + exception_message + '\'' +
                ", trans_datetime=" + trans_datetime +
                '}';
    }
}
