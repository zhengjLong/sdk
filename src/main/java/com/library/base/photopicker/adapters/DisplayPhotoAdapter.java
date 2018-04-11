package com.library.base.photopicker.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.library.base.photopicker.utils.PhotoUtils;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * 单张多张本地网络图片的显示
 *
 * @Author jerome
 * @Date 2017/5/17
 */
public class DisplayPhotoAdapter extends PagerAdapter {

    private ArrayList<String> mImagePaths;
    private Context mContext;
    private int mDefaultPic;

    public DisplayPhotoAdapter(Context mContext, ArrayList<String> mImagePaths, int mDefaultPic) {
        this.mContext = mContext;
        this.mImagePaths = mImagePaths;
        this.mDefaultPic = mDefaultPic;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView mPhotoView = new PhotoView(container.getContext());
        mPhotoView.setImageResource(mDefaultPic);

        displayImage(mImagePaths.get(position), mPhotoView);

        container.addView(mPhotoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        initTouchListener(mPhotoView);

        return mPhotoView;
    }

    /**
     * 触摸退出
     *
     * @param mPhotoView
     */
    private void initTouchListener(PhotoView mPhotoView) {
        mPhotoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                ((Activity) mContext).finish();
            }
        });
    }

    /**
     * 图片本地与网络加载
     *
     * @param mImagePath 路径，可本地与网络两种
     * @param mPhotoView
     */
    private void displayImage(String mImagePath, PhotoView mPhotoView) {
        if (PhotoUtils.checkURL(mImagePath)) {
            Glide.with(mContext).load(mImagePath)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(mDefaultPic).into(mPhotoView);
        } else {
            mPhotoView.setImageBitmap(PhotoUtils.decodeBitmapSd(mImagePath));
        }
    }

    @Override
    public int getCount() {
        return null != mImagePaths ? mImagePaths.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
