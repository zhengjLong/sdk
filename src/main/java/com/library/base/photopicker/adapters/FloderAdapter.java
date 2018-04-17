package com.library.base.photopicker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.library.base.R;
import com.library.base.photopicker.beans.MediaFloder;
import com.library.base.photopicker.utils.GlideDisplay;
import com.library.base.photopicker.utils.PhotoUtils;

import java.io.File;
import java.util.List;

/**
 * 图片目录适配器
 */
public class FloderAdapter extends BaseAdapter {

    List<MediaFloder> mDatas;
    Context mContext;
    int mWidth;

    public FloderAdapter(Context context, List<MediaFloder> mDatas) {
        this.mDatas = mDatas;
        this.mContext = context;
        mWidth = PhotoUtils.dip2px(context, 90);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.photo_item_floder_layout, null);
            holder.photoIV = convertView.findViewById(R.id.imageview_floder_img);
            holder.floderNameTV = convertView.findViewById(R.id.textview_floder_name);
            holder.photoNumTV = convertView.findViewById(R.id.textview_photo_num);
            holder.selectIV = convertView.findViewById(R.id.imageview_floder_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.selectIV.setVisibility(View.GONE);
        holder.photoIV.setImageResource(R.mipmap.icon_default_pic);
        MediaFloder floder = mDatas.get(position);
        if (floder.isSelected()) {
            holder.selectIV.setVisibility(View.VISIBLE);
        }
        holder.floderNameTV.setText(floder.getName());
        holder.photoNumTV.setText(String.format("%d张", floder.getMediaBeanList().size()));
        if (floder.getMediaBeanList().size() > 0) {
            holder.photoIV.getLayoutParams().width = holder.photoIV.getLayoutParams().height = mWidth;
            GlideDisplay.display(holder.photoIV, new File(floder.getMediaBeanList().get(0).getRealPath()));
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView photoIV;
        private TextView floderNameTV;
        private TextView photoNumTV;
        private ImageView selectIV;
    }

}
