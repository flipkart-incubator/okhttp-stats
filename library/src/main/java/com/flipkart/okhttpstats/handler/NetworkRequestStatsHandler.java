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