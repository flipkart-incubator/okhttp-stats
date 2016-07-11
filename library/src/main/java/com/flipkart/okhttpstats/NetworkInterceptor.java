package com.flipkart.okhttpstats;

import android.content.Context;

import com.flipkart.okhttpstats.interpreter.NetworkInterpreter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class is the entry point for this library.
 * A OKHttp {@link Interceptor} which intercepts all requests & responses and passes it on to the {@link NetworkInterpreter} along with {@link TimeInfo}
 * All network related exceptions are also bubbled to the same interpreter.
 */
public final class NetworkInterceptor implements Interceptor {

    private final NetworkInterpreter mInterpreter;
    private final AtomicInteger mNextRequestId = new AtomicInteger(1);
    private boolean mEnabled = true;

    private NetworkInterceptor(Builder builder) {
        mEnabled = builder.mEnabled;
        if (builder.mInterpreter == null) {
            throw new IllegalStateException("NetworkInterpreter cannot be null");
        }
        mInterpreter = builder.mInterpreter;
    }

    /**
     * Method to intercept all the network calls.
     *
     * @param chain {@link Interceptor.Chain}
     * @return {@link Response}
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        final int requestId = mNextRequestId.getAndIncrement();
        Request request = chain.request();
        TimeInfo timeInfo = new TimeInfo();
        Response response;
        try {
            //note the time taken for the response
            timeInfo.mStartTime = System.currentTimeMillis();
            response = chain.proceed(request);
            timeInfo.mEndTime = System.currentTimeMillis();
        } catch (final IOException e) {
            if (mEnabled) {
                //notify event reporter in case there is any exception while proceeding request
                mInterpreter.interpretError(requestId, timeInfo, request, e);
            }
            throw e;
        }

        if (mEnabled) {
            response = mInterpreter.interpretResponseStream(requestId, timeInfo, request, response);
        }

        return response;
    }

    public static class TimeInfo {
        public long mStartTime;
        public long mEndTime;
    }

    /**
     * Builder Pattern for {@link NetworkInterceptor}
     */
    public static class Builder {
        private boolean mEnabled = true;
        private NetworkInterpreter mInterpreter;

        /**
         * To enable/disable the calls to {@link NetworkInterpreter}
         * If disabled, the interceptor continues to operate without reporting to the {@link NetworkInterpreter}
         *
         * @param enabled boolean
         * @return {@link Builder}
         */
        public Builder setEnabled(boolean enabled) {
            this.mEnabled = enabled;
            return this;
        }

        /**
         * Can leave it null for the default implementation
         *
         * @param interpreter {@link NetworkInterpreter}
         * @return {@link Builder}
         */
        public Builder setNetworkInterpreter(NetworkInterpreter interpreter) {
            this.mInterpreter = interpreter;
            return this;
        }

        public NetworkInterceptor build() {
            return new NetworkInterceptor(this);
        }
    }
}
