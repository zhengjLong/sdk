package com.library.base.mvpDemo.model;

import android.content.Context;

import com.library.base.base.BaseApi;
import com.library.base.utils.SdkPreference;


/**
 * model 基类
 * @author : jerome
 */

public class BaseModelImpl {

    private Context mContext;
    public SdkPreference mPreferenceUtils;
    private BaseApi mApi;


    public BaseModelImpl(Context context) {
        mContext = context;
        mPreferenceUtils = SdkPreference.getInstance();
    }

    /**
     * 提供api请求实例
     * @return
     */
    protected BaseApi getApi() {
        if (mApi == null) {
            mApi = BaseApi.getInstance(mContext);
        }
        return mApi;
    }
}
