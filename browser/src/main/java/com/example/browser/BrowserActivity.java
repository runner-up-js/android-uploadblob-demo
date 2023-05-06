package com.example.browser;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.browser.views.webview.PostBaseWebView;

public class BrowserActivity extends AppCompatActivity implements PostBaseWebView.OnWebViewListener {
    private PostBaseWebView postBaseWebView;
    public static final String URL_TAG = "url";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        initView();
    }

    private void getUrl() {
        String url = getIntent().getStringExtra(URL_TAG);
        postBaseWebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        boolean canBack = postBaseWebView.canGoBack();
        if (canBack) {
            postBaseWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    protected void initView() {
        postBaseWebView = findViewById(R.id.webview);
        postBaseWebView.setOnWebViewListener(this);
        getUrl();
    }

    @Override
    public void onReceivedTitle(String title) {
    }

    private boolean isOnPause = false;

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (isOnPause) {
                if (postBaseWebView != null) {
                    postBaseWebView.getClass().getMethod("onResume").invoke(postBaseWebView, (Object[]) null);
                }
                isOnPause = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (postBaseWebView != null) {
                postBaseWebView.getClass().getMethod("onPause").invoke(postBaseWebView, (Object[]) null);
                isOnPause = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (postBaseWebView != null) {
            postBaseWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            postBaseWebView.clearHistory();

            ((ViewGroup) postBaseWebView.getParent()).removeView(postBaseWebView);
            postBaseWebView.destroy();
            postBaseWebView = null;
        }
        super.onDestroy();
    }

}