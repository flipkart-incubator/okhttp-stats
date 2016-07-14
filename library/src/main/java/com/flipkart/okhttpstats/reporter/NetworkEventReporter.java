/*
 *
 *  * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.flipkart.okhttpstats.reporter;


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
