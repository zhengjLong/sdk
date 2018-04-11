package com.library.base.mvpDemo.model;

/**
 * 数据返回
 *
 * @Author: jerome
 * @Date: 2018-01-30
 */
public interface BaseResponseListener<T> {

    void onSuccess(T data);

    void onFailure(int errorCode, String message);
}
