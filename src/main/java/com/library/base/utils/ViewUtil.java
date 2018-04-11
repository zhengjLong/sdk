package com.library.base.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * view 操作工具
 */
public class ViewUtil {


    /**
     * 获取控件的高度，如果获取的高度为0，则重新计算尺寸后再返回高度
     *
     * @param view
     * @return
     */
    public static int getViewMeasuredHeight(View view) {
        calcViewMeasure(view);
        return view.getMeasuredHeight();
    }

    /**
     * 获取控件的宽度，如果获取的宽度为0，则重新计算尺寸后再返回宽度
     *
     * @param view
     * @return
     */
    public static int getViewMeasuredWidth(View view) {
        calcViewMeasure(view);
        return view.getMeasuredWidth();
    }

    /**
     * 测量控件的尺寸
     *
     * @param view
     */
    public static void calcViewMeasure(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, View.MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
    }


    /**
     * 使用ColorFilter来改变图片亮度
     *
     * @param imageview
     * @param brightness
     */
    public static void changeBrightness(ImageView imageview, float brightness) {
        imageview.setColorFilter(getBrightnessMatrixColorFilter(brightness));
    }
    public static void changeBrightness(Drawable drawable, float brightness) {
        drawable.setColorFilter(getBrightnessMatrixColorFilter(brightness));
    }

    private static ColorMatrixColorFilter getBrightnessMatrixColorFilter(float brightness) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(
                new float[]{
                        1, 0, 0, 0, brightness,
                        0, 1, 0, 0, brightness,
                        0, 0, 1, 0, brightness,
                        0, 0, 0, 1, 0
                });
        return new ColorMatrixColorFilter(matrix);
    }


    /**
     * 设置是否可以点击
     * @param clickable
     * @param views
     */
    public static void setClickable(boolean clickable, View ... views){
        if(views != null && views.length > 0){
            for(View v : views){
                v.setClickable(clickable);
            }
        }
    }

    /**
     * 抖动指定的view
     * @param context
     * @param view 目标
     */
    public static void shake(Context context, View view){
        AnimatorSet animator = new AnimatorSet();
        animator.setDuration(1000);
        animator.playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1, 0.9f, 0.9f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1),
                ObjectAnimator.ofFloat(view, "scaleY", 1, 0.9f, 0.9f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1.1f, 1),
                ObjectAnimator.ofFloat(view, "rotation", 0, -3, -3, 3, -3, 3, -3, 3, -3, 0)
        );
        animator.start();
        return;
    }

    /**
     * 设置字体间距
     * @param view
     * @param size
     */
    public static void applyLetterSpacing(TextView view, float size) {
        StringBuilder builder = new StringBuilder();
        String originalText = view.getText().toString();
        for (int i = 0; i < originalText.length(); i++) {
            builder.append(originalText.charAt(i));
            if (i + 1 < originalText.length()) {
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if (builder.toString().length() > 1) {
            for (int i = 1; i < builder.toString().length(); i += 2) {
                finalText.setSpan(new ScaleXSpan((size + 1) / 10), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        view.setText(finalText, TextView.BufferType.SPANNABLE);
    }


    /**
     * 获得edittext输入字符
     *
     * @param editText
     * @return
     */
    public static String getInputContent(EditText editText) {
        return editText.getText().toString().trim().replace(" ", "");
    }
}