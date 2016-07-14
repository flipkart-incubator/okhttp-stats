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

package com.flipkart.okhttpstats.reporter;

import com.flipkart.okhttpstats.handler.NetworkRequestStatsHandler;
import com.flipkart.okhttpstats.model.RequestStats;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * Created by anirudh.r on 09/05/16 at 12:32 PM.
 * Default implementation of {@link NetworkEventReporter}
 * <p>
 * In case of any {@link IOException} during the {@link Interceptor.Chain#proceed(Request)})},
 * {@link NetworkEventReporter#httpExchangeError(InspectorRequest, IOException)} gets called with appropriate error message.
 * <p/>
 * In case of any {@link Exception} during the {@link ResponseBody#byteStream()}, {@link NetworkEventReporter#responseInputStreamError(InspectorRequest, InspectorResponse, Exception)}
 * <p/>
 */
public class NetworkEventReporterImpl implements NetworkEventReporter {

    private NetworkRequestStatsHandler mNetworkRequestStatsHandler;

    public NetworkEventReporterImpl(NetworkRequestStatsHandler networkRequestStatsHandler) {
        this.mNetworkRequestStatsHandler = networkRequestStatsHandler;
    }

    @Override
    public void responseReceived(final InspectorRequest inspectorRequest, final InspectorResponse inspectorResponse) {
        if (inspectorRequest != null && inspectorResponse != null) {
            final int requestId = inspectorResponse.requestId();
            RequestStats requestStats = new RequestStats(requestId);
            requestStats.setRequestSize(inspectorRequest.requestSize());
            requestStats.setUrl(inspectorRequest.url());
            requestStats.setMethodType(inspectorRequest.method());
            requestStats.setHostName(inspectorRequest.hostName());
            requestStats.setResponseSize(inspectorResponse.responseSize());
            requestStats.setStatusCode(inspectorResponse.statusCode());
            requestStats.setStartTime(inspectorResponse.startTime());
            requestStats.setEndTime(inspectorResponse.endTime());
            mNetworkRequestStatsHandler.onResponseReceived(requestStats);
        }
    }

    @Override
    public void httpExchangeError(final InspectorRequest inspectorRequest, final IOException e) {
        if (inspectorRequest != null) {
            final int requestId = inspectorRequest.requestId();
            RequestStats requestStats = new RequestStats(requestId);
            requestStats.setUrl(inspectorRequest.url());
            requestStats.setMethodType(inspectorRequest.method());
            requestStats.setHostName(inspectorRequest.hostName());
            requestStats.setRequestSize(inspectorRequest.requestSize());
            mNetworkRequestStatsHandler.onHttpExchangeError(requestStats, e);
        }
    }

    @Override
    public void responseInputStreamError(final InspectorRequest inspectorRequest, final InspectorResponse inspectorResponse, final Exception e) {
        if (inspectorRequest != null && inspectorResponse != null) {
            final int requestId = inspectorResponse.requestId();
            RequestStats requestStats = new RequestStats(requestId);
            requestStats.setRequestSize(inspectorRequest.requestSize());
            requestStats.setUrl(inspectorRequest.url());
            requestStats.setMethodType(inspectorRequest.method());
            requestStats.setHostName(inspectorRequest.hostName());
            requestStats.setStatusCode(inspectorResponse.statusCode());
            requestStats.setStartTime(inspectorResponse.startTime());
            requestStats.setEndTime(inspectorResponse.endTime());
            mNetworkRequestStatsHandler.onResponseInputStreamError(requestStats, e);
        }
    }
}