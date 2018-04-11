package com.library.base.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.TextView;

import com.library.base.R;
import com.library.base.dialog.LoadingDialog;
import com.library.base.mvpDemo.view.IBaseView;

import java.util.List;

/**
 * 基类
 * @Author: jerome
 * @Date: 2018-01-30
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseView {

    protected Fragment tempFragment;
    protected Handler handler;
    protected ActionBar actionBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        handler = new Handler(Looper.getMainLooper());
        setContentView(setLayoutId());

        if (null != initToolBar()) {
            setSupportActionBar(initToolBar());
            initActionBar();
        }
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Handler getHandler() {
        return handler;
    }

    /**
     * 初始化toolBar
     *
     * @return
     */
    public Toolbar initToolBar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    /**
     * 标题栏名称
     * @param title
     */
    @Override
    public void setTitle(CharSequence title) {
        TextView titleView = findViewById(android.R.id.title);
        if (titleView != null) {
            titleView.setText(title);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
        super.setTitle(title);
    }

    /**
     * 是否显不左上角返回图标
     */
    public void showHomeAsUp(boolean show) {
        if (getSupportActionBar() == null) return;
        getSupportActionBar().setDisplayHomeAsUpEnabled(show);
    }

    /**
     * 设置标题栏属性
     */
    public void initActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_back);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setElevation(0);
        if (!TextUtils.isEmpty(getTitle())) {
            setTitle(getTitle());
        }
    }

    /**
     * 将回调下发到fragment
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (null == fragments || fragments.size()==0 ) return;
        for (Fragment fragment : fragments) {
            if (fragment.isRemoving() || fragment.isDetached()) continue;
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void replaceFragment(int containerViewId, String tag, Class fragmentClass) {
        replaceFragment(containerViewId, tag, fragmentClass, null);
    }

    /**
     * fragmentClass在需求时进得初始化
     */
    protected void replaceFragment(int containerViewId, String tag, Class fragmentClass, Bundle args) {
        boolean isAdd = true;
        tempFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (tempFragment == null) {
            try {
                tempFragment = (Fragment) fragmentClass.newInstance();
                if (args != null) {
                    tempFragment.setArguments(args);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            isAdd = false;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerViewId, tempFragment, tag);
        if (!isAdd) {
            transaction.addToBackStack(tag);
        }
        transaction.commitAllowingStateLoss();
    }
    /**
     * 用tempFragment替代当前Fragment
     */
    protected void replaceFragment(int containerViewId, String tag, Fragment fragment, Bundle args) {
        boolean isAdd = true;
        tempFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (tempFragment == null) {
            try {
                tempFragment = fragment;
                if (args != null) {
                    tempFragment.setArguments(args);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            isAdd = false;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerViewId, tempFragment, tag);
        if (!isAdd) {
            transaction.addToBackStack(tag);
        }
        transaction.commitAllowingStateLoss();
    }


    @Override
    public void showLoading() {
        LoadingDialog.show(this);
    }

    @Override
    public void dismissLoading() {
        LoadingDialog.dismiss();
    }

    @Override
    public void showToast(final String msg) {
        LoadingDialog.toast(getContext(),msg);
    }

    /**
     * 设置布局
     */
    public abstract int setLayoutId();


    /**
     * 数据初始化
     */
    protected abstract void initData();


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    protected Context getContext() {
        return this;
    }
}

