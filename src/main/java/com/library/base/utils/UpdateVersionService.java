package com.library.base.utils;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.library.base.BuildConfig;
import com.library.base.R;


/**
 * 启动下载的后台服务，通过广播接收下载完成的通知
 */
public class UpdateVersionService extends Service {

    public static final String DOWNLOAD_APK_NAME = BuildConfig.APP_NAME + "_v" + BuildConfig.VERSION_NAME + "_" + BuildConfig.FLAVOR + ".apk";
    private String url;
    //     安卓系统下载类
    private DownloadManager manager;
    //     接收下载完的广播
    private DownloadCompleteReceiver receiver;


    /**
     * 下载地址
     *
     * @param context
     * @param url
     * @return
     */
    public static Intent getIntent(Context context, String url) {
        Intent intent = new Intent(context, UpdateVersionService.class);
        intent.putExtra("url", url);
        return intent;
    }

    /**
     * 初始化下载器
     */
    private void initDownManager() {
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver = new DownloadCompleteReceiver();
        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(
                Uri.parse(url));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        // 设置下载路径和文件名
        down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, DOWNLOAD_APK_NAME);
        down.setDescription(BuildConfig.APP_NAME + "更新");
        // 下载时，通知栏显示途中
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        down.setMimeType("application/vnd.android.package-archive");
        // 设置为可被媒体扫描器找到
        down.allowScanningByMediaScanner();
        // 将下载请求放入队列
        manager.enqueue(down);
        //注册下载广播
        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // 注销下载广播
        if (receiver != null)
            unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = intent.getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            // 调用下载
            SdkUtil.getUserPermission(new SdkUtil.PermissionCallBack() {
                @Override
                public void isSuccess(boolean isSuccess) {
                    if (isSuccess)
                        initDownManager();
                }
            }, getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //意外杀死后，不再重启服务
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 接受下载完成后的intent
     */
    class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //判断是否下载完成的广播
            if (intent.getAction().equals(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //获取下载的文件id
                long downId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                //停止服务并关闭广播
                UpdateVersionService.this.stopSelf();
                //自动安装apk
                installAPK(context, manager.getUriForDownloadedFile(downId));

            }
        }

        /**
         * 安装apk文件
         */
        private void installAPK(Context context, Uri apk) {
            if (apk != null) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);//动作
                intent.addCategory(Intent.CATEGORY_DEFAULT);//类型
                intent.setDataAndType(apk, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }

    }
}
