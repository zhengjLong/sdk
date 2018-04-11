package com.library.base.net;

import android.content.Context;
import android.text.TextUtils;

import com.library.base.BuildConfig;
import com.library.base.base.BaseApi;
import com.library.base.utils.Logcat;
import com.library.base.utils.SdkPreference;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * TOKEN 续期拦截器
 * @Author: jerome
 * @Date: 2017-09-20
 */

public class TokenInterceptor implements Interceptor {

    private Context mContext;

    public TokenInterceptor(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Response response = chain.proceed(request);

        try {
            if (!response.isSuccessful()) {
                return response;
            }

            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = responseBody.contentType();
            if (responseBody.contentLength() <= 0) {
                return response;
            }
            if (contentType != null) {
                try {
                    charset = contentType.charset();
                } catch (UnsupportedCharsetException e) {
                    //Couldn't decode the response body; charset is likely malformed.
                    return response;
                }
            }


            String json = buffer.clone().readString(charset);


            JSONObject obj = new JSONObject(json);
            int rc = obj.has("code") ? obj.getInt("code") : 0;
            if (rc == ApiErrorCode.ERROR_TOKEN_INVALID.getErrorCode()) {
                Logcat.INSTANCE.d( "TOKEN失效，重新获取TOKEN，原来TOKEN = " + SdkPreference.getInstance().getToken() + ";RefreshToken = " + SdkPreference.getInstance().getRefreshToken());
                // 刷新TOKEN，TOKEN刷新错误返回
                String token = refreshToken();
                //  替换Token
                Request.Builder builder = request.newBuilder();
                String flag = request.header("request-flag");
                JSONObject flagObj = new JSONObject(flag);
                flagObj.getJSONObject("client").put("token", token); // 刷新的TOEKN
                builder.header("request-flag", flagObj.toString());

                response = chain.proceed(builder.build());
                Logcat.INSTANCE.d( "准备重新请求！");
                return response;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Logcat.INSTANCE.e("TOKEN 拦截失败！", e);
        }

        return response;
    }

    /**
     * 同步刷新Token
     * @return
     * @throws IOException
     */
    private String refreshToken() throws IOException {
        SdkPreference p = SdkPreference.getInstance();

        String newToken = syncRefreshToken(p.getRefreshToken());

        SdkPreference.getInstance().saveRefreshToken(newToken);
        if (TextUtils.isEmpty(newToken)) {
            Logcat.INSTANCE.e("TOKEN 刷新失败，返回为空，使用上一次Token：" + p.getToken());
            return p.getToken();
        }

        Logcat.INSTANCE.d("刷新TOKEN成功：" + newToken);
        return newToken;
    }

    /**
     * 同步方法获取TOKEN
     *
     * @param refreshToken 登录返回
     */
    public String syncRefreshToken(String refreshToken) {
        ApiResponseModel data = BaseApi.getInstance(mContext).getRefreshToken(refreshToken);
        if (data == null || data.getData() == null) {
            return null;
        }

        if (data.getData() instanceof Map) {
            Map map = (Map) data.getData();
            return map.containsKey("token") ? map.get("token").toString() : null;
        }
        return null;
    }


}
