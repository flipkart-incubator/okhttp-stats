/*
 *
 *  * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
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