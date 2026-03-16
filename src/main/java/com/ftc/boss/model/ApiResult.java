package com.ftc.boss.model;

public class ApiResult {
    private boolean success;
    private String message;
    private Object data;

    public ApiResult() {}
    public ApiResult(boolean success, String message) { this.success = success; this.message = message; }
    public ApiResult(boolean success, String message, Object data) { this.success = success; this.message = message; this.data = data; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean v) { this.success = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { this.message = v; }
    public Object getData() { return data; }
    public void setData(Object v) { this.data = v; }
}
