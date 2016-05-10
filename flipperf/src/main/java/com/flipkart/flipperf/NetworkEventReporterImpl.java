package com.flipkart.flipperf;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by anirudh.r on 02/05/16 at 1:32 PM.
 * Implementation for {@link NetworkEventReporter}
 */
public class NetworkEventReporterImpl implements NetworkEventReporter {

    private static final String TAG = NetworkEventReporterImpl.class.getName();
    private boolean isReportedEnabled = false;

    @Override
    public void setEnabled(boolean isEnabled) {
        isReportedEnabled = isEnabled;
    }

    @Override
    public boolean isReporterEnabled() {
        return isReportedEnabled;
    }

    @Override
    public void requestToBeSent(InspectorRequest inspectorRequest) {
        Log.d(TAG, "REQUEST SENT WITH ID : " + inspectorRequest.requestId()
                + "\nUrl : " + inspectorRequest.url()
                + "\nMethod : " + inspectorRequest.method()
                + "\nSize : " + inspectorRequest.requestSize());
    }

    @Override
    public void responseHeadersReceived(InspectorResponse inspectorResponse) throws IOException {
        if (!inspectorResponse.hasContentLength()) {
            Log.d(TAG, "RESPONSE HEADERS RECEIVED FOR ID : " + inspectorResponse.requestId()
                    + "\nUrl : " + inspectorResponse.url()
                    + "\nTime : " + inspectorResponse.responseTime()
                    + "\nStatus Code : " + inspectorResponse.statusCode());
        } else {
            Log.d(TAG, "RESPONSE HEADERS RECEIVED FOR ID : " + inspectorResponse.requestId()
                    + "\nUrl : " + inspectorResponse.url()
                    + "\nSize : " + inspectorResponse.responseSize()
                    + "\nTime : " + inspectorResponse.responseTime()
                    + "\nStatus Code : " + inspectorResponse.statusCode());
        }
    }

    @Override
    public void httpExchangeError(String requestId, IOException e) {
        Log.d(TAG, "EXCHANGE ERROR FOR ID : " + requestId
                + "\nException : " + e.getMessage());
    }

    @Override
    public InputStream interpretResponseStream(@Nullable InputStream inputStream, ResponseHandler responseHandler) throws IOException {
        return new CountingInputStream(inputStream, responseHandler);
    }

    @Override
    public void responseReadFinished(String requestId) {
    }

    @Override
    public void dataReceived(String requestId, int dataLength) {
        Log.d(TAG, "RESPONSE SIZE RECEIVED FOR ID : " + requestId
                + "\nSize : " + dataLength);
    }
}
