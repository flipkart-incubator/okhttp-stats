package com.flipkart.flipperf.network;

import com.flipkart.flipperf.model.RequestResponseModel;

/**
 * Created by anirudh.r on 08/05/16 at 1:17 AM.
 */
public final class NetworkStat {

    private static float mTotalAPISpeed;
    private static int mTotalResponseCount;

    public static synchronized void calculateNetworkAvgSpeed(final RequestResponseModel requestResponseModel, int responseCount) {
        mTotalAPISpeed += requestResponseModel.getApiSpeed();
        mTotalResponseCount = responseCount;
    }

    public static float getAverageSpeed() {
        return mTotalAPISpeed / mTotalResponseCount;
    }
}
