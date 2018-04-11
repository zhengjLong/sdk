package com.library.base.photopicker;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 重写onInterceptTouchEvent解决photoview 缩放奔溃问题
 * @Author jerome
 * @Date 2017/5/17
 */

public class PhotoViewViewPager extends ViewPager {

    public PhotoViewViewPager(Context context) {
        super(context);
    }

    public PhotoViewViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return false;
    }

}
