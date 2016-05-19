package com.flipkart.flipperf.newlib;

import android.net.NetworkInfo;

import com.flipkart.flipperf.model.RequestStats;
import com.flipkart.flipperf.toolbox.NetworkSpeed;

/**
 * Created by anirudh.r on 11/05/16 at 3:41 PM.
 */
public interface NetworkManager {
    void onResponseReceived(RequestStats requestStats);

    void onHttpExchangeError(RequestStats requestStats);

    void onResponseInputStreamError(RequestStats requestStats);

    void addListener(OnResponseReceivedListener onResponseReceivedListener);

    void removeListener(OnResponseReceivedListener onResponseReceivedListener);

    void setNetworkType(NetworkInfo networkType);

    NetworkInfo getNetworkInfo();

    NetworkSpeed getNetworkSpeed();

    void setMaxSize(int size);

    float getAverageNetworkSpeed();
}
