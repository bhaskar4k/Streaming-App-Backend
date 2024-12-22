package com.app.authentication.common;

public class CommonApiReturn<T> {
    private int status;
    private String message;
    private T data;

    public CommonApiReturn() {}

    public CommonApiReturn(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> CommonApiReturn<T> success(String message, T data) {
        return new CommonApiReturn<>(200, message, data);
    }

    public static <T> CommonApiReturn<T> error(int status, String message) {
        return new CommonApiReturn<>(status, message, null);
    }
}

