package com.library.base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.library.base.R;

import butterknife.ButterKnife;

/**
 * @describe:
 * @author: zhengjl
 * @date: 2016/9/20 0020
 */
public abstract class BaseDialog extends Dialog implements View.OnClickListener {

    private Context context;
    public CallBack callBack;
    public final String TAG = "msg";
    private Toast mToast;


    public BaseDialog(Context context, int dialogLayout) {
        super(context, R.style.Dialog);
        this.context = context;
        this.setCanceledOnTouchOutside(true);
        setContentView(dialogLayout);
        ButterKnife.bind(this);
        DisplayMetrics dm = new DisplayMetrics();
        initDialog();
        setListener();
    }

    /**
     * 初始化对话框
     */
    protected abstract void initDialog();

    /**
     * 设置监听器
     */
    protected abstract void setListener();

    /**
     * 显示一个Toast信息
     *
     * @param resId
     */
    public void showToast(int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
        }
        mToast.show();
    }

    /**
     * 显示一个Toast信息
     *
     * @param content
     */
    public void showToast(String content) {
        if (mToast == null) {
            mToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(content);
        }
        mToast.show();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        void callBack(Object returnData);

        void cancel(Object returnData);
    }
}
