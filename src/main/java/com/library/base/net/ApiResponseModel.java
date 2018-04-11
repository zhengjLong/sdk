package com.library.base.net;

import com.google.gson.annotations.SerializedName;

/**
 * 公共返回
 *
 * @Author: jerome
 * @Date: 2017-09-20
 */
public class ApiResponseModel<T> {
    @SerializedName("msg")
    private String message;
    @SerializedName("code")
    private int statusCode;
    private String requestId;
    private T data;


    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
