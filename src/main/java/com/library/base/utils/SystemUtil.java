package com.library.base.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import com.library.base.DataCallBack;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * 系统功能
 * @Author: jerome
 * @Date: 2018-01-26
 */

public class SystemUtil {


    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";


    /**
     * 判断当前应用程序处于前台还是后台
     * 需要添加权限: <uses-permission android:name="android.permission.GET_TASKS" />
     */
    public static boolean isApplicationBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                Logcat.INSTANCE.e("isBackground: true");
                return true;
            }
        }
        Logcat.INSTANCE.e("isBackground: true");
        return false;
    }

    /**
     * 判断手机是否处理睡眠
     *
     * @param context
     * @return
     */
    public static boolean isSleeping(Context context) {
        KeyguardManager kgMgr = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean isSleeping = false;
        if (kgMgr != null) {
            isSleeping = kgMgr.inKeyguardRestrictedInputMode();
            Logcat.INSTANCE.e(isSleeping ? "手机睡眠中.." : "手机未睡眠...");
        }
        return isSleeping;
    }

    /**
     * 安装apk
     *
     * @param context
     * @param file
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("application/vnd.android.package-archive");
        intent.setData(Uri.fromFile(file));
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    /**
     * 判断是否为手机
     *
     * @param context
     * @return
     * @author wangjie
     */
    public static boolean isPhone(Context context) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int type = 0;
        if (telephony != null) {
            type = telephony.getPhoneType();
        }
        if (type == TelephonyManager.PHONE_TYPE_NONE) {
            Logcat.INSTANCE.e("Current device is no phone!");
            return false;
        } else {
            Logcat.INSTANCE.e("Current device is phone!");
            return true;
        }
    }

    /**
     * 获得设备的屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getDeviceWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay().getWidth();
    }

    /**
     * 获得设备的屏幕高度
     *
     * @param context
     * @return
     */
    public static int getDeviceHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay().getHeight();
    }

    /**
     * 获取设备id（IMEI）
     *  添加权限<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     * @return "" & String
     */
    public static void getDeviceIMEI(final Context context, final DataCallBack<String> callBack) {
        SdkUtil.getUserPermission(new SdkUtil.PermissionCallBack() {
            @SuppressLint({"MissingPermission", "HardwareIds"})
            @Override
            public void isSuccess(boolean isSuccess) {
                if (isSuccess) {
                    if (isPhone(context.getApplicationContext())) {
                        TelephonyManager telephony = (TelephonyManager) context.getApplicationContext()
                                .getSystemService(Context.TELEPHONY_SERVICE);
                        callBack.callBack(telephony != null ? telephony.getDeviceId() : "");
                    } else {
                        callBack.callBack(Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                                Settings.Secure.ANDROID_ID));
                    }
                }else {
                    callBack.callBack("");
                }
            }
        },context,android.Manifest.permission.READ_PHONE_STATE);
    }

    /**
     * 获取设备mac地址
     *
     * @param context
     * @return
     * @author wangjie
     */
    public static String getMacAddress(Context context) {
        String macAddress;
        WifiManager wifi = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi != null ? wifi.getConnectionInfo() : null;
        macAddress = (info != null) ? info.getMacAddress() : null;
        Logcat.INSTANCE.e("当前mac地址: " + (null == macAddress ? "null" : macAddress));
        if (null == macAddress) {
            return "";
        }
        macAddress = macAddress.replace(":", "");
        return macAddress;
    }

    /**
     * 获取当前应用程序的版本名
     *
     * @return
     */
    public static String getAppVersionName(Context context) {
        String versionName = "1.0";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Logcat.INSTANCE.e(e.getMessage());
        }

        return versionName;
    }

    /**
     * 获取当前应用程序的版本号
     *
     * @return
     */
    public static int getAppVersionCode(Context context) {
        int version = 1;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            Logcat.INSTANCE.e("该应用的版本号: " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Logcat.INSTANCE.e(e.getMessage());
        }

        return version;
    }

    /**
     * 收集设备信息
     *
     * @param context
     */
    private static Properties collectDeviceInfo(Context context) {
        Properties mDeviceCrashInfo = new Properties();
        try {
            // Class for retrieving various kinds of information related to the
            // application packages that are currently installed on the device.
            // You can find this class through getPackageManager().
            PackageManager pm = context.getPackageManager();
            // getPackageInfo(String packageName, int flags)
            // Retrieve overall information about an application package that is installed on the system.
            // public static final int GET_ACTIVITIES
            // Since: API Level 1 PackageInfo flag: return information about activities in the package in activities.
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                // public String versionName The version name of this package,
                // as specified by the <manifest> tag's versionName attribute.
                mDeviceCrashInfo.put(VERSION_NAME, pi.versionName == null ? "not set" : pi.versionName);
                // public int versionCode The version number of this package,
                // as specified by the <manifest> tag's versionCode attribute.
                mDeviceCrashInfo.put(VERSION_CODE, pi.versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Logcat.INSTANCE.e(e.getMessage());
        }
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        // 返回 Field 对象的一个数组，这些对象反映此 Class 对象所表示的类或接口所声明的所有字段
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                // setAccessible(boolean flag)
                // 将此对象的 accessible 标志设置为指示的布尔值。
                // 通过设置Accessible属性为true,才能对私有变量进行访问，不然会得到一个IllegalAccessException的异常
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), field.get(null));
            } catch (Exception e) {
                Logcat.INSTANCE.e("Error while collect crash info"+e.getMessage());
            }
        }

        return mDeviceCrashInfo;
    }

    /**
     * 收集设备信息
     *
     * @param context
     * @return
     */
    public static String collectDeviceInfoStr(Context context) {
        Properties prop = collectDeviceInfo(context);
        Set deviceInfos = prop.keySet();
        StringBuilder deviceInfoStr = new StringBuilder("{\n");
        for (Object item : deviceInfos) {
            deviceInfoStr.append("\t\t\t").append(item).append(":").append(prop.get(item)).append(", \n");
        }
        deviceInfoStr.append("}");
        return deviceInfoStr.toString();
    }

    /**
     * 回到home，后台运行
     *
     * @param context
     */
    public static void goHome(Context context) {
        Logcat.INSTANCE.e("返回键回到HOME，程序后台运行...");
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);

        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(mHomeIntent);
    }

}
