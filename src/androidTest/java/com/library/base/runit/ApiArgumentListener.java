package com.library.base.runit;


import com.library.base.net.HttpCallback;

/**
 * 接口回调通知测试
 */
public class ApiArgumentListener<T> implements HttpCallback<T> {

    RUnitArgumentTestListener<T> mListener;

    public ApiArgumentListener(RUnitArgumentTestListener<T> listener) {
        mListener = listener;
    }

    @Override
    public void onSuccess(T data) {
        mListener.onApiSuccess(data);
    }

    @Override
    public void onFailure(int errorCode, String message) {
        mListener.onApiError(errorCode, message);
    }
}
