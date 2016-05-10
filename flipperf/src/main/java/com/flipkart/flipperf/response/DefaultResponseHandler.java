package com.flipkart.flipperf.response;


import com.flipkart.flipperf.NetworkEventReporter;

public class DefaultResponseHandler implements ResponseHandler {
    private final NetworkEventReporter mEventReporter;
    private NetworkEventReporter.InspectorResponse inspectorResponse;
    private int mBytesRead = 0;

    public DefaultResponseHandler(NetworkEventReporter eventReporter, NetworkEventReporter.InspectorResponse inspectorResponse) {
        this.mEventReporter = eventReporter;
        this.inspectorResponse = inspectorResponse;
    }

    @Override
    public void onRead(int numBytes) {
        mBytesRead += numBytes;
    }

    @Override
    public void onEOF() {
        mEventReporter.responseDataReceived(inspectorResponse, mBytesRead);
    }
}
