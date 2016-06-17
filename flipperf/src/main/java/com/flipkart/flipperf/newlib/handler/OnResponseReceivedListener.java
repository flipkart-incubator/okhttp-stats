package com.flipkart.flipperf.newlib.handler;

import android.net.NetworkInfo;

import com.flipkart.flipperf.newlib.model.RequestStats;

import java.io.IOException;

/**
 * This interface to be consumed by the client, to get callbacks on success/failure cases
 */
public interface OnResponseReceivedListener {

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
    void onResponseError(NetworkInfo info, RequestStats requestStats, IOException e);
}