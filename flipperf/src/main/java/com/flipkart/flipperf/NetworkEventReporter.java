package com.flipkart.flipperf;


import android.support.annotation.Nullable;

import com.flipkart.flipperf.response.DefaultResponseHandler;
import com.flipkart.flipperf.response.ResponseHandler;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by anirudh.r on 02/05/16 at 12:53 PM.
 * Interface for Network Event Reporter
 */
public interface NetworkEventReporter {

    void setEnabled(boolean isEnabled);
    /**
     * Check if {@link NetworkEventReporter} is enabled
     *
     * @return true if enabled
     */
    boolean isReporterEnabled();

    /**
     * Notifies the {@link NetworkEventReporter} that the intercepted {@link com.squareup.okhttp.Request} is to be sent
     *
     * @param inspectorRequest : contains request details
     */
    void requestToBeSent(InspectorRequest inspectorRequest);

    /**
     * Notifies the {@link NetworkEventReporter} that the intercepted {@link com.squareup.okhttp.Response} headers has been received
     *
     * @param inspectorResponse : contains response headers
     */
    void responseHeadersReceived(InspectorResponse inspectorResponse) throws IOException;

    /**
     * Reports any {@link IOException} while {@link com.squareup.okhttp.Response} is being proceeded.
     *
     * @param requestId : request id
     * @param e         : exception
     */
    void httpExchangeError(String requestId, IOException e);

    /**
     * Interpret the input stream received from the {@link com.squareup.okhttp.ResponseBody}
     *
     * @param inputStream     {@link InputStream}
     * @param responseHandler {@link DefaultResponseHandler}
     * @return {@link InputStream}
     * @throws IOException
     */
    InputStream interpretResponseStream(@Nullable InputStream inputStream, ResponseHandler responseHandler) throws IOException;

    /**
     * Notifies the {@link NetworkEventReporter} that response data has been read
     *
     * @param requestId : request id
     */
    void responseReadFinished(String requestId);

    /**
     * Notifies the {@link NetworkEventReporter} that reponse data has been received
     *
     * @param requestId  : request id
     * @param dataLength : Data Length
     */
    void dataReceived(String requestId, int dataLength);


    interface InspectorRequest {
        String requestId();

        String url();

        String method();

        String requestSize();
    }

    interface InspectorResponse {
        boolean hasContentLength();

        String requestId();

        String url();

        int statusCode();

        String responseSize();
    }
}
