package com.flipkart.flipperf.newlib.reporter;

import com.flipkart.flipperf.newlib.handler.NetworkRequestStatsHandler;
import com.flipkart.flipperf.newlib.model.RequestStats;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

/**
 * Created by anirudh.r on 09/05/16 at 12:32 PM.
 * <p/>
 * Whenever we receive {@link NetworkEventReporterImpl#responseReceived(InspectorRequest, InspectorResponse)} callback
 * <p/>
 * In case of any {@link IOException} during the {@link com.squareup.okhttp.Interceptor.Chain#proceed(Request)},
 * {@link NetworkEventReporter#httpExchangeError(InspectorRequest, IOException)} gets called with appropriate error message.
 * <p/>
 * In case of any {@link IOException} during the {@link ResponseBody#byteStream()}, {@link NetworkEventReporter#responseInputStreamError(InspectorRequest, InspectorResponse, IOException)}
 * <p/>
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
    public void responseInputStreamError(final InspectorRequest inspectorRequest, final InspectorResponse inspectorResponse, final IOException e) {
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
