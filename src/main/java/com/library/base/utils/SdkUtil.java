package com.library.base.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.library.base.R;
import com.library.base.WebActivity;
import com.library.base.net.HttpEngine;
import com.library.base.photopicker.DisplayPhotoActivity;
import com.library.base.photopicker.PhotoPickerActivity;
import com.library.base.photopicker.SelectPhotoTypeActivity;
import com.library.base.viewPageCycle.Adverts;
import com.library.base.viewPageCycle.CycleViewPager;
import com.library.base.viewPageCycle.ViewFactory;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import rx.functions.Action1;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * sdk管理类：application 调用initSdk()初始化
 *
 * @Author: jerome
 * @Date: 2017-09-20
 */

public class SdkUtil {


    /**
     * 项目初始化
     *
     * @param context
     */
    public static void initSdk(Context context) {
        SdkPreference.instance(context);
        HttpEngine.init(context);
        FileUtil.INSTANCE.init(context);
        AndroidExceptionLog.init(context);
    }


    public static Properties getProperties(Context context) {
        try {
            Properties properties = new Properties();
            InputStream inputStream = context.getResources().openRawResource(R.raw.environment);
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 打开web页面
     *
     * @param context
     * @param title
     * @param url
     */
    public static void openWebActivity(Context context, String title, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(WebActivity.PARAM_TITLE, title);
        bundle.putString(WebActivity.PARAM_URL, url);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 打开系统图片选择或拍照对话框
     *
     * @param tag 标签,可选
     *            <p>
     *            图片回调方式:
     * @Subscribe(threadMode = ThreadMode.MAIN)
     * public void onSelectPhotoEvent(SelectPhotoEvent event) {}
     */
    public static void selectPhotoEventBusDialog(Context context, String tag) {
        SelectPhotoTypeActivity.start(context, tag, SelectPhotoTypeActivity.CALL_BACK_EVENT_BUS);
    }

    /**
     * 打开系统图片选择或拍照对话框
     *
     * @param tag 标签,可选
     *            <p>
     *            图片回调方式:
     *            Bus.observe<SelectPhotoEvent>()
     *            .subscribe {
     *            }
     *            .registerInBus(this)
     *            销毁：
     *            override fun onDestroy() {
     *            super.onDestroy()
     *            Bus.unregister(this)
     *            }
     */
    public static void selectPhotoBusDialog(Context context, String tag) {
        SelectPhotoTypeActivity.start(context, tag, SelectPhotoTypeActivity.CALL_BACK_BUS);
    }


    /**
     * 打开自定义图片选择 :EventBus 回调，用于
     *
     * @param isShowCamera 是否使用拍照功能
     * @param tag          标签,可选
     * @param num          选择图片个数
     *                     <p>
     *                     图片回调方式:
     * @Subscribe(threadMode = ThreadMode.MAIN)
     * public void onSelectPhotoEvent(SelectImageEvent event) {
     * if (event.selectMediaBeans != null) {}
     * }
     */
    public static void selectPhotoEventBus(Context context, Boolean isShowCamera, int num, String tag) {
        PhotoPickerActivity.start(context, isShowCamera, tag, num, PhotoPickerActivity.CALL_BACK_EVENT_BUS);
    }

    /**
     * 打开自定义图片选择 ： Bus回调，用于kotlin
     *
     * @param isShowCamera 是否使用拍照功能
     * @param tag          标签,可选
     * @param num          选择图片个数
     *                     <p>
     *                     图片回调方式:
     *                     Bus.observe<SelectImageEvent>()
     *                     .subscribe {
     *                     if (it.selectMediaBeans != null) {
     *                     when (it.flag) {}
     *                     }
     *                     }
     *                     .registerInBus(this)
     *                     销毁：
     *                     override fun onDestroy() {
     *                     super.onDestroy()
     *                     Bus.unregister(this)
     *                     }
     */
    public static void selectPhotoBus(Context context, Boolean isShowCamera, int num, String tag) {
        PhotoPickerActivity.start(context, isShowCamera, tag, num, PhotoPickerActivity.CALL_BACK_BUS);
    }

    /**
     * 查看图片
     *
     * @param displayPosition 打开时的显示位置
     * @param defaultPic      默认图片资源,非必填
     */
    public static void displayPhoto(Context context,ArrayList<String> images, int displayPosition, int defaultPic) {
        DisplayPhotoActivity.start(context,images, displayPosition, defaultPic);
    }

    /**
     * 图片压缩
     */
    public static void compressPic(Context context, List<String> paths, String savePath, OnCompressListener listener) {
        Luban.with(context)
                .load(paths)                                   // 传入要压缩的图片列表
                .ignoreBy(100)                                  // 忽略不压缩图片的大小
                .setTargetDir(savePath)                        // 设置压缩后文件存储位置
                .setCompressListener(listener).launch();    //启动压缩
    }

    /**
     * 初始化图片自动轮翻
     * 初始化:
     * <fragment
     * android:id="@+id/cycle_view_page"
     * android:layout_width="match_parent"
     * android:layout_height="96dp"/>
     *
     * CycleViewPager  cycleViewPager = (CycleViewPager) getChildFragmentManager().findFragmentById(R.id.cycle_view_page);
     */
    public static void initCycleView(Context context, List<Adverts> infos, CycleViewPager cycleViewPager, boolean isShowPoint,
                                     boolean showPointCenter, CycleViewPager.ImageCycleViewListener listener) {
        try {
            if (infos == null || infos.size() <= 0) {
                cycleViewPager.hide();
                return;
            }
            cycleViewPager.show();
            List<ImageView> views = new ArrayList<>();
            if (infos.size() > 1) {//超过一张自动轮播
                views.add(ViewFactory.getImageView(context, infos.get(infos.size() - 1).getPicUrl()));
                for (int i = 0; i < infos.size(); i++) {
                    views.add(ViewFactory.getImageView(context, infos.get(i).getPicUrl()));
                }
                views.add(ViewFactory.getImageView(context, infos.get(0).getPicUrl()));
                cycleViewPager.setCycle(true);
                cycleViewPager.setWheel(true);
            } else {
                views.add(ViewFactory.getImageView(context, infos.get(0).getPicUrl()));
                cycleViewPager.setCycle(false);
                cycleViewPager.setWheel(false);
            }
            //是否显示指示器
            cycleViewPager.isShowIndicator(isShowPoint);
            cycleViewPager.setData(views, infos, listener);
            // 设置轮播时间，默认5000ms
            cycleViewPager.setTime(3000);
            //设置圆点指示图标组居中显示，默认靠右
            if (showPointCenter)
                cycleViewPager.setIndicatorCenter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启后台服务下载图片(文件保存在SdkPreference,key = fileName)
     * AndroidManifest:<service android:name=".utils.ImgDownLoadService"/>
     *
     * @param context
     * @param url      图片下载路径
     * @param fileDir  下载的保存的文件夹
     * @param fileName 下载的文件名不包括后缀名
     * @return
     */
    public static void downLoadImage(Context context, String url, String fileDir, String fileName) {
        context.startService(ImgDownLoadService.getIntent(context, url, fileDir, fileName));
    }


    /**
     * 调用服务下载app
     * AndroidManifest: <service android:name=".utils.UpdateVersionService" />
     *
     * @param context
     * @param url
     */
    public static void downApp(Context context, String url) {
        context.startService(UpdateVersionService.getIntent(context, url));
    }

    /**
     * 是否获得用户权限许可
     *
     * @param permission 权限引用ID,edg:Manifest.permission.CAMERA
     * @return 权限是否可用
     */
    public static void getUserPermission(final PermissionCallBack callBack, Context context, String... permission) {
        try {
            RxPermissions.getInstance(context.getApplicationContext()).request(permission).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean permission) {
                    callBack.isSuccess(permission);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callBack.isSuccess(false);
        }
    }


    /**
     * 权限回调
     */
    public interface PermissionCallBack {
        void isSuccess(boolean isSuccess);
    }


}
