package com.library.base.runit;


import com.library.base.net.HttpCallback;

/**
 * 测试方法运行接口
 */
public abstract class XPApiRunnable<T> extends RUnitTestCaseRunnable<T> implements HttpCallback<T> {

    @Override
    public void onSuccess(T data) {
        onApiSuccess(data);
        finish();
    }

    @Override
    public void onFailure(int errorCode, String message) {
        onApiError(errorCode, message);
    }
}
