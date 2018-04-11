package com.library.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: jerome
 * @Date: 2017-09-20
 */
public class NetworkUtil {



    /**
     * 网络连接状态
     *
     * @param context
     * @return
     */
    public static boolean checkConnection(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * wifi是否连接
     *
     * @param context
     * @return
     */
    public static boolean checkWifiConnect(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    /**
     * 手机网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean checkGpsConnect(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    /**
     * 网络连接类型
     *
     * @param context
     * @return
     */
    public static int getConnectionType(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobileNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifiNetworkInfo != null && wifiNetworkInfo.isAvailable()) {
            return ConnectivityManager.TYPE_WIFI;
        } else if (mobileNetworkInfo != null && mobileNetworkInfo.isAvailable()) {
            return ConnectivityManager.TYPE_MOBILE;
        } else {
            return -1;
        }
    }

    /**
     * 打开wifi
     *
     * @param context
     */
    public static void openWifi(Context context, boolean enabled) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enabled);
    }

    /**
     * 打开移动网络
     *
     * @param context
     */
    public static void openMobileGps(Context context, boolean enabled) {
        ConnectivityManager conMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Class<?> conMgrClass = null;
        Field iConMgrField = null;
        Object iConMgr = null;
        Class<?> iConMgrClass = null;
        Method setMobileDataEnabledMethod = null;
        try {
            conMgrClass = Class.forName(conMgr.getClass().getName());
            iConMgrField = conMgrClass.getDeclaredField("mService");
            iConMgrField.setAccessible(true);
            iConMgr = iConMgrField.get(conMgr);
            iConMgrClass = Class.forName(iConMgr.getClass().getName());
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled",
                    Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

