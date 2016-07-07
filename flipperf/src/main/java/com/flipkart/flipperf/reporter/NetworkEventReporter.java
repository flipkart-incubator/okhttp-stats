package com.flipkart.flipperf.reporter;


import java.io.IOException;
import java.net.URL;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Interface to report events in case of response or any errors.
 */
public interface NetworkEventReporter {

    /**
     * Notifies the {@link NetworkEventReporter} that the intercepted {@link Response} headers has been received
     *
     * @param inspectorResponse : contains response headers
     */
    void responseReceived(InspectorRequest inspectorRequest, InspectorResponse inspectorResponse);

    /**
     * Reports any {@link IOException} while {@link Response} is being proceeded.
     *
     * @param inspectorRequest {@link InspectorRequest}
     * @param e                : error message
     */
    void httpExchangeError(InspectorRequest inspectorRequest, IOException e);

    /**
     * Reports error while getting the input steam from {@link ResponseBody}
     *
     * @param inspectorResponse {@link InspectorResponse}
     * @param e                 : error message
     */
    void responseInputStreamError(InspectorRequest inspectorRequest, InspectorResponse inspectorResponse, Exception e);

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
