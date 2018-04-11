package com.library.base.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.library.base.BuildConfig;
import com.library.base.R;
import com.library.base.utils.Logcat;
import com.library.base.utils.NetworkUtil;
import com.library.base.utils.SdkUtil;
import com.library.base.utils.SignUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 请求处理
 *
 * @Author: jerome
 * @Date: 2017-09-20
 */
public class HttpEngine {

    private static String BASE_URL = BuildConfig.APP_BASE_RUL;
    private String addInterceptor = BuildConfig.APP_ADD_INTERCEPTOR;
    private String signParams = BuildConfig.APP_SIGN_PARAMS;
    private String postKeyValue = BuildConfig.APP_POST_KEY_VALUE;//默认post键值对
    private static String APP_SECRET;

    private static final int CONNECT_TIME_OUT = 60;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private static WeakReference<HttpEngine> sInstance;
    private Context mContext;
    private Handler mHandler;
    private OkHttpClient mOkHttpClient;

    private HttpEngine(Context context) {
        this.mContext = context.getApplicationContext();
        mOkHttpClient = buildHttpsClient();
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 创建Http Client
     *
     * @return
     */
    private OkHttpClient buildHttpsClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        addCertificate(builder);

        addInterceptor(builder);

        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS).writeTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
        return builder.build();
    }

    /**
     * 配置文件添加拦截器
     *
     * @param builder
     */
    private void addInterceptor(OkHttpClient.Builder builder) {
        if ("1".equals(addInterceptor))
            builder.addInterceptor(new TokenInterceptor(mContext));
    }

    /**
     * 配置文件添加证书
     *
     * @param builder
     */
    private void addCertificate(OkHttpClient.Builder builder) {
        String addCertificate = BuildConfig.APP_ADD_CERTIFICATE;
        if ("1".equals(addCertificate)) {
            try {
                InputStream inputStream = mContext.getResources().openRawResource(R.raw.domain);
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                Certificate certificate = certificateFactory.generateCertificate(inputStream);
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
                keyStore.setCertificateEntry("trust", certificate);

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
                builder.sslSocketFactory(sslContext.getSocketFactory());
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession sslSession) {
                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取实例
     */
    public synchronized static HttpEngine getInstance(Context context) {
        if (TextUtils.isEmpty(BASE_URL)) {
            throw new NullPointerException("请在application中初始化HTTP请求。");
        }
        if (sInstance == null || sInstance.get() == null) {
            sInstance = new WeakReference<>(new HttpEngine(context));
        }
        return sInstance.get();
    }

    /**
     * 初始化一些参数，请在application 中初始化。
     */
    public static void init(Context applicationContext) {
        Properties properties = SdkUtil.getProperties(applicationContext);
        if (properties == null) {
            Logcat.INSTANCE.d("API 信息初始化失败！");
            return;
        }
        APP_SECRET = properties.getProperty("APP_SECRET");
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    /**
     * 取消所有请求
     */
    public void cancel() {
        this.mOkHttpClient.dispatcher().cancelAll();
    }

    /**
     * 对map 参数签名后返回
     *
     * @return 签好名的sign
     */
    public String signMap(String url, Map<String, String> map) {
        if (map == null) return null;

        // 1、对data参数进行重新排序
        Map<String, String> sortMap = SignUtils.sort(map);

        // 2、拼接参数：key1Value1key2Value2
        String urlParams = SignUtils.groupStringParam(sortMap);
//        Log.i("rae", "URL拼接参数：" + urlParams);

        // 3、拼接准备排序： stringURI + stringParams + AppSecret
        String signString = url + urlParams + APP_SECRET;

        // 4、私钥签名
        String sign = SignUtils.encryptHMAC(APP_SECRET, signString);
        if (!TextUtils.isEmpty(sign)) {
            sign = sign.toLowerCase();
        }

        return sign;
    }

    /**
     * POST 参数签名处理
     */
    private String paramsToJson(String url, Map<String, String> map) {
        if (map == null) return "{}";
//        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, String> entry = iterator.next();
//            if (entry.getValue() == null)
//                iterator.remove();
//        }
//        配置实现验签
        if ("1".equals(signParams)) {
            PostParams param = new PostParams();
            param.sign = signMap(url, map);
            param.data = map;
            return new Gson().toJson(param);
        } else {
            return new Gson().toJson(map);
        }

    }

    /**
     * GET  参数签名处理
     */
    private String signParamsToUrlParams(String url, Map<String, String> map) {
        if ("1".equals(signParams)) {
            signMap(url, map);
        }
        return SignUtils.toStringParams(map, true);
    }

    /**
     * GET 请求
     *
     * @param urlPath      短路径
     * @param paramMap     参数
     * @param classOfT     返回实体
     * @param httpCallback 回调
     * @param <T>          实体
     */
    public <T> void get(String urlPath, Map<String, String> paramMap, Class<T> classOfT, HttpCallback<T> httpCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append(BASE_URL);
        sb.append(urlPath);

        if (paramMap != null && paramMap.size() > 0) {
            sb.append("?").append(signParamsToUrlParams(urlPath, paramMap));
        }
        sendRequest(new Request.Builder().url(sb.toString()).build(), new HttpResponseCallBack<>(classOfT, httpCallback, mHandler));
    }


    /**
     * POST 请求，返回TEXT 类型数据
     *
     * @param fullUrl  全路径
     * @param params   参数
     * @param callback 回调
     */
    public void post(String fullUrl, Map<String, String> params, HttpCallback<String> callback) {
        Request builder = getPostBuilder(fullUrl, params);
        sendRequest(builder, new HttpTextResponseCallBack(callback, mHandler));
    }

    /**
     * POST 请求
     *
     * @param urlPath      短路径
     * @param paramMap     参数
     * @param classOfT     返回实体
     * @param httpCallback 回调
     * @param <T>          实体
     */
    public <T> void post(String urlPath, Map<String, String> paramMap, Class<T> classOfT, HttpCallback<T> httpCallback) {
        Request builder = getPostBuilder(urlPath, paramMap);
        sendRequest(builder, new HttpResponseCallBack<>(classOfT, httpCallback, mHandler));
    }

    /**
     * 同步的POST 请求
     *
     * @param <T>    实体
     * @param url    短路径
     * @param params 参数
     * @return 返回原始数据
     */
    public <T> ApiResponseModel<T> syncPost(String url, Map<String, String> params, Class<T> cls) {
        Request builder = getPostBuilder(url, params);
        return sendSyncRequest(builder, cls);
    }

    /**
     * 获取POST 请求
     *
     * @param urlPath
     * @param paramMap
     * @return
     */
    public Request getPostBuilder(String urlPath, Map<String, String> paramMap) {
        String url = urlPath.startsWith("http") ? urlPath : BASE_URL + urlPath;
        RequestBody body = addPostValue(urlPath, paramMap);

        return new Request.Builder().post(body).url(url).build();
    }

    /**
     * 使用key-value或json
     *
     * @param urlPath
     * @param paramMap
     * @return
     */
    private RequestBody addPostValue(String urlPath, Map<String, String> paramMap) {
        if ("1".equals(postKeyValue)) {
            FormBody.Builder body = new FormBody.Builder();
            if (null != paramMap){
                for (Map.Entry<String, String> item : paramMap.entrySet()) {
                    String value = item.getValue();
//                value = URLEncoder.encode(value);
                    body.add(item.getKey(), value);
                }
            }
            return body.build();
        } else {
            String json = paramsToJson(urlPath, paramMap);
            return RequestBody.create(JSON_MEDIA_TYPE, json);
        }
    }


    /**
     * 同步方式发请求
     *
     * @param builder
     * @param cls
     * @param <T>
     * @return
     */
    private <T> ApiResponseModel<T> sendSyncRequest(Request builder, Class<T> cls) {
        ApiResponseModel<T> responseModel = new ApiResponseModel<>();
        Gson gson = new Gson();
        if (!NetworkUtil.checkConnection(mContext)) {
            ApiErrorCode code = ApiErrorCode.ERROR_NET_WORK;
            responseModel.setMessage(code.getMessage());
            responseModel.setStatusCode(code.getErrorCode());
            return responseModel;
        }

        Call caller = newCall(builder);
        try {
            Response response = caller.execute();
            if (response.isSuccessful()) {
                String json = response.body().string();
                Logcat.INSTANCE.d("同步请求返回结果：\n" + json);
                return gson.fromJson(json, responseModel.getClass());
            }
        } catch (Exception e) {
            e.printStackTrace();
            ApiErrorCode code = ApiErrorCode.ERROR_UNKNOWN;
            responseModel.setMessage(code.getMessage());
            responseModel.setStatusCode(code.getErrorCode());
        }
        return responseModel;
    }


    /**
     * 最终调用该方法发出请求
     *
     * @param builder
     * @param response
     * @param <T>
     */
    private <T> void sendRequest(Request builder, BaseHttpResponse<T> response) {
        if (!NetworkUtil.checkConnection(mContext)) {
            response.notifyFailure(ApiErrorCode.ERROR_NET_WORK);
            return;
        }

        newCall(builder).enqueue(response); // 发出请求
    }

    private Call newCall(Request builder) {
//        builder.addHeader("android-model", Build.MODEL)
        return mOkHttpClient.newCall(builder);
    }

    /**
     *上传文件
     * @param paramsMap 参数
     * @param callBack 回调
     * @param <T>
     */
    public <T>void upLoadFile(String requestUrl, HashMap<String, Object> paramsMap, final Class<T> mClass, final HttpCallback<T> callBack) {
        try {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            //追加参数
            for (String key : paramsMap.keySet()) {
                Object object = paramsMap.get(key);
                if (!(object instanceof File)) {
                    builder.addFormDataPart(key, object.toString());
                } else {
                    File file = (File) object;
                    builder.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
                }
            }
            RequestBody body = builder.build();
            final Request request = new Request.Builder().url(requestUrl).post(body).build();
            //单独设置参数 比如读取超时时间
            final Call call = mOkHttpClient.newBuilder().writeTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS).build().newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Logcat.INSTANCE.e(e.toString());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFailure(0,"上传失败");
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        Logcat.INSTANCE.e("response ----->" + string);
//                        successCallBack((T) string, callBack);
                        final T data = new Gson().fromJson(string, mClass);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onSuccess(data);
                            }
                        });
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onFailure(0,"上传失败");
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            Logcat.INSTANCE.e(e.toString());
        }
    }
}
