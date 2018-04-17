package com.library.base.photopicker.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.library.base.R;

import java.io.File;


/**
 * 图片加载
 */
public class GlideDisplay {


//    默认图片加载设置
    private static RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.color.colorTextStyleA9)
            .error(R.color.colorTextStyleA9)
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.ALL);


    /**
     * 加载File
     * @param iv
     * @param file
     */
    public static void display(ImageView iv, File file) {
        Glide.with(iv.getContext()).load(file).apply(options).into(iv);
    }

    /**
     * 加载资源id路径
     * @param iv
     * @param resId
     */
    public static void display(ImageView iv, int resId) {
        Glide.with(iv.getContext()).load(resId)
                .into(iv);
    }
}
