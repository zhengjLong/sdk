package com.library.base.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.library.base.BuildConfig;
import com.library.base.net.ApiResponseModel;
import com.library.base.net.HttpCallback;
import com.library.base.net.HttpEngine;
import com.library.base.utils.Logcat;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求Api类
 * @author jerome
 */
public class BaseApi {


//    url短路径
    private String baseUrl = BuildConfig.APP_BASE_RUL;
//    上传错误日志
    private String uploadErrorData = baseUrl + "url";
//    获得刷新token
    private String getRefreshToken = baseUrl + "url";

    private HttpEngine httpEngine;
    private static WeakReference<BaseApi> instance;
    private final Context mContext;

    private BaseApi(Context context, Handler handler) {
        mContext = context.getApplicationContext();
        httpEngine = HttpEngine.getInstance(context);
        httpEngine.setHandler(handler);
    }

    public synchronized static BaseApi getInstance(Context context, Handler handler) {
        if (instance == null || instance.get() == null) {
            instance = new WeakReference<>(new BaseApi(context, handler));
        }
        return instance.get();
    }

    /**
     * get default main thread looper server api
     *
     * @param context
     * @return
     */
    public synchronized static BaseApi getInstance(Context context) {
        return getInstance(context, new Handler(Looper.getMainLooper()));
    }



    /**
     * 上传错误日志
     *
     * @param callback
     */
    public void uploadErrorData(String content, HttpCallback<String> callback) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("content", content);
        httpEngine.post(uploadErrorData, paramMap, String.class, callback);
    }
    /**
     * 同步获得刷新token
     */
    public ApiResponseModel getRefreshToken(String refreshToken) {
        Map<String, String> params = new HashMap<>();
        params.put("refreshToken", refreshToken);
        return  httpEngine.syncPost(getRefreshToken, params, null);
    }


}
