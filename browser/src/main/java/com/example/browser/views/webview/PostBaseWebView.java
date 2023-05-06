package com.example.browser.views.webview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.browser.bridges.uploadblob.UploadBlobBean;
import com.example.browser.bridges.uploadblob.UploadBlobContainer;
import com.example.browser.bridges.uploadblob.UploadBlobListener;
import com.example.browser.bridges.uploadblob.UploadResult;
import com.google.gson.Gson;

public class PostBaseWebView extends WebView {
    private PostCardWebViewClient postCardWebViewClient;
    private OnWebViewListener onWebViewListener;
    private  PostCardWebChromeClient postCardWebChromeClient;

    public void setOnWebViewListener(OnWebViewListener onWebViewListener) {
        this.onWebViewListener = onWebViewListener;
        postCardWebChromeClient.setOnWebViewListener(onWebViewListener);
    }

    public PostBaseWebView(@NonNull Context context) {
        super(context,null);
    }

    public PostBaseWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PostBaseWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWebViewSettings();
        postCardWebViewClient = new PostCardWebViewClient();
        postCardWebChromeClient =new PostCardWebChromeClient();
        setWebChromeClient(postCardWebChromeClient);
        setWebViewClient(postCardWebViewClient);
        addJavascriptInterface(new Bridge(),"bridge");
    }
    public void setCustomBrower(boolean customBrower) {
        postCardWebViewClient.setCustomBrower(customBrower);
    }

    public void setBrowerClass(String browerClass) {
        postCardWebViewClient.setBrowerClass(browerClass);
    }
    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(false);
        webSetting.setUseWideViewPort(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setBlockNetworkImage(false);
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(this, true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            webSetting.setMediaPlaybackRequiresUserGesture(false);
        }
        String dir = getContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSetting.setGeolocationDatabasePath(dir);
    }
    public interface  OnWebViewListener{
        void  onReceivedTitle(String title);
    }
    public class Bridge {
        private void callBack(UploadBlobBean blob, UploadResult.Result resultBean){
            callBack(blob, resultBean, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {

                }
            });
        }
        private void callBack(UploadBlobBean blob,UploadResult.Result resultBean,ValueCallback<String> valueCallback){
            UploadResult uploadResult= new UploadResult(resultBean);
            String result =new Gson().toJson(uploadResult);
            Log.e("getFile-callback",  blob.getCallback()+"("+result+")");
            post(new Runnable() {
                @Override
                public void run() {
                    evaluateJavascript("javascript:"+ blob.getCallback()+"("+result+")", valueCallback);
                }
            });
        }
        @JavascriptInterface
         public void transferBlob(String message){
            UploadBlobBean uploadBlobBean =new Gson().fromJson(message,UploadBlobBean.class);
            UploadBlobContainer.upload(uploadBlobBean, new UploadBlobListener<UploadBlobBean>() {
                @Override
                public void start(UploadBlobBean blob) {
                    callBack(blob,UploadResult.Result.INIT);
                }

                @Override
                public void write(UploadBlobBean blob) {
                    Log.e("getFile-callback",  UploadResult.Result.NEXTBLOB+"");
                    callBack(blob,UploadResult.Result.NEXTBLOB);
                }

                @Override
                public void end(UploadBlobBean blob) {
                    callBack(blob,UploadResult.Result.END);
                }
            });
         }
    }
}
