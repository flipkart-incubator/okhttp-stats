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

package com.flipkart.okhttpstats.handler;

import android.support.annotation.Nullable;

import com.flipkart.okhttpstats.model.RequestStats;

import java.io.IOException;

public interface NetworkRequestStatsHandler {

    /**
     * Indicates a successful response was received.
     *
     * @param requestStats {@link RequestStats}
     */
    void onResponseReceived(RequestStats requestStats);

    /**
     * Indicates the connection failed, which implies that we dont have information like response status code, size etc.
     * Typically, socket timeouts, connect errors etc.
     *
     * @param requestStats {@link RequestStats}
     */
    void onHttpExchangeError(RequestStats requestStats, @Nullable IOException e);

    /**
     * Indicates that connection was successful, but we could not read the response stream.
     * This implies that we have access to response status code etc.
     *
     * @param requestStats {@link RequestStats}
     * @param e            {@link IOException}
     */
    void onResponseInputStreamError(RequestStats requestStats, @Nullable Exception e);
}