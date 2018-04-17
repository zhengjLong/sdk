package com.library.base.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.library.base.dialog.LoadingDialog;
import com.library.base.utils.SdkPreference;


import butterknife.ButterKnife;

/**
 * fragment 基类
 * @author : jerome
 */
public abstract class BaseFragment extends Fragment {


    private View rootView;
    protected Activity activity;
    protected SdkPreference preferenceUtils;
    protected boolean isCreate;
    private boolean mIsShown; // 是否真正显示


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtils = SdkPreference.getInstance();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return setContentView(inflater, setLayoutId());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mIsShown = true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        if (!isCreate) {
            initOnceData();
            isCreate = true;
        }
        initData();
        initListener();
    }


    /**
     * @param inflater
     * @param resId
     * @Description: 子类调用该办法，即可避免重复加载UI
     */
    public View setContentView(LayoutInflater inflater, int resId) {
        if (rootView == null) {
            rootView = inflater.inflate(resId, null);
        }
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        return rootView;
    }

    /**
     * 设置布局
     *
     * @return
     */
    public abstract int setLayoutId();


    /**
     * fragment 只需初始化一次的数据
     */
    public abstract void initOnceData();


    /**
     * Description: 初始化数组
     */
    public abstract void initData() ;

    /**
     * Description: 初始化监听器
     */
    public abstract void initListener();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsShown = false;
    }


    protected boolean isShown() {
        return mIsShown;
    }

    protected void showToast(String msg) {
        LoadingDialog.toast(getContext(), msg);
    }


}

