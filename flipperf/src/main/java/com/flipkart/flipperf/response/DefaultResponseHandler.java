package com.flipkart.flipperf.response;


import com.flipkart.flipperf.NetworkEventReporter;

public class DefaultResponseHandler implements ResponseHandler {
    private final NetworkEventReporter mEventReporter;
    private final NetworkEventReporter.InspectorResponse mInspectorResponse;
    private final NetworkEventReporter.InspectorRequest mInspectorRequest;
    private int mBytesRead = 0;

    public DefaultResponseHandler(NetworkEventReporter eventReporter, NetworkEventReporter.InspectorRequest inspectorRequest, NetworkEventReporter.InspectorResponse inspectorResponse) {
        this.mEventReporter = eventReporter;
        this.mInspectorResponse = inspectorResponse;
        this.mInspectorRequest = inspectorRequest;
    }

    @Override
    public void onRead(int numBytes) {
        mBytesRead += numBytes;
    }

    @Override
    public void onEOF() {
        mEventReporter.responseDataReceived(mInspectorRequest, mInspectorResponse, mBytesRead);
    }
}
