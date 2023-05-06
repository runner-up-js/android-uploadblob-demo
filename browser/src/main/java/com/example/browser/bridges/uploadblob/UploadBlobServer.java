package com.example.browser.bridges.uploadblob;

import android.util.Base64;
import android.util.Log;

import com.example.browser.ApplicationData;
import com.example.browser.ToolsFile;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * 流传输服务-目前只具备下载功能
 */
public class UploadBlobServer {
    public  String  file = "";
    private DownThread downThread;
    private int downLen = 0;
    public Status status = Status.PADDING;
    private long bufferLen;
    private UploadBlobListener<String> uploadBlobListener;

    public void setUploadBlobListener(UploadBlobListener<String> uploadBlobListener) {
        this.uploadBlobListener = uploadBlobListener;
    }

    public boolean init(String fileName, long bufferLen) {
        this.bufferLen = bufferLen;
        if(downThread ==null){
            downThread=new  DownThread(ToolsFile.createImagePathUriNoName(ApplicationData.globalContext,fileName));
        }
        status = Status.START;
        if(uploadBlobListener!=null){
            uploadBlobListener.start(fileName);
        }
        return true;
    }
    public boolean end(String fileName) {
        downThread = null;
        File file = new File( ToolsFile.createImagePathUriNoName(ApplicationData.globalContext,fileName));
        downLen = 0;
        Log.e("getFile-native-name",file.getName()+"::"+file.getPath());
        status = Status.END;
        if(uploadBlobListener!=null){
            uploadBlobListener.end(fileName);
        }
        return true;
    }
    public void uploadFile(String bufferBase64,String fileName) throws IOException {
        Log.e("getFile-native",bufferBase64 +"");
        byte[]  buffer = Base64.decode(bufferBase64,Base64.DEFAULT);
        Log.e("getFile-native",buffer[0]+"::" + downLen);
        downThread.write(buffer, downLen);
        downLen += buffer.length;
        downThread.close();
        // 长度一致的话结束流传输
        if(downLen>=bufferLen){
            end(fileName);
        }else{
            status = Status.WRITE;
            if(uploadBlobListener!=null){
                uploadBlobListener.write(fileName);
            }
        }
    }
    private class  DownThread {
        private String cacheFilePath = null;
        private InputStream inputStream;
        public DownThread(String cacheFilePath) {
            this.cacheFilePath = cacheFilePath;
        }

        private RandomAccessFile fileOutputStream = null;
        public void write(byte[] buffer,int seekStart) throws IOException {
            File cacheFile = new File(this.cacheFilePath);
            if (!cacheFile.exists()) {
                cacheFile.createNewFile();
            }
            fileOutputStream = new RandomAccessFile(cacheFile,"rwd");
            inputStream = new ByteArrayInputStream(buffer);
            fileOutputStream.seek(seekStart);
            int length = -1;
            while ((length = inputStream.read(buffer)) != -1) {
                Log.e("getFile-native-length",length +"");
                fileOutputStream.write(buffer,0,length);
            }
        }
        public File close() throws IOException {
            inputStream.close();
            fileOutputStream.close();
            return  new File(cacheFilePath);
        }
    }

    public enum  Status {
        PADDING,
        START,
        END,
        WRITE
    }
}
