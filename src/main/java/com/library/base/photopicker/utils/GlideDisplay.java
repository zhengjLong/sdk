package com.library.base.photopicker.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.library.base.R;

import java.io.File;


/**
 * 图片加载,加载的图片不能设置缩放类型
 */
public class GlideDisplay {


    /*由于是静态对象3个option需要独立开来*/
    private static RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.color.colorTextStyleA9)
            .error(R.color.colorTextStyleA9)
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    private static RequestOptions optionsCircle = new RequestOptions()
            .centerCrop()
            .placeholder(R.color.colorTextStyleA9)
            .error(R.color.colorTextStyleA9)
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    private static RequestOptions optionsRound = new RequestOptions()
            .centerCrop()
            .placeholder(R.color.colorTextStyleA9)
            .error(R.color.colorTextStyleA9)
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.ALL);


    /**
     * 正常图片加载File
     * @param iv
     * @param file
     */
    public static void display(ImageView iv, File file) {
        Glide.with(iv.getContext()).load(file).apply(options).into(iv);
    }


    /**
     * 正常图片加载resId
     * @param iv
     * @param resId
     */
    public static void display(ImageView iv, int resId) {
        Glide.with(iv.getContext()).load(resId).apply(options)
                .into(iv);
    }


    /**
     * 正常图片加载bitmap
     * @param iv
     * @param bitmap
     */
    public static void display(ImageView iv, Bitmap bitmap) {
        Glide.with(iv.getContext()).load(bitmap).apply(options)
                .into(iv);
    }


    /**
     * 加载图形图片
     * @param iv
     * @param url
     */
    public static void displayCircle(ImageView iv,String url) {
        Glide.with(iv.getContext()).load(url).apply(getOptionsCircle())
                .into(iv);
    }

    /**
     * 加载图角图片
     * @param iv
     * @param url
     * @param radius
     */
    public static void displayRound(ImageView iv,String url,int radius) {
        Glide.with(iv.getContext()).load(url).apply(getOptionsRound(iv.getContext(),radius))
                .into(iv);
    }

    private static RequestOptions getOptionsCircle (){
        return  optionsCircle.circleCrop();
    }

    private static RequestOptions getOptionsRound (Context context,int radius){
        return optionsRound.transform(new GlideRoundTransform(context,radius));
    }

}
