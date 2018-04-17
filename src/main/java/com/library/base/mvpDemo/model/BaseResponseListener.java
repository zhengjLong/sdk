package com.library.base.mvpDemo.model;

/**
 * 数据返回
 * @author : jerome
 */
public interface BaseResponseListener<T> {

    void onSuccess(T data);

    void onFailure(int errorCode, String message);
}
