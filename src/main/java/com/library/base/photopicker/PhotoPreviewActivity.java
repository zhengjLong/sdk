package com.library.base.photopicker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.library.base.R;
import com.library.base.photopicker.beans.MediaBean;
import com.library.base.photopicker.beans.SelectStatusEvent;
import com.library.base.photopicker.utils.MediaManager;
import com.library.base.photopicker.utils.PhotoUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;


/**
 * 预览界面，包括拍照预览
 */
public class PhotoPreviewActivity extends AppCompatActivity {
    public final static int TYPE_CAMERA=0;//照相预览
    public final static int TYPE_PHOTO=1;//预览
    public final static int TYPE_SELECT_PHOTO=2;//预览选中的照片
    private static final String TAG = "PhotoPreviewActivity";
    private int type;
    private int currentPosition;
    private String cameraPhotoPath;
    private String floderName;
    private MultiTouchViewPager viewPager;
    private ViewPagerAdapter adapter;
    ImageView ivSelect;
    Button commitBtn;
    TextView tv_num;
    View rl_show_select;
//    private List<MediaBean> selectMediaBeans=null;
    private long maxSize =0l;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        type = getIntent().getIntExtra("type", 1);
        currentPosition = getIntent().getIntExtra("position", 0);
        cameraPhotoPath = getIntent().getStringExtra("cameraPhotoPath");
        floderName = getIntent().getStringExtra("floderName");
        maxSize = PhotoUtils.getDeviceWidth(this)
                        * PhotoUtils.getDeviceHeight(this);
        setupUI();
    }

    private void setupUI() {
        ivSelect = (ImageView) findViewById(R.id.iv_select);
        tv_num = (TextView) findViewById(R.id.tv_num);
        rl_show_select =  findViewById(R.id.rl_show_select);
        commitBtn = (Button) findViewById(R.id.commit);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type==TYPE_CAMERA){//是相机预览
                    MediaManager.getSelectMediaBeans().clear();
                    MediaManager.getSelectMediaBeans().add(adapter.getItem(0));
                }
                if(rl_show_select.getVisibility()==View.VISIBLE) {

                    if (MediaManager.getSelectMediaBeans().size() >0) {
                        MediaManager.selectOK();
                        finish();
                    } else {
                        Toast.makeText(PhotoPreviewActivity.this,"请选择图片哦!",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    MediaManager.selectOK();
                    finish();
                }
            }
        });

        viewPager = (MultiTouchViewPager) findViewById(R.id.viewpager);
        if(type==TYPE_CAMERA){
            rl_show_select.setVisibility(View.GONE);
            MediaBean bean=new MediaBean(cameraPhotoPath);
            bean.setIsPhoto(true);
            ArrayList<MediaBean> mediaBeans = new ArrayList<>();
            mediaBeans.add(bean);
            adapter=new ViewPagerAdapter(mediaBeans);
        }else if(type==TYPE_SELECT_PHOTO){
//            selectMediaBeans=new LinkedList<>();
//            selectMediaBeans.addAll(MediaManager.getSelectMediaBeans());
            adapter=new ViewPagerAdapter(MediaManager.getSelectMediaBeans()); //不能直接设置，否则更改选中时数据全局的会减少
//            adapter=new ViewPagerAdapter(selectMediaBeans);
        } else if(type==TYPE_PHOTO){
            adapter=new ViewPagerAdapter(MediaManager.getMediaFloder(floderName).getMediaBeanList());
        }
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition, false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ivSelect.setSelected(adapter.getItem(position).isSelected());
                tv_num.setText((position + 1) + "/" + adapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //第一次显示状态
        ivSelect.setSelected(adapter.getItem(currentPosition).isSelected());
        tv_num.setText((currentPosition+1)+"/"+adapter.getCount());
        ivSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ivSelect.setSelected(!ivSelect.isSelected());
                boolean isSelected = ivSelect.isSelected();
                int currentItem = viewPager.getCurrentItem();
                MediaBean mediaBean = adapter.getItem(currentItem);

                if(isSelected){//已经选中的，取消选中
                    MediaManager.getSelectMediaBeans().remove(mediaBean);
                    ivSelect.setSelected(false);
                    EventBus.getDefault().post(new SelectStatusEvent(mediaBean.getId(),false));
                }else{//未选中的，选择
                    if(MediaManager.getSelectMediaBeans().size()>=PhotoPickerActivity.mMaxNum){//已达上限
                        ivSelect.setSelected(false);
                        Toast.makeText(PhotoPreviewActivity.this, "只能选取" + PhotoPickerActivity.mMaxNum + "张", Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        MediaManager.getSelectMediaBeans().remove(mediaBean);
                        ivSelect.setSelected(true);
                        if (ivSelect.isSelected()) {
                            mediaBean.setIsSelected(true);
                            MediaManager.getSelectMediaBeans().add(mediaBean);
                        }
                        //通知相册列表更新
                        EventBus.getDefault().post(new SelectStatusEvent(mediaBean.getId(),true));
                    }
                }


            }
        });
    }

    /**
     * 预览相机拍过来的照片
     * @param context
     * @param cameraPhotoPath
     */
    public static void startFromCamera(Context context,String cameraPhotoPath) {
        Intent starter = new Intent(context, PhotoPreviewActivity.class);
        starter.putExtra("type",TYPE_CAMERA);
        starter.putExtra("cameraPhotoPath", cameraPhotoPath);
        context.startActivity(starter);
    }
    /**
     * 预览图片
     * @param context
     * @param floderName
     * @param position
     */
    public static void startPreviewPhoto(Context context,String floderName,int position) {
        Intent starter = new Intent(context, PhotoPreviewActivity.class);
        starter.putExtra("type",TYPE_PHOTO);
        starter.putExtra("position",position);
        starter.putExtra("floderName",floderName);
        context.startActivity(starter);
    }

    public static void startPreviewPhoto(Activity activity, String floderName, int position,int reqCode) {
        Intent starter = new Intent(activity, PhotoPreviewActivity.class);
        starter.putExtra("type",TYPE_PHOTO);
        starter.putExtra("position",position);
        starter.putExtra("floderName",floderName);
        activity.startActivityForResult(starter,reqCode);
    }

    /**
     * 预览选中的图片
     * @param context
     */
    public static void startPreviewSelectPhoto(Context context) {
        Intent starter = new Intent(context, PhotoPreviewActivity.class);
        starter.putExtra("type",TYPE_SELECT_PHOTO);
        context.startActivity(starter);
    }
    class ViewPagerAdapter extends PagerAdapter {
        private List<MediaBean> allPicFiles;// 所有图片
        private MediaBean mf;
        private RequestOptions options;


        public ViewPagerAdapter(List<MediaBean> sysallPicFiles) {
            super();
            this.allPicFiles = sysallPicFiles;

            options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.color.colorTextStyleA9)
                    .error(R.color.colorTextStyleA9)
                    .priority(Priority.HIGH)
                    .dontAnimate()
                    .dontTransform()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
        }


        @Override
        public int getCount() {
            return allPicFiles.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
        public MediaBean getItem(int position){
            return allPicFiles.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        @SuppressLint("CheckResult")
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            mf = allPicFiles.get(position);
            if(mf.isPhoto()){
                PhotoView photoView = new PhotoView(container.getContext());
                photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                int[] size = PhotoUtils.getBitmapWidthAndHeight(mf.getRealPath());
                if(size[0]*size[1] > maxSize){

                    options.override(PhotoUtils.getDeviceWidth(PhotoPreviewActivity.this) > size[0] ? size[0] : PhotoUtils.getDeviceWidth(PhotoPreviewActivity.this),
                            PhotoUtils.getDeviceHeight(PhotoPreviewActivity.this) > size[1] ? size[1] : PhotoUtils.getDeviceHeight(PhotoPreviewActivity.this));

                    Glide.with(PhotoPreviewActivity.this)
                            .load(new File(mf.getRealPath())).apply(options)
                            .into(photoView);
                }else{
                    Glide.with(PhotoPreviewActivity.this)
                            .load(new File(mf.getRealPath()))
                            .into(photoView);
                }
                container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                return photoView;
            }

            return null;
        }
    }
}
