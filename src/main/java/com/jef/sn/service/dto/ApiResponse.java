package com.jef.sn.service.dto;

import java.io.Serializable;
import java.util.Objects;

public class ApiResponse {

    private boolean success;
    private int status;
    private String code;
    private String message;
    private Object data;

    public ApiResponse(boolean success, int status, String code, String message, Object data) {
        this.success = success;
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // getters & setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return (
            "ApiResponse{" +
            "success=" +
            success +
            ", status=" +
            status +
            ", code='" +
            code +
            '\'' +
            ", message='" +
            message +
            '\'' +
            ", data=" +
            data +
            '}'
        );
    }
}
