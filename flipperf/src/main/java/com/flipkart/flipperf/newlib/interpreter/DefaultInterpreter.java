package com.flipkart.flipperf.newlib.interpreter;

import android.support.annotation.VisibleForTesting;

import com.flipkart.flipperf.newlib.NetworkInterceptor;
import com.flipkart.flipperf.newlib.reporter.NetworkEventReporter;
import com.flipkart.flipperf.newlib.response.CountingInputStream;
import com.flipkart.flipperf.newlib.response.DefaultResponseHandler;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import okio.BufferedSource;
import okio.Okio;

public class DefaultInterpreter implements NetworkInterpreter {
    private static final String HOST_NAME = "HOST";
    private Logger logger = LoggerFactory.getLogger(DefaultInterpreter.class);
    private NetworkEventReporter mEventReporter;

    public DefaultInterpreter(NetworkEventReporter mEventReporter) {
        this.mEventReporter = mEventReporter;
    }

    @Override
    public Response interpretResponseStream(int requestId, NetworkInterceptor.TimeInfo timeInfo, Request request, Response response) throws IOException {
        final OkHttpInspectorRequest okHttpInspectorRequest = new OkHttpInspectorRequest(requestId, request.url(), request.method(), request.header("Content-Length"), request.header(HOST_NAME));
        final OkHttpInspectorResponse okHttpInspectorResponse = new OkHttpInspectorResponse(requestId, response.code(), response.header("Content-Length"), timeInfo.mStartTime, timeInfo.mEndTime);

        //if response does not have content length, using CountingInputStream to read its bytes
        if (response.header("Content-Length") == null) {
            final ResponseBody body = response.body();
            InputStream responseStream = null;
            if (body != null) {
                try {
                    responseStream = body.byteStream();
                } catch (IOException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Error received while reading input stream {}", e.getMessage());
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
                    okHttpInspectorResponse.mResponseSize = String.valueOf(bytesRead);
                    mEventReporter.responseReceived(okHttpInspectorRequest, okHttpInspectorResponse);
                }
            }));

            //creating response object using the interpreted stream
            response = response.newBuilder().body(new ForwardingResponseBody(body, responseStream)).build();
        } else {
            //if response has content length, notify the event reporter that response has been received.
            mEventReporter.responseReceived(okHttpInspectorRequest, okHttpInspectorResponse);
        }
        return response;
    }

    @Override
    public void interpretError(int requestId, NetworkInterceptor.TimeInfo timeInfo, Request request, IOException e) {
        if (logger.isDebugEnabled()) {
            logger.debug("Error received while proceeding response {}", e.getMessage());
        }
        final OkHttpInspectorRequest okHttpInspectorRequest = new OkHttpInspectorRequest(requestId, request.url(), request.method(), request.header("Content-Length"), request.header(HOST_NAME));
        mEventReporter.httpExchangeError(okHttpInspectorRequest, e);
    }

    /**
     * Implementation of {@link NetworkEventReporter.InspectorRequest}
     */
    @VisibleForTesting
    public static class OkHttpInspectorRequest implements NetworkEventReporter.InspectorRequest {
        private final int mRequestId;
        private final URL mRequestUrl;
        private final String mMethodType;
        private final String mContentLength;
        private final String mHostName;

        public OkHttpInspectorRequest(int requestId, URL requestUrl, String methodType, String contentLength, String hostName) {
            this.mRequestId = requestId;
            this.mRequestUrl = requestUrl;
            this.mMethodType = methodType;
            this.mContentLength = contentLength;
            this.mHostName = hostName;
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
        public String requestSize() {
            return mContentLength;
        }

        @Override
        public String hostName() {
            return mHostName;
        }
    }

    /**
     * Implementation of {@link NetworkEventReporter.InspectorResponse}
     */
    @VisibleForTesting
    public static class OkHttpInspectorResponse implements NetworkEventReporter.InspectorResponse {
        private int mRequestId;
        private long mStartTime;
        private long mEndTime;
        private int mStatusCode;
        private String mResponseSize;

        public OkHttpInspectorResponse(int requestId, int statusCode, String responseSize, long startTime, long endTime) {
            this.mRequestId = requestId;
            this.mStatusCode = statusCode;
            this.mResponseSize = responseSize;
            this.mStartTime = startTime;
            this.mEndTime = endTime;
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
        public String responseSize() {
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
    }

    /**
     * Wrapper for {@link ResponseBody}
     * Will only be used in case the response does not have the content-length
     */
    @VisibleForTesting
    public static class ForwardingResponseBody extends ResponseBody {
        private final ResponseBody mBody;
        private final BufferedSource mInterceptedSource;

        public ForwardingResponseBody(ResponseBody body, InputStream interceptedStream) {
            mBody = body;
            mInterceptedSource = Okio.buffer(Okio.source(interceptedStream));
        }

        @Override
        public MediaType contentType() {
            return mBody.contentType();
        }

        @Override
        public long contentLength() throws IOException {
            return mBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            return mInterceptedSource;
        }
    }
}
