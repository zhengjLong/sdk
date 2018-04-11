package com.library.base.viewPageCycle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.library.base.R;
import com.library.base.utils.CheckUtil;
import com.library.base.utils.Logcat;

import static com.bumptech.glide.Glide.with;

/**
 * ImageView创建工厂
 */
public class ViewFactory {

    public static interface ImageLoaderListener {
        void onImageLoaderSuccess(String url);

        void onImageLoaderFailed(String url, Exception e);
    }

    /**
     * 获取ImageView视图的同时加载显示url
     *
     * @return
     */
    public static ImageView getImageView(Context context, String url) {
        ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(
                R.layout.view_image, null);
        if (!CheckUtil.isImgUrl(url)) {
            with(context).load(Integer.valueOf(url)).into(imageView); // .bitmapTransform(new GlideRoundTransform(context, 10))
        } else {
            with(context).load(url).into(imageView);
        }
        return imageView;
    }

    @SuppressLint("CheckResult")
    public static void showImage(Context context, final String url, ImageView view, final ImageLoaderListener listener) {
        RequestBuilder<Drawable> loader = Glide.with(context).load(url);
        if (listener != null) {
            loader.listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Logcat.INSTANCE.e("图片加载失败：" + model);
                    listener.onImageLoaderFailed(url, e);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    listener.onImageLoaderSuccess(url);
                    return false;
                }
            });
        }
        loader.into(view);
    }
}
