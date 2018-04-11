package com.library.base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.library.base.BuildConfig;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;


/**
 * 数据存储
 * @author : jerome
 * @version : 2017-09-20
 */
public class SdkPreference {

    private static final String  KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_TOKEN = "token";
    public static final String REMOTEID = "remoteId";
    private static final String KEY_USER_INFO = "userInfo";
    private static final String KEY_COOKIE= "cookie";
    private static SdkPreference instance;
    private SharedPreferences sp;


    /**
     * application 初始化
     * @param context
     * @return
     */
    public static SdkPreference instance(Context context) {
        if (instance == null) {
            instance = new SdkPreference(context);
        }
        return instance;
    }


    public static SdkPreference getInstance() {
        return instance;
    }

    public SdkPreference(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public void saveToken(String token) {
        putString(KEY_TOKEN, token);
    }

    public String getToken() {
        return getString(KEY_TOKEN, null);
    }

    public void saveRefreshToken(String token) {
        putString(KEY_REFRESH_TOKEN, token);
    }

    public String getRefreshToken() {
        return getString(KEY_REFRESH_TOKEN, null);
    }


    /**
     * 保存cookie
     */
    public void saveCookie(List<Cookie> cookies){
        HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
        cookieStore.put(BuildConfig.APP_BASE_RUL+"shiro-2", cookies);
        putString(KEY_COOKIE,new Gson().toJson(cookieStore));
    }

    /**
     * 获得cookie
     * @return
     */
    public List<Cookie> getCookie(){
        Type type = new TypeToken<HashMap<String, List<Cookie>>>() {}.getType();
        HashMap<String, List<Cookie>> cookieStore =  new Gson().fromJson(getString(KEY_COOKIE,""),type);
        if (null == cookieStore) return null;
        return cookieStore.get(BuildConfig.APP_BASE_RUL+"shiro-2");
    }


    /**
     * 清除用户信息 - 退出登录
     */
    public void clearUserInfo() {
        SharedPreferences.Editor e = sp.edit();
        e.remove(KEY_USER_INFO);
        e.remove(KEY_TOKEN);
        e.remove(KEY_REFRESH_TOKEN);
        e.apply();
    }



    public void clearValue(String key) {
        sp.edit().remove(key).apply();
    }

    public void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defa) {
        return sp.getBoolean(key, defa);
    }

    public void putString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public String getString(String key, String defau) {
        return sp.getString(key, defau);
    }

    public void putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public void putLong(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    public int getInt(String key, int defa) {
        return sp.getInt(key, defa);
    }

    public long getLong(String key, long defa) {
        return sp.getLong(key, defa);
    }

    public void clearData() {
        sp.edit().clear().apply();
    }

    public void remove(String key) {
        sp.edit().remove(key).apply();
    }

}
