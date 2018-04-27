package com.library.base.runit;

/**
 * 参数化测试回调
 */
public interface RUnitArgumentTestListener<T> {
    /**
     * 测试成功调用
     *
     * @param data 实体数据
     */
    void onApiSuccess(T data);

    /**
     * 测试失败调用
     *
     */
    void onApiError(int error, String msg);

    /**
     * 测试完成
     */
    void finish();
}
