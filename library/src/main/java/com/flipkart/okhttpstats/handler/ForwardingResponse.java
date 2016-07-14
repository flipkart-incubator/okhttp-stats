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
import com.flipkart.okhttpstats.toolbox.HTTPStatusCode;

public class ForwardingResponse implements OnResponseListener {

    private OnStatusCodeAwareResponseListener mOnStatusCodeAwareResponseListener;

    public ForwardingResponse(OnStatusCodeAwareResponseListener onStatusCodeAwareResponseListener) {
        mOnStatusCodeAwareResponseListener = onStatusCodeAwareResponseListener;
    }

    @Override
    public void onResponseSuccess(NetworkInfo info, RequestStats requestStats) {
        if (requestStats != null) {
            int statusCode = requestStats.getStatusCode();
            if ((statusCode >= HTTPStatusCode.HTTP_2XX_START && statusCode <= HTTPStatusCode.HTTP_2XX_END) ||
                    (statusCode >= HTTPStatusCode.HTTP_3XX_START && statusCode <= HTTPStatusCode.HTTP_3XX_END)) {
                mOnStatusCodeAwareResponseListener.onResponseServerSuccess(info, requestStats);
            } else if ((statusCode >= HTTPStatusCode.HTTP_4XX_START && statusCode <= HTTPStatusCode.HTTP_4XX_END) ||
                    (statusCode >= HTTPStatusCode.HTTP_5XX_START && statusCode <= HTTPStatusCode.HTTP_5XX_END)) {
                mOnStatusCodeAwareResponseListener.onResponseServerError(info, requestStats);
            }
        }
    }

    @Override
    public void onResponseError(NetworkInfo info, RequestStats requestStats, Exception e) {
        mOnStatusCodeAwareResponseListener.onResponseNetworkError(info, requestStats, e);
    }
}