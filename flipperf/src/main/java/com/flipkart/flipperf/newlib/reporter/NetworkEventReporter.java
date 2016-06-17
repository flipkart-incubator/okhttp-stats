package com.flipkart.flipperf.newlib.reporter;


import java.io.IOException;
import java.net.URL;

/**
 * Interface to report events in case of response or any errors.
 */
public interface NetworkEventReporter {

    /**
     * Notifies the {@link NetworkEventReporter} that the intercepted {@link com.squareup.okhttp.Response} headers has been received
     *
     * @param inspectorResponse : contains response headers
     */
    void responseReceived(InspectorRequest inspectorRequest, InspectorResponse inspectorResponse);

    /**
     * Reports any {@link IOException} while {@link com.squareup.okhttp.Response} is being proceeded.
     *
     * @param inspectorRequest {@link InspectorRequest}
     * @param e                : error message
     */
    void httpExchangeError(InspectorRequest inspectorRequest, IOException e);

    /**
     * Reports error while getting the input steam from {@link com.squareup.okhttp.ResponseBody}
     *
     * @param inspectorResponse {@link InspectorResponse}
     * @param e                 : error message
     */
    void responseInputStreamError(InspectorRequest inspectorRequest, InspectorResponse inspectorResponse, IOException e);

    interface InspectorRequest {
        int requestId();

        URL url();

        String method();

        long requestSize();

        String hostName();
    }

    interface InspectorResponse {

        int requestId();

        int statusCode();

        long responseSize();

        long startTime();

        long endTime();
    }
}
