package com.example.browser;

import android.app.Application;
import android.content.Intent;

/**
 * Application
 * 保存一个全局的Applocation
 * 启动CrashException
 *
 * @author wangyue
 */
public class ApplicationData {

    /**
     * ApplicationData单例
     */
    private static ApplicationData instance;
    /**
     * 全局上下文
     */
    public static Application globalContext;


    public static void setInstance(ApplicationData instance) {
        ApplicationData.instance = instance;
    }

    public static synchronized ApplicationData getInstance() {
        if (instance == null) {
            instance = new ApplicationData();
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param globalContext
     */
    public void init(Application globalContext) {
        this.globalContext = globalContext;

    }

    public static void restartApplication() {
        final Intent intent = globalContext.getPackageManager().getLaunchIntentForPackage(globalContext.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        globalContext.startActivity(intent);
    }

}
