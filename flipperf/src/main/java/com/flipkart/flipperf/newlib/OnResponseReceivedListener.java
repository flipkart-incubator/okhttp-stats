package com.flipkart.flipperf.newlib;

import com.flipkart.flipperf.model.RequestStats;

/**
 * Created by anirudh.r on 11/05/16 at 3:48 PM.
 */
public interface OnResponseReceivedListener {
    void onResponseReceived(RequestStats requestStats);
}
