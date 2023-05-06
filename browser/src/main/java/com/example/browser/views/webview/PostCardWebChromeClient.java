package com.example.browser.views.webview;

import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;


public class PostCardWebChromeClient  extends WebChromeClient {
    private PostBaseWebView.OnWebViewListener onWebViewListener;

    public void setOnWebViewListener(PostBaseWebView.OnWebViewListener onWebViewListener) {
        this.onWebViewListener = onWebViewListener;
    }
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        callback.invoke(origin, true, true);
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }
    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
        if(onWebViewListener!=null){
            onWebViewListener.onReceivedTitle(title);
        }
    }
}
