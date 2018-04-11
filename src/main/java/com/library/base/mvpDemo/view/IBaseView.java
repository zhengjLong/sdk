package com.library.base.mvpDemo.view;

/**
 * View层基类
 *
 * @Author: jerome
 * @Date: 2018-01-30
 */
public interface IBaseView {

    void showLoading();

    void dismissLoading();

    void showToast(String msg);

    void requestError(int code, String message);
}
