package com.flipkart.flipperf.newlib;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import okio.BufferedSource;
import okio.Okio;

/*
 * Custom Interceptor to intercept all network calls.
 */
public final class NetworkInterceptor implements Interceptor {

    private Logger logger = LoggerFactory.getLogger(NetworkInterceptor.class);
    private static final String HANDLER_THREAD_NAME = " com.flipkart.flipperf.networkInterceptor";
    private static final String CONTENT_LENGTH = "Content-Length";
    private final NetworkEventReporter mEventReporter;
    private final AtomicInteger mNextRequestId = new AtomicInteger(1);

    private NetworkInterceptor(Context context, Builder builder) {
        Handler handler = builder.mHandler;

        //if handler given by client is null, create a newlib handler
        if (handler == null) {
            HandlerThread handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
            handlerThread.start();
            handler = new Handler(handlerThread.getLooper());
        }

        NetworkManager mNetworkManager = builder.mNetworkManager == null ? new NetworkStatManager(context) : builder.mNetworkManager;
        mEventReporter = builder.mNetworkEventReporter == null ? new NetworkEventReporterImpl() : builder.mNetworkEventReporter;
        mEventReporter.onInitialized(context, handler, mNetworkManager);
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
        final int requestId = mNextRequestId.getAndIncrement();

        Request request = chain.request();
        OkHttpInspectorRequest okHttpInspectorRequest = null;

        if (mEventReporter.isReporterEnabled()) {
            okHttpInspectorRequest = new OkHttpInspectorRequest(requestId, request.url(), request.method(), request.header(CONTENT_LENGTH), request.header("HOST"));
        }

        long mStartTime, mEndTime;
        Response response;
        try {
            //note the time taken for the response
            mStartTime = System.currentTimeMillis();
            response = chain.proceed(request);
            mEndTime = System.currentTimeMillis();
        } catch (final IOException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error received while proceeding response {}", e.getMessage());
            }
            if (mEventReporter.isReporterEnabled()) {
                //notify event reporter in case there is any exception while proceeding request
                mEventReporter.httpExchangeError(okHttpInspectorRequest, e);
            }
            throw e;
        }

        if (mEventReporter.isReporterEnabled()) {
            final OkHttpInspectorResponse okHttpInspectorResponse = new OkHttpInspectorResponse(requestId, response.code(), response.header(CONTENT_LENGTH), mStartTime, mEndTime);

            //if response does not have content length, using CountingInputStream to read its bytes
            if (!okHttpInspectorResponse.hasContentLength()) {
                ResponseBody body = response.body();
                InputStream responseStream = null;
                if (body != null) {
                    try {
                        responseStream = body.byteStream();
                    } catch (IOException e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Error received while reading input stream {}", e.getMessage());
                        }

                        if (mEventReporter.isReporterEnabled()) {
                            //notify event reporter in case there is any exception while getting the input stream of response
                            mEventReporter.responseInputStreamError(okHttpInspectorRequest, okHttpInspectorResponse, e);
                        }
                        throw e;
                    }
                }

                //interpreting the response stream using CountingInputStream, once the counting is done, notify the event reporter that response has been received
                responseStream = mEventReporter.interpretResponseStream(responseStream,
                        new DefaultResponseHandler(mEventReporter, okHttpInspectorRequest, okHttpInspectorResponse));

                //creating newlib response object using the interpreted stream
                response = response.newBuilder().body(new ForwardingResponseBody(body, responseStream)).build();
            } else {
                //if response has content length, notify the event reporter that response has been received.
                mEventReporter.responseReceived(okHttpInspectorRequest, okHttpInspectorResponse);
            }
        }
        return response;
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
        private final int mRequestId;
        private final long mStartTime;
        private final long mEndTime;
        private final int mStatusCode;
        private final String mResponseSize;

        public OkHttpInspectorResponse(int requestId, int statusCode, String responseSize, long startTime, long endTime) {
            this.mRequestId = requestId;
            this.mStatusCode = statusCode;
            this.mResponseSize = responseSize;
            this.mStartTime = startTime;
            this.mEndTime = endTime;
        }

        @Override
        public boolean hasContentLength() {
            return mResponseSize != null;
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

    /**
     * Builder Pattern for {@link NetworkInterceptor}
     */
    public static class Builder {
        private boolean mEnabled;
        private Handler mHandler;
        private NetworkEventReporter mNetworkEventReporter;
        private NetworkManager mNetworkManager;

        /**
         * To enable/disable the {@link NetworkEventReporter}
         *
         * @param enabled boolean
         * @return {@link Builder}
         */
        public Builder setReporterEnabled(boolean enabled) {
            this.mEnabled = enabled;
            return this;
        }

        /**
         * Setting handler for background operations. Leaving it null, the library will create its own handler
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

        /**
         * @param networkManager {@link NetworkManager}
         * @return {@link Builder}
         */
        public Builder setNetworkManager(NetworkManager networkManager) {
            this.mNetworkManager = networkManager;
            return this;
        }

        public NetworkInterceptor build(Context context) {
            return new NetworkInterceptor(context, this);
        }
    }
}
