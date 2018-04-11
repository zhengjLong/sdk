package com.library.base.utils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;

/**
 * 后台service+glide下载图片
 */

public class ImgDownLoadService extends IntentService {
    public static final String BUNDLE_IMG_URL = "imgUrl";
    public static final String BUNDLE_IMG_DIR = "saveDir";
    public static final String BUNDLE_IMG_NAME = "fileName";


    public ImgDownLoadService() {
        super("ImgDownLoadService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ImgDownLoadService(String name) {
        super(name);
    }


    /**
     *
     * @param context
     * @param url 图片下载路径
     * @param fileDir 下载的保存的文件夹
     * @param fileName 下载的文件名不包括后缀名
     * @return
     */
    public static Intent getIntent(Context context, String url, String fileDir, String fileName) {
        Intent intent = new Intent(context, ImgDownLoadService.class);
        intent.putExtra(BUNDLE_IMG_URL, url);
        intent.putExtra(BUNDLE_IMG_DIR, fileDir);
        intent.putExtra(BUNDLE_IMG_NAME, fileName);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String url = intent.getStringExtra(BUNDLE_IMG_URL);
        String fileDir = intent.getStringExtra(BUNDLE_IMG_DIR);
        final String fileName = intent.getStringExtra(BUNDLE_IMG_NAME);//这里的文件名是indexManager里面的sp_file 不带.png后缀
        if (!TextUtils.isEmpty(url)) {
            new Thread(new DownLoadImageRun(getBaseContext(), url, fileDir, fileName, new ImageDownLoadCallBack() {
                @Override
                public void onDownLoadSuccess(File file) {
                    Logcat.INSTANCE.e("图片下载成功！path:" + file.getAbsolutePath());
                    SdkPreference.getInstance().putString(fileName,file.getAbsolutePath());
                }

                @Override
                public void onDownLoadFailed() {
                    Logcat.INSTANCE.e("图片下载失败！url:" + url);
                }
            })).start();
        } else {
            Logcat.INSTANCE.e("图片下载失败！url:null");
        }

    }

    static class DownLoadImageRun implements Runnable {
        private String mDirName;
        private String mFileName;
        private String mUrl;
        private ImageDownLoadCallBack mCallBack;
        private Context mContext;

        DownLoadImageRun(Context context, String url
                , String dirName, String fileName, ImageDownLoadCallBack callBack) {
            this.mUrl = url;
            this.mDirName = dirName;
            this.mFileName = fileName;
            this.mCallBack = callBack;
            this.mContext = context;
        }

        @Override
        public void run() {
            File cacheFile = null;
            try {
                Bitmap bmp = BitmapUtil.drawable2Bitmap(Glide.with(mContext).load(mUrl).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get());
                if (bmp != null) {
                    // 在这里执行图片保存方法
                    BitmapUtil.savePicture(mDirName + mFileName + ".png", bmp);
                    cacheFile = new File(mDirName + mFileName + ".png");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logcat.INSTANCE.e("图片下载失败！exception:" + e.getMessage());
            } finally {
                if (cacheFile != null && cacheFile.exists()) {
                    if (mCallBack != null)
                        mCallBack.onDownLoadSuccess(cacheFile);
                } else {
                    if (mCallBack != null)
                        mCallBack.onDownLoadFailed();
                }
            }
        }
    }

    public interface ImageDownLoadCallBack {

        void onDownLoadSuccess(File file);

        void onDownLoadFailed();
    }
}
