package com.curso.tasks.model;

public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String error;

    private ApiResponse(boolean success, T data, String error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }

    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getError() { return error; }
}
