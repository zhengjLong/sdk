package com.library.base.net;

import android.os.Handler;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.library.base.utils.SdkPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;


/**
 * HTTP响应
 *
 * @Author: jerome
 * @Date: 2017-09-20
 */
public abstract class BaseHttpResponse<T> implements Callback {

    private final Handler mHandler;

    private HttpCallback<T> mListener;

    BaseHttpResponse(HttpCallback<T> callback, Handler handler) {
        this.mListener = callback;
        mHandler = handler;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        Log.d("HTTP","接口请求失败：" + call.request().url().toString());
        notifyFailure(ApiErrorCode.ERROR_NET_WORK);
    }


    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {

//            请求失败
            if (faileCheck(call, response)) return;

            String result = response.body().string();

//            数据打印
            logInfo(call, result);

//            返回码
            JSONObject json = new JSONObject(result);
            int statusCode = json.getInt("code");


            // 登录过期
            if (loginTimeCheck(statusCode)) return;

//            返回结果
            setResult(result, json, statusCode);

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            notifyFailure(ApiErrorCode.ERROR_JSON_EXCEPTION);
        } catch (Exception e) {
            e.printStackTrace();
            notifyFailure(ApiErrorCode.ERROR_UNKNOWN);
        }
    }

    /**
     * 请求失败
     * @param call
     * @param response
     * @return
     */
    private boolean faileCheck(Call call, Response response) {
        if (mListener == null || mHandler == null) {
            Log.d("HTTP"," 接口调用成功，但是没有回调监听。listener = " + mListener + "; handler = " + mHandler);
            return true;
        }
        if (!response.isSuccessful()) {
            Log.d( "HTTP","请求失败：" + call.request().url() + "\n" + response);
            notifyFailure(ApiErrorCode.ERROR_NET_WORK);
            try {
                logInfo(call,response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
                notifyFailure(ApiErrorCode.ERROR_NET_WORK);
            }
            return true;
        }
        return false;
    }

    /**
     * 结果回调
     * @param result
     * @param json
     * @param statusCode
     * @throws JSONException
     */
    private void setResult(String result, JSONObject json, int statusCode) throws JSONException {
        String msg = json.has("msg") ? json.getString("msg") : null;
        if (statusCode > 0) {
            onParseDataJson(msg, result);
        } else {
            ApiErrorCode errorCode = ApiErrorCode.ERROR_UNKNOWN;
            // 一些中文提示的转换
            if ("server error".equalsIgnoreCase(msg)) {
                errorCode = ApiErrorCode.ERROR_SERVER_EXCEPTION;
                msg = errorCode.getMessage();
            }
            notifyFailure(errorCode, msg);
        }
    }

    /**
     * 是否登录过期
     * @param statusCode
     * @return
     */
    private boolean loginTimeCheck(int statusCode) {
        if (statusCode == ApiErrorCode.ERROR_LOGIN_INVALID.getErrorCode() || statusCode == ApiErrorCode.ERROR_TOKEN_INVALID.getErrorCode()) {
            SdkPreference.getInstance().clearUserInfo(); // 清除登录信息
            notifyFailure(ApiErrorCode.ERROR_LOGIN_INVALID);
            return true;
        }
        return false;
    }

    /**
     * 解析DATA 里面的JSON
     *
     * @param msg      接口的提示信息
     * @param dataJson json 字符串
     */
    protected void onParseDataJson(String msg, String dataJson) {
    }


    /**
     * 通知错误
     *
     * @param errorCode 错误代码
     */
    void notifyFailure(final ApiErrorCode errorCode, final String msg) {
        if (mHandler == null || mListener == null) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onFailure(errorCode.getErrorCode(), msg == null ? errorCode.getMessage() : msg);
            }
        });
    }

    void notifyFailure(ApiErrorCode errorCode) {
        notifyFailure(errorCode, null);
    }


    void notifySuccess(final T data) {
        if (mListener == null || mHandler == null) {
            Log.d( "HTTP"," 接口调用成功，但是没有回调监听。listener = " + mListener + "; handler = " + mHandler);
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onSuccess(data);
            }
        });
    }


    void logInfo(Call call, String response) {
        Log.d("HTTP","==============================================================================");
        Request request = call.request();
        Log.d( "HTTP",request.url().toString());
        Log.d("HTTP","-----------------------------------");
        Log.d("HTTP","请求头：");
        Log.d("HTTP",request.headers().toString());
        try {
            if (request.body() == null) {
                Log.d("HTTP", "无参数");
            } else {
                Buffer sink = new Buffer();
                request.body().writeTo(sink);
                String body = sink.readString(Charset.forName("UTF-8"));
                Log.d("HTTP","-----------------------------------");
                Log.d("HTTP","参数信息：");
                String [] params = body.split("&");
                for (String item : params)
                Log.d("HTTP",item+"\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("HTTP", "-----------------------------------");
        Log.d("HTTP","返回结果：\n");

        // 超出大小
        int maxLength = 3 * 1024;
        if (response.length() > maxLength) {
            for (int i = 0; i < response.length(); i += maxLength) {
                int len = i + maxLength;
                if (len < response.length())
                    Log.d("HTTP", response.substring(i, len));

                else
                    Log.d("HTTP",response.substring(i, response.length()));
            }
        } else {
            Log.d("HTTP",response);
        }

        Log.d("HTTP","-----------------------------------");

    }
}
