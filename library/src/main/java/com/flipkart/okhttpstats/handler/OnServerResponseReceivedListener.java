package com.flipkart.okhttpstats.handler;

import android.net.NetworkInfo;

import com.flipkart.okhttpstats.model.RequestStats;

import java.io.IOException;

/**
 * This interface to be consumed by the client, to get callbacks based on status code and errors
 */
interface OnServerResponseReceivedListener{

    /**
     * This callback will be invoked for all the response with 2XX status codes
     *
     * @param info         : {@link NetworkInfo}
     * @param requestStats : {@link RequestStats}
     */
    void on2XXStatusResponseReceived(NetworkInfo info, RequestStats requestStats);

    /**
     * This callback will be invoked for all the response with 3XX status codes
     *
     * @param info         : {@link NetworkInfo}
     * @param requestStats : {@link RequestStats}
     */
    void on3XXStatusResponseReceived(NetworkInfo info, RequestStats requestStats);

    /**
     * This callback will be invoked for all the response with 4XX status codes
     *
     * @param info         : {@link NetworkInfo}
     * @param requestStats : {@link RequestStats}
     */
    void on4XXStatusResponseReceived(NetworkInfo info, RequestStats requestStats);

    /**
     * This callback will be invoked for all the response with 5XX status codes
     *
     * @param info         : {@link NetworkInfo}
     * @param requestStats : {@link RequestStats}
     */
    void on5XXStatusResponseReceived(NetworkInfo info, RequestStats requestStats);

    /**
     * This callback includes failure request cases, in cases when there are no response such as NoInternet and more
     *
     * @param info         {@link NetworkInfo}
     * @param requestStats {@link RequestStats}
     * @param e            {@link IOException}
     */
    void onResponseError(NetworkInfo info, RequestStats requestStats, Exception e);
}