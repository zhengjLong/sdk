package com.library.base.net;

/**
 * 错误代码
 * @author : jerome
 */
public enum ApiErrorCode {

    ERROR_NET_WORK(-10000, "网络请求失败"),
    ERROR_NOT_NET_WORK(-99, "请打开网络连接"),
    ERROR_UNKNOWN(-1, "发生未知错误"),
    ERROR_JSON_EXCEPTION(120, "返回数据格式异常"),
    ERROR_SERVER_EXCEPTION(500, "服务器连接失败,请联系管理员。"),
    ERROR_TOKEN_INVALID(-99, "TOKEN过期"),
    ERROR_LOGIN_INVALID(-98, "登录过期，请重新登录"),
    ERROR_EMPTY_DATA(700, "暂无数据");


    private int errorCode;
    private String message;

    ApiErrorCode(int code, String msg) {
        this.errorCode = code;
        this.message = msg;
    }

    /**
     * 根据错误码获得对应实例
     * @param errorCode
     * @return
     */
    public ApiErrorCode valueOf(int errorCode) {
        ApiErrorCode[] values = ApiErrorCode.values();
        for (ApiErrorCode val : values) {
            if (val.errorCode == errorCode) {
                return val;
            }
        }
        return ERROR_UNKNOWN;
    }

    /**
     * 错误码
     * @return
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * 错误信息
     * @return
     */
    public String getMessage() {
        return message;
    }
}
