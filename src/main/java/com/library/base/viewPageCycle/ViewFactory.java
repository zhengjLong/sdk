package com.library.base.viewPageCycle;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
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

    public static void showImage(Context context, String url, ImageView view, final ImageLoaderListener listener) {
        DrawableTypeRequest<String> loader = Glide.with(context).load(url);
        if (listener != null) {
            loader.listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    Logcat.INSTANCE.e("图片加载失败：" + model);
                    listener.onImageLoaderFailed(model, e);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    listener.onImageLoaderSuccess(model);
                    return false;
                }
            });
        }
        loader.into(view);
    }

    public static void showImage(Context context, String url, ImageView view) {
        showImage(context, url, view, null);
    }
}
