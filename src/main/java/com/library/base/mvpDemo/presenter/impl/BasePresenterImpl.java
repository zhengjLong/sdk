package com.library.base.mvpDemo.presenter.impl;

import android.content.Context;
import android.text.TextUtils;

import com.library.base.base.BaseApi;
import com.library.base.utils.SdkPreference;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 *逻辑处理基类
 * @Author: jerome
 * @Date: 2018-01-30
 */
public class BasePresenterImpl<V> {

    protected Context mContext;
    protected SdkPreference mPreferenceUtils;
    private WeakReference<V> weakRefView;
    private BaseApi mApi;

    public BasePresenterImpl(Context context) {
        mContext = context;
        mPreferenceUtils = SdkPreference.getInstance();
    }

    /**
     * 传入view,必传
     * @param view
     */
    public void attach(V view){
        weakRefView = new WeakReference<V>(view);
    }

    /**
     * 销毁view，防止内存泄露
     */
    public void detach() {
        if(isAttach()) {
            weakRefView.clear();
            weakRefView = null;
        }
    }
    public V getView(){
        return isAttach()?weakRefView.get():null;
    }

    /**
     *  是否存在view
     * @return
     */
    protected boolean isAttach() {
        return weakRefView != null &&
                weakRefView.get() != null;
    }

    protected BaseApi getApi() {
        if (mApi == null) {
            mApi = BaseApi.getInstance(mContext);
        }
        return mApi;
    }

    protected boolean isEmpty(String val) {
        return val == null || TextUtils.isEmpty(val.trim());
    }

    protected <T> boolean isEmpty(List<T> data) {
        return data == null || data.isEmpty();
    }

}
