package com.flipkart.okhttpstats.handler;

import android.net.NetworkInfo;

import com.flipkart.okhttpstats.model.RequestStats;

import java.io.IOException;

/**
 * This interface to be consumed by the client, to get callbacks based on successful / unsuccessful responses and errors
 */
interface OnServerResponseReceivedListener {

    /**
     * This callback will be invoked for all the successful response (2XX and 3XX)
     *
     * @param info         : {@link NetworkInfo}
     * @param requestStats : {@link RequestStats}
     */
    void onResponseSuccess(NetworkInfo info, RequestStats requestStats);

    /**
     * This callback will be invoked for all the unsuccessful response (4XX and 5XX)
     *
     * @param info         : {@link NetworkInfo}
     * @param requestStats : {@link RequestStats}
     */
    void onResponseServerError(NetworkInfo info, RequestStats requestStats);

    /**
     * This callback includes failure request cases, in cases when there are no response such as NoInternet and more
     *
     * @param info         : {@link NetworkInfo}
     * @param requestStats : {@link RequestStats}
     * @param e : {@link Exception e}
     */
    void onResponseNetworkError(NetworkInfo info, RequestStats requestStats, Exception e);
}