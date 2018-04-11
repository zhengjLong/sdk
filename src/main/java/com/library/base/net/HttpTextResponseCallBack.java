package com.library.base.net;

import android.os.Handler;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 纯文本类型HTTP 回调
 *
 * @Author: jerome
 * @Date: 2017-09-20
 */
public class HttpTextResponseCallBack extends BaseHttpResponse<String> {

    public HttpTextResponseCallBack(HttpCallback<String> callback, Handler handler) {
        super(callback, handler);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            String result = response.body().string();
            if (!response.isSuccessful()) {
                notifyFailure(ApiErrorCode.ERROR_NET_WORK);
                logInfo(call, result);
                return;
            }
            logInfo(call, result);
            notifySuccess(result);
        } catch (Exception e) {
            e.printStackTrace();
            notifyFailure(ApiErrorCode.ERROR_UNKNOWN);
        }
    }
}
