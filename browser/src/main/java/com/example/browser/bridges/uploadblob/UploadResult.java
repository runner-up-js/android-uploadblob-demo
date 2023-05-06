package com.example.browser.bridges.uploadblob;

public class UploadResult {
    private String result ;

    public UploadResult(Result result) {
        this.result = result.getValue();
    }

    public enum Result {
        INIT("int") ,
        NEXTBLOB("nextBlock") ,
        END("end");

        private final String value;
        Result(String s) {
            this.value = s;
        }

        public String getValue() {
            return value;
        }
    }
}
