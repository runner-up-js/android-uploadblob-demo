package com.example.browser.bridges.uploadblob;

public interface UploadBlobListener<T> {
    void start(T blob);
    void write(T blob);
    void end(T blob);
}
