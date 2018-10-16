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

package com.flipkart.okhttpstats;

import com.flipkart.okhttpstats.interpreter.NetworkInterpreter;
import com.flipkart.okhttpstats.toolbox.Utils;

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
    private final boolean mEnabled;

    NetworkInterceptor(Builder builder) {
        mEnabled = builder.mEnabled;
        if (builder.mInterpreter == null) {
            throw new IllegalStateException("NetworkInterpreter cannot be null");
        }
        mInterpreter = builder.mInterpreter;
        Utils.isLoggingEnabled = builder.mIsLoggingEnabled;
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
        boolean mEnabled = true;
        boolean mIsLoggingEnabled = false;
        NetworkInterpreter mInterpreter;

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

        /**
         * To enable/disable logging. By default logging is disabled.
         *
         * @param isLoggingEnabled : boolean
         * @return {@link Builder}
         */
        public Builder setLoggingEnabled(boolean isLoggingEnabled) {
            this.mIsLoggingEnabled = isLoggingEnabled;
            return this;
        }

        public NetworkInterceptor build() {
            return new NetworkInterceptor(this);
        }
    }
}