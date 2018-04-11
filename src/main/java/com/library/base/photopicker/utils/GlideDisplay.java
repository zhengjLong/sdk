package com.library.base.photopicker.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.library.base.R;

import java.io.File;


public class GlideDisplay {
    public static int BG_DEF = R.color.colorTextStyleA9;


    /**
     * 适配第三方自定义ImageView
     *
     * @param builder
     * @param iv
     */
    private static void fitThirdImageView(DrawableRequestBuilder builder, ImageView iv) {
        builder.into(new MyImageViewTarget(iv));
    }

    public static void display(ImageView iv, File file) {
        DrawableRequestBuilder<File> builder = Glide.with(iv.getContext()).load(file).centerCrop()
                .placeholder(BG_DEF)
                .error(BG_DEF)
                .crossFade();

        fitThirdImageView(builder, iv);
    }


    public static void display(ImageView iv, int resId) {
        DrawableRequestBuilder<Integer> builder = Glide.with(iv.getContext()).load(resId).centerCrop()
                .error(BG_DEF)
                .crossFade();
        fitThirdImageView(builder, iv);
    }

    public static void displayGif(ImageView iv, String url) {
        GifRequestBuilder<String> stringGifRequestBuilder = Glide.with(iv.getContext()).load(url).asGif().centerCrop()
                .placeholder(BG_DEF)
                .error(BG_DEF)
                .crossFade();
        stringGifRequestBuilder.into(buildTarget(iv, GifDrawable.class));
    }

    public static class MyImageViewTarget extends ImageViewTarget<GlideDrawable> {
        public MyImageViewTarget(ImageView view) {
            super(view);
        }

        @Override
        protected void setResource(GlideDrawable resource) {
            view.setImageDrawable(resource);
        }

        @Override
        public void setRequest(Request request) {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            view.setTag(glide_tag_id, request);
        }

        @Override
        public Request getRequest() {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            return (Request) view.getTag(glide_tag_id);
        }
    }

    public static class MyGifImageViewTarget extends GlideDrawableImageViewTarget {

        public MyGifImageViewTarget(ImageView view) {
            super(view);
        }

        @Override
        public void setRequest(Request request) {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            view.setTag(glide_tag_id, request);
        }

        @Override
        public Request getRequest() {
            //动态获取id
            int glide_tag_id = view.getContext().getResources().getIdentifier("glide_tag_id", "id", view.getContext().getPackageName());
            return (Request) view.getTag(glide_tag_id);
        }
    }

    @SuppressWarnings("unchecked")
    public static <Z> Target<Z> buildTarget(ImageView view, Class<Z> clazz) {
        if (GlideDrawable.class.isAssignableFrom(clazz)) {
            return (Target<Z>) new MyGifImageViewTarget(view);
        } else if (Bitmap.class.equals(clazz)) {
            return (Target<Z>) new BitmapImageViewTarget(view);
        } else if (Drawable.class.isAssignableFrom(clazz)) {
            return (Target<Z>) new DrawableImageViewTarget(view);
        } else {
            throw new IllegalArgumentException("Unhandled class: " + clazz
                    + ", try .as*(Class).transcode(ResourceTranscoder)");
        }
    }
}
