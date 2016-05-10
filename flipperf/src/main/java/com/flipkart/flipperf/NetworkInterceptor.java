package com.flipkart.flipperf;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.VisibleForTesting;

import com.flipkart.flipperf.response.DefaultResponseHandler;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import okio.BufferedSource;
import okio.Okio;

/*
 * Custom Interceptor to intercept all network calls.
 */
public final class NetworkInterceptor implements Interceptor {

    private static final String HANDLER_THREAD_NAME = "flipperf.networkInterceptor";
    private static final String CONTENT_LENGTH = "Content-Length";
    private final NetworkEventReporter mEventReporter;
    private final AtomicInteger mNextRequestId = new AtomicInteger(0);

    private NetworkInterceptor(Context context, Builder builder) {
        Handler handler = builder.mHandler;
        //if handler given by client is null, create a new handler
        if (handler == null) {
            HandlerThread handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }

        mEventReporter = builder.mNetworkEventReporter == null ? new NetworkEventReporterImpl() : builder.mNetworkEventReporter;
        mEventReporter.onInitialized(context, handler);
        mEventReporter.setEnabled(builder.mEnabled);
    }

    /**
     * Method to intercept all the network calls.
     *
     * @param chain {@link com.squareup.okhttp.Interceptor.Chain}
     * @return {@link Response}
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        final String requestId = String.valueOf(mNextRequestId.getAndIncrement());

        Request request = chain.request();
        OkHttpInspectorRequest okHttpInspectorRequest = null;

        if (mEventReporter.isReporterEnabled()) {
            okHttpInspectorRequest = new OkHttpInspectorRequest(requestId, request.urlString(), request.method(), request.header(CONTENT_LENGTH));
            //notify event reporter that request is to be sent.
            mEventReporter.requestToBeSent(okHttpInspectorRequest);
        }

        long mResponseStartTime, mResponseEndTime;
        Response response;
        try {
            mResponseStartTime = System.nanoTime();
            response = chain.proceed(request);
            mResponseEndTime = System.nanoTime();
        } catch (final IOException e) {
            if (mEventReporter.isReporterEnabled()) {
                //notify event reporter in case there is any exception while proceeding request
                mEventReporter.httpExchangeError(okHttpInspectorRequest, e);
            }
            throw e;
        }

        if (mEventReporter.isReporterEnabled()) {
            final OkHttpInspectorResponse okHttpInspectorResponse = new OkHttpInspectorResponse(requestId, response.code(), response.header(CONTENT_LENGTH), mResponseEndTime - mResponseStartTime);

            //if response does not have content length, using CountingInputStream to read its bytes
            if (!okHttpInspectorResponse.hasContentLength()) {
                ResponseBody body = response.body();
                InputStream responseStream = null;
                if (body != null) {
                    try {
                        responseStream = body.byteStream();
                    } catch (IOException e) {
                        if (mEventReporter.isReporterEnabled()) {
                            //notify event reporter in case there is any exception while getting the input stream of response
                            mEventReporter.responseInputStreamError(okHttpInspectorResponse, e);
                        }
                        throw e;
                    }
                }

                /**interpreting the response stream using CountingInputStream, once the counting is done,
                 notify the event reporter that response has been received*/
                responseStream = mEventReporter.interpretResponseStream(responseStream,
                        new DefaultResponseHandler(mEventReporter, okHttpInspectorResponse));

                //creating new response object using the interpreted stream
                response = response.newBuilder().body(new ForwardingResponseBody(body, responseStream)).build();
            } else {
                //if response has content length, notify the event reporter that response has been received.
                mEventReporter.responseReceived(okHttpInspectorResponse);
            }
        }

        return response;
    }

    /**
     * Implementation of {@link com.flipkart.flipperf.NetworkEventReporter.InspectorRequest}
     */
    private static class OkHttpInspectorRequest implements NetworkEventReporter.InspectorRequest {
        private final String mRequestId;
        private final String mRequestUrl;
        private final String mMethodType;
        private final String mContentLength;

        public OkHttpInspectorRequest(String requestId, String requestUrl, String methodType, String contentLength) {
            this.mRequestId = requestId;
            this.mRequestUrl = requestUrl;
            this.mMethodType = methodType;
            this.mContentLength = contentLength;
        }

        @Override
        public String requestId() {
            return mRequestId;
        }

        @Override
        public String url() {
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
    }

    /**
     * Implementation of {@link com.flipkart.flipperf.NetworkEventReporter.InspectorResponse}
     */
    private static class OkHttpInspectorResponse implements NetworkEventReporter.InspectorResponse {
        private final String mRequestId;
        private final long mResponseTime;
        private final int mStatusCode;
        private final String mResponseSize;

        public OkHttpInspectorResponse(String requestId, int statusCode, String responseSize, long responseTime) {
            this.mRequestId = requestId;
            this.mStatusCode = statusCode;
            this.mResponseSize = responseSize;
            this.mResponseTime = responseTime;
        }

        @Override
        public boolean hasContentLength() {
            return mResponseSize != null;
        }

        @Override
        public String requestId() {
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
        public long responseTime() {
            return mResponseTime;
        }
    }

    /**
     * Wrapper for {@link ResponseBody}
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

    /**
     * Builder Pattern for {@link NetworkInterceptor}
     */
    public static class Builder {
        private boolean mEnabled;
        private Handler mHandler;
        private NetworkEventReporter mNetworkEventReporter;

        /**
         * To enable disable the {@link NetworkEventReporter}
         *
         * @param enabled boolean
         * @return {@link Builder}
         */
        public Builder setEnabled(boolean enabled) {
            this.mEnabled = enabled;
            return this;
        }

        /**
         * Setting handler for background. Give it null if you want to use the handler of the library
         *
         * @param handler {@link Handler}
         * @return {@link Builder}
         */
        public Builder setHandler(Handler handler) {
            this.mHandler = handler;
            return this;
        }

        /**
         * Can leave it null for the default implementation
         *
         * @param networkEventReporter {@link NetworkEventReporter}
         * @return {@link Builder}
         */
        public Builder setEventReporter(NetworkEventReporter networkEventReporter) {
            this.mNetworkEventReporter = networkEventReporter;
            return this;
        }

        public NetworkInterceptor build(Context context) {
            return new NetworkInterceptor(context, this);
        }
    }
}
