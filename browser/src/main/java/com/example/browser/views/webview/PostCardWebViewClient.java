package com.example.browser.views.webview;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;


import com.example.browser.ApplicationData;
import com.example.browser.UrlUtil;

import java.util.HashMap;
import java.util.Map;

public class PostCardWebViewClient extends WebViewClient {
    private Map<String, String> mHeaderMap = new HashMap<String, String>();
    private String currUrl;//当前访问url
    private boolean customBrower;
    private String browerClass;

    public void addHeaderParams(String key, String value) {
        mHeaderMap.put(key, value);
    }

    public void setCustomBrower(boolean customBrower) {
        this.customBrower = customBrower;
    }

    public void setBrowerClass(String browerClass) {
        this.browerClass = browerClass;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
        if (shouldOverrideUrl(webView, webResourceRequest.getUrl().toString())) {
            return true;
        } else {
            return super.shouldOverrideUrlLoading(webView, webResourceRequest);
        }
    }
    /**
     * 防止加载网页时调起系统浏览器
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.e("shouldUrlLoading", url);
        if (shouldOverrideUrl(view, url)) {
            return true;
        } else {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    private boolean shouldOverrideUrl(WebView view, String url) {
        Log.d("shouldOverrideUrl", url);
        if (UrlUtil.isUrlPrefix(url)) {
            if (customBrower) {
                Intent intent = new Intent();
                intent.setClassName(ApplicationData.globalContext,browerClass);
                intent.putExtra("url", url);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ApplicationData.globalContext.startActivity(intent);
            } else {
                view.loadUrl(url, mHeaderMap);
            }
            currUrl = url;
            return true;
        } else {
            try {
                inspectUrl(view, url);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    //特殊scheme处理
    private void inspectUrl(final WebView view, String url) {
        Log.d("inspectUrl", url);
        if (url.startsWith("alipays:") || url.startsWith("alipay")) {//支付宝判断支付宝是否安装 否则网页支付
            try {
                view.getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
            } catch (Exception e) {
                new AlertDialog.Builder(view.getContext())
                        .setMessage("未检测到支付宝客户端，请安装后重试。")
                        .setPositiveButton("立即安装", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri alipayUrl = Uri.parse("https://d.alipay.com");
                                view.getContext().startActivity(new Intent("android.intent.action.VIEW", alipayUrl));
                            }
                        }).setNegativeButton("取消", null).show();
            }
        } else if (url.startsWith("weixin://wap/pay?")) {//h5支付调起后关闭一个页面防止白页
            try {
                if (view.canGoBack()) {
                    view.goBack();
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
    }

}
