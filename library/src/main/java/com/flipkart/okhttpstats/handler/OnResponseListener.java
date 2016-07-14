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

import android.net.NetworkInfo;

import com.flipkart.okhttpstats.model.RequestStats;

import java.io.IOException;

/**
 * This interface to be consumed by the client, to get callbacks on success/failure cases
 * It is more of a generic listener, where if server gives back any response {@link OnResponseListener#onResponseSuccess(NetworkInfo, RequestStats)} gets called, independent of the status code.
 * When there is no response, or any error before server responds {@link OnResponseListener#onResponseError(NetworkInfo, RequestStats, Exception)} gets called
 * <p>
 * If the client wants callback more specific to server status code such as 2XX, 3XX, 4XX and 5XX, implement the {@link ForwardingResponse}
 */
public interface OnResponseListener {

    /**
     * This callback includes response with 2XX, 3XX, 4XX, 5XX status codes
     *
     * @param info         {@link NetworkInfo}
     * @param requestStats {@link RequestStats}
     */
    void onResponseSuccess(NetworkInfo info, RequestStats requestStats);

    /**
     * This callback includes failure request cases, in cases when there are no response such as NoInternet and more
     *
     * @param info         {@link NetworkInfo}
     * @param requestStats {@link RequestStats}
     * @param e            {@link IOException}
     */
    void onResponseError(NetworkInfo info, RequestStats requestStats, Exception e);
}