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