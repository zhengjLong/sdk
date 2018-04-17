package com.library.base.mvpDemo.view;

/**
 * View层基类
 * @author : jerome
 */
public interface IBaseView {

    /**
     * 显示加载框
     */
    void showLoading();

    /**
     * 关闭加载框
     */
    void dismissLoading();

    /**
     * toast提示
     * @param msg
     */
    void showToast(String msg);

    /**
     * 错误统一回调
     * @param code
     * @param message
     */
    void requestError(int code, String message);
}
