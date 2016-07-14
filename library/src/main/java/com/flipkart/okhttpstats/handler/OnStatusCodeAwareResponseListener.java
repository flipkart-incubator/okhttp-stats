package com.flipkart.okhttpstats.handler;

import android.net.NetworkInfo;

import com.flipkart.okhttpstats.model.RequestStats;

/**
 *
 */
public interface OnStatusCodeAwareResponseListener {
    void onResponseServerSuccess(NetworkInfo networkInfo, RequestStats requestStats);

    void onResponseServerError(NetworkInfo networkInfo, RequestStats requestStats);

    void onResponseNetworkError(NetworkInfo info, RequestStats requestStats, Exception e);
}