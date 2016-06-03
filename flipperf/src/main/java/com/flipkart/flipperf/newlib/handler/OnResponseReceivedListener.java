package com.flipkart.flipperf.newlib.handler;

import android.net.NetworkInfo;

import com.flipkart.flipperf.newlib.model.RequestStats;

import java.io.IOException;

public interface OnResponseReceivedListener {

    /**
     * To be implemented by client, gets a callback for a successful request
     *
     * @param info         {@link NetworkInfo}
     * @param requestStats {@link RequestStats}
     */
    void onResponseSuccess(NetworkInfo info, RequestStats requestStats);

    /**
     * To be implemented by client, gets a callback for a failure request
     *
     * @param info         {@link NetworkInfo}
     * @param requestStats {@link RequestStats}
     * @param e            {@link IOException}
     */
    void onResponseError(NetworkInfo info, RequestStats requestStats, IOException e);
}