package com.library.base.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.library.base.R;

/**
 * 加载中对话框
 * @Author: jerome
 * @Date: 2017-08-15
 */
public class AppLoadingDialog extends AppDialog {

    private final TextView mContentView;
    private final ImageView mIconView;
    private final View mProgressView;

    public AppLoadingDialog(Context context) {
        super(context, R.style.AppDialog_Loading); // 不要主题
        setContentView(R.layout.fragment_dialog_loading);
        mContentView = (TextView) findViewById(R.id.tv_loading);
        mIconView = (ImageView) findViewById(R.id.img_loading_dialog_icon);
        mProgressView = findViewById(R.id.pb_loading_dialog);
        dismiss();
    }

    public void setMessage(CharSequence msg) {
        if (TextUtils.isEmpty(msg)) {
            mContentView.setVisibility(View.GONE);
        } else {
            mContentView.setVisibility(View.VISIBLE);
        }
        mContentView.setText(msg);
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            Log.e("rae", "dismiss loading dialog error!!", e);
        }
    }

    /**
     * 设置图标
     *
     * @param resId
     */
    public void setIconResource(int resId) {
        mIconView.setVisibility(View.VISIBLE);
        mProgressView.setVisibility(View.GONE);
        mIconView.setImageResource(resId);
    }

    public void setAutoDismiss(int autoDismiss) {
        mContentView.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, autoDismiss);
    }

    public void loading() {
        mProgressView.setVisibility(View.VISIBLE);
        mIconView.setVisibility(View.GONE);
    }

    private Context getBaseContext() {
        return ((ContextWrapper) getContext()).getBaseContext();
    }

    private Activity getActivity() {
        if (getBaseContext() instanceof Activity)
            return (Activity) getBaseContext();
        return null;
    }
}
