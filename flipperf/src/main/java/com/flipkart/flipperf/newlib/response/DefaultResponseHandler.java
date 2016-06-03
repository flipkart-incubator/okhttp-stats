package com.flipkart.flipperf.newlib.response;


public class DefaultResponseHandler implements ResponseHandler {
    private final ResponseCallback mResponseCallback;
    private int mBytesRead = 0;

    public DefaultResponseHandler(ResponseCallback responseCallback) {
        mResponseCallback = responseCallback;
    }

    @Override
    public void onRead(int numBytes) {
        mBytesRead += numBytes;
    }

    @Override
    public void onEOF() {
        mResponseCallback.onEOF(mBytesRead);
    }

    public interface ResponseCallback {
        void onEOF(long bytesRead);
    }
}
