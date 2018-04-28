package com.library.base.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.library.base.AppRoute;
import com.library.base.utils.SdkUtil;

/**
 * Application类基类
 *
 */
public abstract class BaseApplication extends Application {

    private AppActivityActivityLifecycle mActivityLifecycle;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mActivityLifecycle = new AppActivityActivityLifecycle();
        registerActivityLifecycleCallbacks(mActivityLifecycle);
        SdkUtil.initSdk(context);
        initData();
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 子类数据初始化
     */
    public abstract void initData();

    class AppActivityActivityLifecycle implements ActivityLifecycleCallbacks {

        private boolean mActivityActiveCount;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            // 压栈
            AppRoute.pushActivity(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            mActivityActiveCount = true;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            mActivityActiveCount = false;
        }

        @Override
        public void onActivityStopped(Activity activity) {
            mActivityActiveCount = false;
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            mActivityActiveCount = false;
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            mActivityActiveCount = false;
            // 移除
            AppRoute.removeActivity(activity);
        }

        public boolean isActive() {
            return mActivityActiveCount;
        }
    }

    /**
     * 当前APP是否在前台运行着
     *
     * @return
     */
    public boolean isActive() {
        return mActivityLifecycle.isActive();
    }


}
