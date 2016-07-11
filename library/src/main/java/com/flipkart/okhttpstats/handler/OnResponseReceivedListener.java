package com.flipkart.okhttpstats.handler;

import android.net.NetworkInfo;

import com.flipkart.okhttpstats.model.RequestStats;

import java.io.IOException;

/**
 * This interface to be consumed by the client, to get callbacks on success/failure cases
 * It is more of a generic listener, where if server gives back any response {@link OnResponseReceivedListener#onResponseSuccess(NetworkInfo, RequestStats)} gets called, independent of the status code.
 * When there is no response, or any error before server responds {@link OnResponseReceivedListener#onResponseError(NetworkInfo, RequestStats, Exception)} gets called
 * <p>
 * If the client wants callback more specific to server status code such as 2XX, 3XX,4XX and 5XX, implement the {@link OnServerResponseReceivedListener}
 */
interface OnResponseReceivedListener {

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