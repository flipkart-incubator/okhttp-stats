package com.flipkart.flipperf.response;


import com.flipkart.flipperf.NetworkEventReporter;

public class DefaultResponseHandler implements ResponseHandler {
    private final NetworkEventReporter mEventReporter;
    private final String mRequestId;

    private int mBytesRead = 0;

    public DefaultResponseHandler(NetworkEventReporter eventReporter, String requestId) {
        mEventReporter = eventReporter;
        mRequestId = requestId;
    }

    @Override
    public void onRead(int numBytes) {
        mBytesRead += numBytes;
    }

    @Override
    public void onEOF() {
        reportDataReceived();
    }

    private void reportDataReceived() {
        mEventReporter.dataReceived(mRequestId, mBytesRead);
//        mEventReporter.responseReadFinished(mRequestId);
    }
}
