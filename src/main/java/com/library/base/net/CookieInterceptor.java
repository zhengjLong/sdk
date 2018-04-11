package com.library.base.net;

import android.content.Context;

import com.library.base.base.BaseApi;
import com.library.base.utils.Logcat;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Cookie 续期拦截器
 * @author : jerome
 * @version : 2018-04-11
 */

public class CookieInterceptor implements Interceptor {

    private Context mContext;

    public CookieInterceptor(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Response response = chain.proceed(request);


        try {

            ResponseBody responseBody = response.body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = responseBody.contentType();

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
            int rc = obj.has("status") ? obj.getInt("status") : -1;
            if (rc == ApiErrorCode.ERROR_TOKEN_INVALID.getErrorCode()) {
                // 刷新cookie
                String token = refreshToken();
                Request.Builder builder = request.newBuilder();
                response = chain.proceed(builder.build());
                Logcat.INSTANCE.e( "准备重新请求！");
                return response;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Logcat.INSTANCE.e("TOKEN 拦截失败！", e);
        }

        return response;
    }

    /**
     * 同步刷新cookie
     * @return
     */
    private String refreshToken() {

        String newToken = syncRefreshToken();

        Logcat.INSTANCE.e("刷新TOKEN成功：" + newToken);
        return newToken;
    }

    /**
     * 同步方法获取cookie
     *
     */
    private String syncRefreshToken() {
//        TODO 请求获得cookie
        ApiResponseModel data = BaseApi.getInstance(mContext).getRefreshToken("params");
        if (data == null || data.getData() == null) {
            return null;
        }
        if (data.getData() instanceof String ) {
            return data.getData().toString();
        }
        return null;
    }


}
