package com.example.bridgedemo;


import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.browser.ApplicationData;
import com.example.browser.BrowserActivity;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationData.getInstance().init(getApplication());
        Intent intent = new Intent(ApplicationData.globalContext, BrowserActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BrowserActivity.URL_TAG,  "http://10.253.105.21:8080/#/?pid=0&cid=1");
        ApplicationData.globalContext.startActivity(intent);
    }
}