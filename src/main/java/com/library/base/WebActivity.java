package com.library.base;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.library.base.base.BasePresenterActivity;
import com.library.base.dialog.BaseDialog;
import com.library.base.dialog.LoadingDialog;
import com.library.base.dialog.PromptDialog;
import com.library.base.mvpDemo.presenter.impl.BasePresenterImpl;
import com.library.base.mvpDemo.view.IBaseView;
import com.library.base.view.PlaceholderView;


/**
 * web加载页面
 */
public class WebActivity extends BasePresenterActivity implements IBaseView {

    public static final String PARAM_TITLE = "title";
    public static final String PARAM_URL = "url";
    public static final int VIEW_TYPE_CLOSE = 1; // 返回键为关闭当前页面
    private Toolbar toolbar;
    private WebView webView;
    private CoordinatorLayout layoutCoordinator;
    private TextView mTitleView;
    private ProgressBar mProgressBar;
    private PlaceholderView mPlaceholderView;
    private String url;
    private int mViewType; // 显示类型
    private BroadcastReceiver mNetWorkChangeReceiver;

    private long mLastBackTime; // 上一次返回键时间


    @Override
    public int setLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    public void initData() {

        toolbar = findViewById(R.id.toolbar);
        webView = findViewById(R.id.content_web);
        layoutCoordinator = findViewById(R.id.layout_coordinator);
        mTitleView = findViewById(android.R.id.title);
        mProgressBar = findViewById(R.id.pb_web_view);
        mPlaceholderView = findViewById(R.id.view_placeholder);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mViewType = intent.getIntExtra("viewType", 0);
        setTitle(intent.getStringExtra(PARAM_TITLE));

        if (intent.getData() != null) {
            url = intent.getData().toString();
        } else {
            url = intent.getStringExtra(PARAM_URL);
        }
//        mPlaceholderView.dismiss();
        mPlaceholderView.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });
        initWebView();
        initViewType();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        mNetWorkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 重新打开网络
                if (TextUtils.equals(intent.getAction(), ConnectivityManager.CONNECTIVITY_ACTION)
                        && !intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
                        && !TextUtils.isEmpty(webView.getUrl())) {
                    mPlaceholderView.dismiss();
                    webView.reload();
                }
            }
        };

        registerReceiver(mNetWorkChangeReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetWorkChangeReceiver);
    }

    @Override
    public BasePresenterImpl createPresenter() {
        return null;
    }

    // 根据ViewType 来显示
    private void initViewType() {
//        if (mViewType == VIEW_TYPE_CLOSE) {
        setBackIsCloseButton();
//        }
    }

    // 返回按钮为关闭键
    protected void setBackIsCloseButton() {
//        mViewType = VIEW_TYPE_CLOSE;
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.iv_identify_close);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            } else if (mLastBackTime > 0 && System.currentTimeMillis() - mLastBackTime <= 2000) {
                finish();
                return true;
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.hint_exit_back), Toast.LENGTH_SHORT).show();
                mLastBackTime = System.currentTimeMillis();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true); // 开启Javascript支持
        settings.setLoadsImagesAutomatically(true); // 设置可以自动加载图片
        settings.setSupportZoom(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);

        settings.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        settings.setAppCachePath(appCachePath);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);

        webView.addJavascriptInterface(this, "app");

        webView.setHorizontalScrollBarEnabled(false); // 设置水平滚动条
        webView.setVerticalScrollBarEnabled(false); // 设置竖直滚动条
        webView.clearCache(true);
        webView.setWebViewClient(new AppWebViewClient());
        webView.setWebChromeClient(new AppWebViewChromeClient());
        webView.loadUrl(url);
    }

    @JavascriptInterface
    public void close() {
        finish();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (null != mTitleView)
        mTitleView.setText(title);
    }

    @Override
    public void requestError(int code, String message) {

    }

    class AppWebViewChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressBar.setProgress(newProgress);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            LoadingDialog.toast(view.getContext(), message);
            result.confirm();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            PromptDialog dialog = new PromptDialog(view.getContext());
            dialog.setContent(message);
            dialog.setCallBack(new BaseDialog.CallBack() {
                @Override
                public void callBack(Object returnData) {
                    result.confirm();
                }

                @Override
                public void cancel(Object returnData) {
                    result.cancel();
                }
            });
            dialog.show();
            return true;
        }
    }

    class AppWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            if (!TextUtils.isEmpty(view.getTitle()))
                setTitle(view.getTitle());
            mProgressBar.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
            mProgressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mProgressBar.setVisibility(View.VISIBLE);
            setTitle(getString(R.string.loading));
            mPlaceholderView.dismiss();
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String
                failingUrl) {
            if (errorCode == ERROR_CONNECT
                    || errorCode == ERROR_HOST_LOOKUP
                    || errorCode == ERROR_TIMEOUT
                    ) {
                setTitle(getString(R.string.network_error));
                mPlaceholderView.networkError();
            } else {
                setTitle(getString(R.string.error_web_page));
                mPlaceholderView.empty();
            }

        }
    }
}

