package com.library.base.photopicker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.library.base.R;
import com.library.base.photopicker.adapters.DisplayPhotoAdapter;
import com.library.base.photopicker.utils.GlideDisplay;
import com.library.base.photopicker.utils.PhotoUtils;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * 本地图片，网络图片单张多张显示
 *
 * @Author jerome
 * @Date 2017/5/17
 * Bundle:
 * bundle.putStringArray("imagePaths");
 * bundle.putInt("position");初始化图片位置
 * bundle.putInt("defaultPic");非必填
 */
public class DisplayPhotoActivity extends AppCompatActivity {

    private PhotoViewViewPager mViewPager;
    private TextView mPhotoNumber;//多张图片显示时，照片页码
    private int mDefaultPic;//默认的显示图片
    private int mPosition;//当前显示的照片页码
    private ArrayList<String> mImagePaths;//保存显示的图片数组的路径
    private RequestOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_display_photo);
        initData();
        initView();
        initListener();
    }

    /**
     * 获取数据
     */
    protected void initData() {
        Bundle mBundle = getIntent().getExtras();
        mImagePaths = (ArrayList<String>) mBundle.getSerializable("imagePaths");
        mPosition = mBundle.getInt("position");
        mDefaultPic = mBundle.getInt("defaultPic") == 0 ? R.mipmap.icon_default_pic : mBundle.getInt("defaultPic");
        options = new RequestOptions()
                .centerCrop()
                .placeholder(mDefaultPic)
                .error(mDefaultPic)
                .priority(Priority.HIGH)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
    }

    /**
     * 初始化控件
     */
    protected void initView() {
        mViewPager = (PhotoViewViewPager) findViewById(R.id.view_pager);
        RelativeLayout mMultiple = (RelativeLayout) findViewById(R.id.multipleRL);
        PhotoView mLeaflet = (PhotoView) findViewById(R.id.leafletPV);
        mPhotoNumber = (TextView) findViewById(R.id.photo_number);
        if (mImagePaths.size() > 1) {
            loadMultipleImage(mMultiple);
        } else {
            loadSingleImage(mLeaflet);
        }
    }

    /**
     * 显示图片
     * @param imagePaths
     * @param displayPosition 打开时的显示位置
     * @param defaultPic 默认图片资源,非必填
     */
    public static void start(Context context,ArrayList<String> imagePaths, int displayPosition, int defaultPic) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("imagePaths",imagePaths);
        bundle.putInt("position",displayPosition);
        bundle.putInt("defaultPic",defaultPic);
        Intent intent = new Intent(context,DisplayPhotoActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);

    }

    /**
     * 单张图片显示
     *
     * @param mLeaflet
     */
    private void loadSingleImage(PhotoView mLeaflet) {
        mLeaflet.setVisibility(View.VISIBLE);
        mLeaflet.setImageResource(mDefaultPic);
        mLeaflet.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                DisplayPhotoActivity.this.finish();
            }
        });
        if (null == mImagePaths.get(0)) {
            File mIsmagePaths = (File) getIntent().getSerializableExtra("remoteFile");
            if (mIsmagePaths != null && mIsmagePaths.exists()) {
                Bitmap mBitmap = BitmapFactory.decodeFile(mImagePaths.get(0));
                mLeaflet.setImageBitmap(mBitmap);
            }
            return;
        }
        if (PhotoUtils.checkURL(mImagePaths.get(0))) {
            Glide.with(this).load(mImagePaths.get(0))
                    .apply(options)
                    .into(mLeaflet);
        } else {
            Bitmap mBitmap = PhotoUtils.decodeBitmapSd(mImagePaths.get(0));
            mLeaflet.setImageBitmap(mBitmap);
        }
    }

    /**
     * 加载多张图片
     *
     * @param mMultiple
     */
    private void loadMultipleImage(RelativeLayout mMultiple) {
        mMultiple.setVisibility(View.VISIBLE);
        DisplayPhotoAdapter mPhotoDisplayAdapter = new DisplayPhotoAdapter(this, mImagePaths, mDefaultPic);
        mViewPager.setAdapter(mPhotoDisplayAdapter);
        mViewPager.setCurrentItem(mPosition);
        mPhotoNumber.setText((mPosition + 1) + "/" + mImagePaths.size());
    }

    /**
     * 初始化监听器
     */
    protected void initListener() {
        if (mImagePaths.size() > 1) {
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    mPhotoNumber.setText((position + 1) + "/" + mImagePaths.size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }
}
