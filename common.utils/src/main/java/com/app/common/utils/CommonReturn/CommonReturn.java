package com.app.common.utils.CommonReturn;

public class CommonReturn<T> {
    private int status;
    private String message;
    private T data;

    public CommonReturn() {}

    public CommonReturn(int status, String message, T data) {
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

    public static <T> CommonReturn<T> success(T data) {
        return new CommonReturn<>(200, "Success", data);
    }

    public static <T> CommonReturn<T> error(int status, String message) {
        return new CommonReturn<>(status, message, null);
    }
}
