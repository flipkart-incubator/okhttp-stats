package com.flipkart.flipperf.newlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.flipkart.flipperf.newlib.model.RequestStats;
import com.flipkart.flipperf.newlib.response.CountingInputStream;
import com.flipkart.flipperf.newlib.response.ResponseHandler;
import com.flipkart.flipperf.newlib.toolbox.ExceptionType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by anirudh.r on 09/05/16 at 12:32 PM.
 * <p>
 * <p>
 * There are two cases by which we will get a response callback:
 * <p>
 * 1st Case : When response do not have Content-Length
 * Whenever we receive {@link NetworkEventReporterImpl#responseDataReceived(InspectorRequest, InspectorResponse, int)} callback
 * <p>
 * 2nd Case : When response have Content-Length
 * Whenever we receive {@link NetworkEventReporterImpl#responseReceived(InspectorRequest, InspectorResponse)} callback
 * <p>
 * In case of any {@link IOException} during the {@link com.squareup.okhttp.Interceptor.Chain#proceed(Request)},
 * {@link NetworkEventReporter#httpExchangeError(InspectorRequest, IOException)} gets called with appropriate error message.
 * <p>
 * In case of any {@link IOException} during the {@link ResponseBody#byteStream()}, {@link NetworkEventReporter#responseInputStreamError(InspectorRequest, InspectorResponse, IOException)}
 * <p>
 *
 *                     with content length
 *                  -------------------------->  {@link NetworkEventReporterImpl#responseReceived(InspectorRequest, InspectorResponse)}
 *                  |
 *                  |
 *                  |
 *      REQUEST --->
 *                  |
 *                  |
 *                  |  without content length
 *                  -------------------------->  {@link NetworkEventReporterImpl#responseDataReceived(InspectorRequest, InspectorResponse, int)}
 */
public class NetworkEventReporterImpl implements NetworkEventReporter {

    private Handler mHandler;
    private boolean mIsReporterEnabled = false;
    private NetworkManager mNetworkManager;
    private NetworkInfo mNetworkInfo;
    private ConnectivityManager mConnectivityManager;

    @Override
    public void onInitialized(Context context, Handler handler, NetworkManager networkManager) {
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.mHandler = handler;
        this.mNetworkManager = networkManager;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        this.mIsReporterEnabled = isEnabled;
    }

    @Override
    public boolean isReporterEnabled() {
        return mIsReporterEnabled;
    }

    @Override
    public void responseReceived(final InspectorRequest inspectorRequest, final InspectorResponse inspectorResponse) {
        final int requestId = inspectorResponse.requestId();
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

                RequestStats requestStats = new RequestStats(requestId);
                requestStats.setSize(inspectorRequest.requestSize());
                requestStats.setUrl(inspectorRequest.url());
                requestStats.setMethodType(inspectorRequest.method());
                requestStats.setHostName(inspectorRequest.hostName());
                requestStats.setResponseSize(inspectorResponse.responseSize());
                requestStats.setHttpStatusCode(inspectorResponse.statusCode());
                requestStats.setStartTime(inspectorResponse.startTime());
                requestStats.setEndTime(inspectorResponse.endTime());
                requestStats.setNetworkType(mNetworkInfo);

                mNetworkManager.setNetworkType(mNetworkInfo);
                mNetworkManager.onResponseReceived(requestStats);
            }
        });
    }

    @Override
    public void httpExchangeError(final InspectorRequest inspectorRequest, final IOException e) {
        final int requestId = inspectorRequest.requestId();
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                int exceptionType = ExceptionType.getExceptionType(e);
                mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

                RequestStats requestStats = new RequestStats(requestId);
                requestStats.setUrl(inspectorRequest.url());
                requestStats.setMethodType(inspectorRequest.method());
                requestStats.setHostName(inspectorRequest.hostName());
                requestStats.setSize(inspectorRequest.requestSize());
                requestStats.setNetworkType(mNetworkInfo);
                requestStats.setExceptionType(exceptionType);
                requestStats.setException(e);

                mNetworkManager.setNetworkType(mNetworkInfo);
                mNetworkManager.onHttpExchangeError(requestStats);
            }
        });
    }

    @Override
    public InputStream interpretResponseStream(@Nullable InputStream inputStream, ResponseHandler responseHandler) {
        return new CountingInputStream(inputStream, responseHandler);
    }

    @Override
    public void responseDataReceived(final InspectorRequest inspectorRequest, final InspectorResponse inspectorResponse, final int dataLength) {
        final int requestId = inspectorResponse.requestId();
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

                RequestStats requestStats = new RequestStats(requestId);
                requestStats.setSize(inspectorRequest.requestSize());
                requestStats.setUrl(inspectorRequest.url());
                requestStats.setMethodType(inspectorRequest.method());
                requestStats.setHostName(inspectorRequest.hostName());
                requestStats.setResponseSize(String.valueOf(dataLength));
                requestStats.setHttpStatusCode(inspectorResponse.statusCode());
                requestStats.setStartTime(inspectorResponse.startTime());
                requestStats.setEndTime(inspectorResponse.endTime());
                requestStats.setNetworkType(mNetworkInfo);

                mNetworkManager.setNetworkType(mNetworkInfo);
                mNetworkManager.onResponseReceived(requestStats);
            }
        });
    }

    @Override
    public void responseInputStreamError(final InspectorRequest inspectorRequest, final InspectorResponse inspectorResponse, final IOException e) {
        final int requestId = inspectorResponse.requestId();
        this.mHandler.post(new Runnable() {
            @Override
            public void run() {
                int exceptionType = ExceptionType.getExceptionType(e);
                mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

                RequestStats requestStats = new RequestStats(requestId);
                requestStats.setSize(inspectorRequest.requestSize());
                requestStats.setUrl(inspectorRequest.url());
                requestStats.setMethodType(inspectorRequest.method());
                requestStats.setHostName(inspectorRequest.hostName());
                requestStats.setHttpStatusCode(inspectorResponse.statusCode());
                requestStats.setStartTime(inspectorResponse.startTime());
                requestStats.setEndTime(inspectorResponse.endTime());
                requestStats.setNetworkType(mNetworkInfo);
                requestStats.setExceptionType(exceptionType);
                requestStats.setException(e);

                mNetworkManager.setNetworkType(mNetworkInfo);
                mNetworkManager.onResponseInputStreamError(requestStats);
            }
        });
    }
}
