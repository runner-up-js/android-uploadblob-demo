package com.example.browser;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * 全局共用的对文件操作的方法
 *
 * @author wangyue
 */
public class ToolsFile {

    public static File getExtPicturesPath() {
        File extPicturesPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!extPicturesPath.exists()) {
            extPicturesPath.mkdir();
        }
        return extPicturesPath;
    }
    public static String createImagePathUriNoName(Context activity,String fileName) {
        File takePictureFile = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //适配 Android Q
            File base = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            String pathName = new StringBuffer().append(base.getPath()).append(File.separator)
                    .append(fileName).toString();
            takePictureFile = new File(pathName);
        } else {
            String pathName = new StringBuffer().append(getExtPicturesPath()).append(File.separator)
                    .append(fileName).toString();
            takePictureFile = new File(pathName);

        }
        return takePictureFile.getAbsolutePath();
    }

    public static URL getFromAssets(String fileName){
        String path = "file:///android_asset/"+fileName;
        File file = new File(path);
        try {
            URL url = file.toURL();
            return url;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
