package com.library.base.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;



/**
 *  程序home与任务键广播监听
 * @author : jerome
 * @date : 2018/6/13 0013
 */
public class HomeStatusUtil {


    private static BroadcastReceiver mReceiver;
    private static boolean hasLock;//是否锁屏

    private static CallBack mcallback;


    /**
     * 注册广播监听
     */
    public static void registerListener(Context context,CallBack callBack) {
        if (null == mReceiver) {
            mReceiver = getReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);//锁屏
            intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);//后台
            context.registerReceiver(mReceiver, intentFilter);
        }
        mcallback = callBack;
    }

    /**
     * 必须注销广播
     */
    public static void unRegisterListener(Context context) {
        if (null != mReceiver)
            context.unregisterReceiver(mReceiver);
        hasLock = false;
        mReceiver = null;
    }




    /**
     * 广播接收
     *
     * @return
     */
    private static BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                        String reason = intent.getStringExtra("reason");
                        if (TextUtils.equals(reason, "homekey")) {
                            //程序到了后台
                            if (null != mcallback)
                            mcallback.leaveApp();
                        } else if (TextUtils.equals(reason, "recentapps")) {
                            //显示最近使用的程序列表
                            if (null != mcallback)
                                mcallback.leaveApp();
                        }
                        break;
                    case Intent.ACTION_SCREEN_ON:
                        hasLock = TextUtils.equals(intent.getAction(), Intent.ACTION_SCREEN_ON);
                        break;
                }
            }
        };
    }

    public interface CallBack{
        void leaveApp();
    }

}

