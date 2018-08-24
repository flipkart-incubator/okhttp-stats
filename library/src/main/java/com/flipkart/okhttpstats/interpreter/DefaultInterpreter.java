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

package com.flipkart.okhttpstats.interpreter;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.flipkart.okhttpstats.NetworkInterceptor;
import com.flipkart.okhttpstats.reporter.NetworkEventReporter;
import com.flipkart.okhttpstats.response.CountingInputStream;
import com.flipkart.okhttpstats.response.DefaultResponseHandler;
import com.flipkart.okhttpstats.toolbox.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;

/**
 * Default implementation of {@link NetworkInterpreter}
 */
public class DefaultInterpreter implements NetworkInterpreter {
    private static final String HOST_NAME = "HOST";
    private static final String CONTENT_LENGTH = "Content-Length";
    NetworkEventReporter mEventReporter;

    public DefaultInterpreter(NetworkEventReporter mEventReporter) {
        this.mEventReporter = mEventReporter;
    }

    @Override
    public Response interpretResponseStream(int requestId, NetworkInterceptor.TimeInfo timeInfo, Request request, Response response) throws IOException {
        ResponseBody responseBody = response.body();

        final OkHttpInspectorRequest okHttpInspectorRequest = new OkHttpInspectorRequest(requestId, request.url().url(), request.method(), Utils.contentLength(request.headers()), request.url().host(), request.body());
        final OkHttpInspectorResponse okHttpInspectorResponse = new OkHttpInspectorResponse(requestId, response.code(), Utils.contentLength(response.headers()), timeInfo.mStartTime, timeInfo.mEndTime, responseBody);
        //if response does not have content length, using CountingInputStream to read its bytes
        if (response.header(CONTENT_LENGTH) == null) {
            InputStream responseStream = null;
            if (responseBody != null) {
                try {
                    responseStream = responseBody.byteStream();
                } catch (Exception e) {
                    if (Utils.isLoggingEnabled) {
                        Log.d("Error reading IS : ", e.getMessage());
                    }

                    //notify event reporter in case there is any exception while getting the input stream of response
                    mEventReporter.responseInputStreamError(okHttpInspectorRequest, okHttpInspectorResponse, e);
                    throw e;
                }
            }

            //interpreting the response stream using CountingInputStream, once the counting is done, notify the event reporter that response has been received
            responseStream = new CountingInputStream(responseStream, new DefaultResponseHandler(new DefaultResponseHandler.ResponseCallback() {
                @Override
                public void onEOF(long bytesRead) {
                    okHttpInspectorResponse.mResponseSize = bytesRead;
                    mEventReporter.responseReceived(okHttpInspectorRequest, okHttpInspectorResponse);
                }
            }));

            //creating response object using the interpreted stream
            response = response.newBuilder().body(new ForwardingResponseBody(responseBody, responseStream)).build();
        } else {
            //if response has content length, notify the event reporter that response has been received.
            mEventReporter.responseReceived(okHttpInspectorRequest, okHttpInspectorResponse);
        }

        return response;
    }

    @Override
    public void interpretError(int requestId, NetworkInterceptor.TimeInfo timeInfo, Request request, IOException e) {
        if (Utils.isLoggingEnabled) {
            Log.d("Error response: ", e.getMessage());
        }
        final OkHttpInspectorRequest okHttpInspectorRequest = new OkHttpInspectorRequest(requestId, request.url().url(), request.method(), Utils.contentLength(request.headers()), request.header(HOST_NAME), request.body());
        mEventReporter.httpExchangeError(okHttpInspectorRequest, e);
    }

    /**
     * Implementation of {@link NetworkEventReporter.InspectorRequest}
     */
    static class OkHttpInspectorRequest implements NetworkEventReporter.InspectorRequest {
        final int mRequestId;
        final URL mRequestUrl;
        final String mMethodType;
        final long mContentLength;
        final String mHostName;
        final RequestBody mRequestBody;

        OkHttpInspectorRequest(int requestId, URL requestUrl, String methodType, long contentLength,
                               String hostName, RequestBody requestBody) {
            this.mRequestId = requestId;
            this.mRequestUrl = requestUrl;
            this.mMethodType = methodType;
            this.mContentLength = contentLength;
            this.mHostName = hostName;
            this.mRequestBody = requestBody;
        }

        @Override
        public int requestId() {
            return mRequestId;
        }

        @Override
        public URL url() {
            return mRequestUrl;
        }

        @Override
        public String method() {
            return mMethodType;
        }

        @Override
        public long requestSize() {
            return mContentLength;
        }

        @Override
        public String hostName() {
            return mHostName;
        }

        @Override
        public RequestBody requestBody() { return mRequestBody; }
    }

    /**
     * Implementation of {@link NetworkEventReporter.InspectorResponse}
     */
    static class OkHttpInspectorResponse implements NetworkEventReporter.InspectorResponse {
        int mRequestId;
        long mStartTime;
        long mEndTime;
        int mStatusCode;
        long mResponseSize;
        @Nullable
        ResponseBody responseBody;

        OkHttpInspectorResponse(int requestId, int statusCode, long responseSize, long startTime, long endTime, @Nullable ResponseBody responseBody) {
            this.mRequestId = requestId;
            this.mStatusCode = statusCode;
            this.mResponseSize = responseSize;
            this.mStartTime = startTime;
            this.mEndTime = endTime;
            this.responseBody = responseBody;
        }

        @Override
        public int requestId() {
            return mRequestId;
        }

        @Override
        public int statusCode() {
            return mStatusCode;
        }

        @Override
        public long responseSize() {
            return mResponseSize;
        }

        @Override
        public long startTime() {
            return mStartTime;
        }

        @Override
        public long endTime() {
            return mEndTime;
        }

        @Override
        @Nullable
        public ResponseBody responseBody() {
            return responseBody;
        }
    }

    /**
     * Wrapper for {@link ResponseBody}
     * Will only be used in case the response does not have the content-length
     */
    @VisibleForTesting
    static class ForwardingResponseBody extends ResponseBody {
        final ResponseBody mBody;
        final BufferedSource mInterceptedSource;

        ForwardingResponseBody(ResponseBody body, InputStream interceptedStream) {
            mBody = body;
            mInterceptedSource = Okio.buffer(Okio.source(interceptedStream));
        }

        @Override
        public MediaType contentType() {
            return mBody.contentType();
        }

        @Override
        public long contentLength() {
            return mBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            return mInterceptedSource;
        }
    }
}