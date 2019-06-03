/*
 * The MIT License
 *
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.flipkart.okhttpstats.reporter;


import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.URL;

import okhttp3.RequestBody;
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

        @Nullable
        RequestBody requestBody();
    }

    interface InspectorResponse {

        int requestId();

        int statusCode();

        long responseSize();

        long startTime();

        long endTime();

        @Nullable
        ResponseBody responseBody();
    }
}