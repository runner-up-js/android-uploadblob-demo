package com.example.browser.bridges.uploadblob;

import java.io.IOException;
import java.util.HashMap;

/**
 * 支持同时多个流传输
 */
public class UploadBlobContainer {

    private static HashMap<String, UploadBlobServer> updateBlobHashMap =new HashMap<>();

    private static UploadBlobListener<UploadBlobBean> uploadBlobListener;

    public static void setUploadBlobListener(UploadBlobListener<UploadBlobBean> uploadBlobListener) {
        UploadBlobContainer.uploadBlobListener = uploadBlobListener;
    }

    /**
     * 获取唯一类
     * @param uploadBlobBean
     * @return
     */
    public static UploadBlobServer getUpdateBlob(UploadBlobBean uploadBlobBean){
        String fileName = uploadBlobBean.getFileName();
        if(updateBlobHashMap.containsKey(fileName)){
            return updateBlobHashMap.get(fileName);
        }
        // 初始化 流传输类
        UploadBlobServer updateBlob=new UploadBlobServer();
        // 事件统一输出
        updateBlob.setUploadBlobListener(new UploadBlobListener<String>() {
            @Override
            public void start(String fileName) {
                 if(UploadBlobContainer.uploadBlobListener!=null){
                     UploadBlobContainer.uploadBlobListener.start(uploadBlobBean);
                 }
            }

            @Override
            public void write(String fileName) {
                if(UploadBlobContainer.uploadBlobListener!=null){
                    UploadBlobContainer.uploadBlobListener.write(uploadBlobBean);
                }
            }

            @Override
            public void end(String fileName) {
                if(UploadBlobContainer.uploadBlobListener!=null){
                    UploadBlobContainer.uploadBlobListener.end(uploadBlobBean);
                }
                // 完成后从输出站内退出
                updateBlobHashMap.remove(fileName);
            }
        });
        updateBlobHashMap.put(fileName,updateBlob);
        return updateBlob;
    }

    public static  void upload(UploadBlobBean uploadBlobBean) {
        upload(uploadBlobBean,null);
    }
    public static  void upload(UploadBlobBean uploadBlobBean,UploadBlobListener<UploadBlobBean> uploadBlobListener) {
        setUploadBlobListener(uploadBlobListener);
        UploadBlobServer uploadBlobServer = getUpdateBlob(uploadBlobBean);
        switch (uploadBlobServer.status){
            case PADDING:
                uploadBlobServer.init(uploadBlobBean.getFileName(),uploadBlobBean.getContentLength());
            case WRITE:
                try {
                    uploadBlobServer.uploadFile(uploadBlobBean.getBody(),uploadBlobBean.getFileName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

        }
    }
}
