package com.library.base.photopicker.adapters;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.library.base.R;
import com.library.base.photopicker.PhotoPickerActivity;
import com.library.base.photopicker.PhotoPreviewActivity;
import com.library.base.photopicker.beans.MediaBean;
import com.library.base.photopicker.utils.GlideDisplay;
import com.library.base.photopicker.utils.MediaManager;
import com.library.base.photopicker.utils.PhotoUtils;

import java.io.File;
import java.util.List;


/**
 * 图片列表预览适配器
 * @author jerome
 */
public class PhotoRcyAdapter extends RecyclerView.Adapter {
    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_PHOTO = 1;
    private static final String TAG = "PhotoRcyAdapter";
    private List<MediaBean> mDatas;
    private String floderName;
    private Context mContext;
    private int mWidth;
    //是否显示相机，默认不显示
    private boolean mIsShowCamera = false;
    //图片选择数量
    private int mMaxNum = PhotoPickerActivity.DEFAULT_NUM;

    private PhotoSelectListener photoSelectListener;

    public PhotoRcyAdapter(Context context, String floderName, List<MediaBean> mDatas, PhotoSelectListener photoSelectListener) {
        this.mDatas = mDatas;
        this.mContext = context;
        this.floderName = floderName;
        int screenWidth = PhotoUtils.getWidthInPx(mContext);
        mWidth = (screenWidth - PhotoUtils.dip2px(mContext, 4)) / 3;
        this.photoSelectListener = photoSelectListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CAMERA) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item_camera_layout, parent, false);
            //设置高度等于宽度
            GridLayoutManager.LayoutParams layoutParams = new GridLayoutManager.LayoutParams(mWidth, mWidth);
            view.setLayoutParams(layoutParams);
            return new CameraHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.photo_item_layout, parent, false);
            GridLayoutManager.LayoutParams layoutParams = new GridLayoutManager.LayoutParams(mWidth, mWidth);
            view.setLayoutParams(layoutParams);
            return new PhotoHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)) {
            case TYPE_CAMERA:
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (photoSelectListener != null) {
                            photoSelectListener.gotoCamera();
                        }
                    }
                });
                break;
            case TYPE_PHOTO:
                final PhotoHolder photoHolder = (PhotoHolder) holder;
                final int index = getRealPosition(position);
                photoHolder.selectView.setVisibility(View.VISIBLE);
                photoHolder.selectView.setSelected(mDatas.get(index).isSelected());//设置显示选择切换
                //设置蒙版切换
                photoHolder.maskView.setVisibility(mDatas.get(index).isSelected() ? View.VISIBLE : View.GONE);
                //设置选中切换
                photoHolder.selectView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDatas.get(index).isSelected()) {//原来选中，现在是取消选中
                            photoHolder.selectView.setSelected(false);
                            photoHolder.maskView.setVisibility(View.GONE);
                            mDatas.get(index).setIsSelected(false);//更改数据为未选中
                            MediaManager.getSelectMediaBeans().remove(mDatas.get(index));
                        } else {//选中
                            if (MediaManager.getSelectMediaBeans().size() >= mMaxNum) {//已达上限
                                Toast.makeText(mContext, "只能选取" + mMaxNum + "张", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                photoHolder.selectView.setSelected(true);
                                photoHolder.maskView.setVisibility(View.VISIBLE);
                                //先移除，保证不重复
                                MediaManager.getSelectMediaBeans().remove(mDatas.get(index));
                                MediaManager.getSelectMediaBeans().add(mDatas.get(index));
                                mDatas.get(index).setIsSelected(true);//更改数据为选中
                            }
                        }
                        if (photoSelectListener != null) {
                            photoSelectListener.photoSelectChange(index, mDatas.get(index).getId(), mDatas.get(index).isSelected());
                        }
                    }
                });
                photoHolder.photoImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PhotoPreviewActivity.startPreviewPhoto(mContext, floderName, index);
                    }
                });
                photoHolder.photoImageView.getLayoutParams().width = photoHolder.photoImageView.getLayoutParams().height = mWidth;
                GlideDisplay.display(photoHolder.photoImageView, new File(mDatas.get(index).getRealPath()));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mIsShowCamera) {
            return mDatas == null ? 1 : mDatas.size() + 1;
        }
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mIsShowCamera) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PHOTO;
        }
    }

    public Integer getRealPosition(int position) {
        if (mIsShowCamera) {
            if (position == 0) {
                return null;
            }
            return position - 1;
        } else {
            return position;
        }
    }


    public void setDatas(List<MediaBean> mDatas) {
        this.mDatas = mDatas;
    }

    public List<MediaBean> getDatas() {
        return mDatas;
    }

    public void setIsShowCamera(boolean isShowCamera) {
        this.mIsShowCamera = isShowCamera;
    }

    public boolean isShowCamera() {
        return mIsShowCamera;
    }

    public void setMaxNum(int maxNum) {
        this.mMaxNum = maxNum;
    }


    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView photoImageView;
        private ImageView selectView;
        private View maskView;
        private FrameLayout wrapLayout;

        PhotoHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.imageview_photo);
            selectView = itemView.findViewById(R.id.checkmark);
            maskView = itemView.findViewById(R.id.mask);
            wrapLayout = itemView.findViewById(R.id.wrap_layout);
        }
    }

    private class CameraHolder extends RecyclerView.ViewHolder {
        CameraHolder(View itemView) {
            super(itemView);
        }
    }

    public interface PhotoSelectListener {

        /**
         * 选中监听回调
         * @param index
         * @param ImageId
         * @param isSelect
         */
        void photoSelectChange(int index, int ImageId, boolean isSelect);

        /**
         * 跳转至摄像机
         */
        void gotoCamera();
    }
}
