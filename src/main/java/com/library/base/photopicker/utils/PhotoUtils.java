package com.library.base.photopicker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Pattern;

import rx.functions.Action1;


/**
 * 图片选择器所需工具类
 */
public class PhotoUtils {

    /**
     * 判断外部存储卡是否可用
     * @return
     */
    public static boolean isExternalStorageAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    public static final int getHeightInPx(Context context) {
        final int height = context.getResources().getDisplayMetrics().heightPixels;
        return height;
    }

    public static final int getWidthInPx(Context context) {
        final int width = context.getResources().getDisplayMetrics().widthPixels;
        return width;
    }

    public static final int getHeightInDp(Context context) {
        final float height = context.getResources().getDisplayMetrics().heightPixels;
        int heightInDp = px2dip(context, height);
        return heightInDp;
    }

    public static final int getWidthInDp(Context context) {
        final float width = context.getResources().getDisplayMetrics().widthPixels;
        int widthInDp = px2dip(context, width);
        return widthInDp;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据string.xml资源格式化字符串
     * @param context
     * @param resource
     * @param args
     * @return
     */
    public static String formatResourceString(Context context, int resource, Object... args) {
        String str = context.getResources().getString(resource);
        if(TextUtils.isEmpty(str)) {
            return null;
        }
        return String.format(str, args);
    }

    /**
     * 检查是否是网络链接
     * @param url
     * @return
     */
    public static boolean checkURL(String url){
        if(isNull(url)){
            return false;
        }
        String regular="(http|https)://[\\S]*";
        return Pattern.matches(regular, url);
    }

    /**
     * 根据路径从sd卡读取位图
     *
     * @param filePath
     * @return
     */
    public static Bitmap decodeBitmapSd(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        Bitmap bitmap = null;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            FileDescriptor fileDescriptor = inputStream.getFD();
            bitmap = decodeBitmap(fileDescriptor);
            inputStream.close();
        } catch (FileNotFoundException e) {
            Log.e("FileNotFound", filePath + "not found");
        } catch (IOException e) {
            Log.e("IOException", filePath + "read error");
        }
        return bitmap;
    }

    /**
     * 图片读取
     *
     * @param desc
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Bitmap decodeBitmap(FileDescriptor desc) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 1;
        try {
            opts.inPurgeable = true;
            opts.inInputShareable = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeFileDescriptor(desc, null, opts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 判断对象是否为空
     */
    public static boolean isNull(Object strObj) {
        String str = strObj + "";
        if (!"".equals(str) && !"null".equals(str)) {
            return false;
        }
        return true;
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
     * 获取bitmap宽高数组
     * @return 0：宽 1：高
     */
    public static int[] getBitmapWidthAndHeight(String file){
        int [] size = new int[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file,options);
        size[0] = options.outWidth;
        size[1] = options.outHeight;
        options.inJustDecodeBounds = false;
        return size;
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
     * 获取拍照相片存储文件
     * @param context
     * @return
     */
    public static File createFile(Context context){
        File file;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            String timeStamp = String.valueOf(new Date().getTime());
            file = new File(Environment.getExternalStorageDirectory() +
                    File.separator + timeStamp+".jpg");
        }else{
            File cacheDir = context.getCacheDir();
            String timeStamp = String.valueOf(new Date().getTime());
            file = new File(cacheDir, timeStamp+".jpg");
        }
        return file;
    }

}
