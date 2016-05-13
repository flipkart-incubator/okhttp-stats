package com.flipkart.flipperf;


import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.flipkart.flipperf.response.DefaultResponseHandler;
import com.flipkart.flipperf.response.ResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


/**
 * Created by anirudh.r on 02/05/16 at 12:53 PM.
 * <p>
 * Interface for Network Request Reporter
 */
public interface NetworkEventReporter {

    /**
     * Initialization method. Initialize all variables here
     */
    void onInitialized(Context context, Handler handler, NetworkManager networkManager);

    /**
     * To enable event reporter
     *
     * @param isEnabled : boolean value
     */
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
    void responseReceived(InspectorResponse inspectorResponse);

    /**
     * Reports any {@link IOException} while {@link com.squareup.okhttp.Response} is being proceeded.
     *
     * @param inspectorRequest {@link InspectorRequest}
     * @param e                : error message
     */
    void httpExchangeError(InspectorRequest inspectorRequest, IOException e);

    /**
     * Interpret the input stream received from the {@link com.squareup.okhttp.ResponseBody}
     *
     * @param inputStream     {@link InputStream}
     * @param responseHandler {@link DefaultResponseHandler}
     * @return {@link InputStream}
     */
    InputStream interpretResponseStream(@Nullable InputStream inputStream, ResponseHandler responseHandler);

    /**
     * Notifies the {@link NetworkEventReporter} that reponse data has been received
     *
     * @param inspectorResponse {@link InspectorResponse}
     * @param dataLength        : length of response
     */
    void responseDataReceived(InspectorResponse inspectorResponse, int dataLength);

    /**
     * Reports error while getting the input steam from {@link com.squareup.okhttp.ResponseBody}
     *
     * @param inspectorResponse {@link InspectorResponse}
     * @param e                 : error message
     */
    void responseInputStreamError(InspectorResponse inspectorResponse, IOException e);

    interface InspectorRequest {
        int requestId();

        URL url();

        String method();

        String requestSize();

        String hostName();
    }

    interface InspectorResponse {
        boolean hasContentLength();

        int requestId();

        int statusCode();

        String responseSize();

        long startTime();

        long endTime();
    }
}
