package com.library.base.net;


/**
 * HTTP响应
 *
 * @Author: jerome
 * @Date: 2017-09-20
 */
public interface HttpCallback<T> {

    void onSuccess(T data);

    void onFailure(int errorCode, String message);
}
