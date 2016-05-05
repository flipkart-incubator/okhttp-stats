package com.flipkart.flipperf;

import android.content.Context;

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

/**
 * Created by anirudh.r on 02/05/16 at 12:51 PM.
 * Custom Interceptor to intercept all network calls.
 */
public final class NetworkInterceptor implements Interceptor {
    private final NetworkEventReporter mEventReporter;
    private final AtomicInteger mNextRequestId = new AtomicInteger(0);

    public NetworkInterceptor(Context context) {
        mEventReporter = new NetworkEventReporterImpl(true);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String requestId = String.valueOf(mNextRequestId.getAndIncrement());

        Request request = chain.request();
        if (mEventReporter.isReporterEnabled()) {
            OkHttpInspectorRequest okHttpInspectorRequest = new OkHttpInspectorRequest(requestId, request);
            mEventReporter.requestToBeSent(okHttpInspectorRequest);
        }

        Response response;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            if (mEventReporter.isReporterEnabled()) {
                mEventReporter.httpExchangeError(requestId, e);
            }
            throw e;
        }

        Response newResponse = response;

        if (mEventReporter.isReporterEnabled()) {

            OkHttpInspectorResponse okHttpInspectorResponse = new OkHttpInspectorResponse(requestId, request, response);
            mEventReporter.responseHeadersReceived(okHttpInspectorResponse);

            if (!okHttpInspectorResponse.hasContentLength()) {
                ResponseBody body = response.body();
                InputStream responseStream = null;
                if (body != null) {
                    responseStream = body.byteStream();
                }

                responseStream = mEventReporter.interpretResponseStream(responseStream,
                        new DefaultResponseHandler(mEventReporter, requestId));

                newResponse = response.newBuilder().body(new ForwardingResponseBody(body, responseStream)).build();
            }
        }

        return newResponse;
    }

    private static class OkHttpInspectorRequest implements NetworkEventReporter.InspectorRequest {
        private final String mRequestId;
        private final Request mRequest;

        public OkHttpInspectorRequest(String requestId, Request request) {
            mRequestId = requestId;
            mRequest = request;
        }

        @Override
        public String requestId() {
            return mRequestId;
        }

        @Override
        public String url() {
            return mRequest.urlString();
        }

        @Override
        public String method() {
            return mRequest.method();
        }

        @Override
        public String requestSize() {
            return firstHeaderValue("Content-Length");
        }

        private String firstHeaderValue(String name) {
            return mRequest.header(name);
        }
    }

    private static class OkHttpInspectorResponse implements NetworkEventReporter.InspectorResponse {
        private final String mRequestId;
        private final Response mResponse;
        private final Request mRequest;

        public OkHttpInspectorResponse(String requestId, Request request, Response response) {
            mRequestId = requestId;
            mResponse = response;
            mRequest = request;
        }

        @Override
        public boolean hasContentLength() {
            return mResponse.header("Content-Length") != null;
        }

        @Override
        public String requestId() {
            return mRequestId;
        }

        @Override
        public String url() {
            return mRequest.urlString();
        }

        @Override
        public int statusCode() {
            return mResponse.code();
        }

        @Override
        public String responseSize() {
            return mResponse.header("Content-Length");
        }
    }

    private static class ForwardingResponseBody extends ResponseBody {
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
