package com.flipkart.flipperf.toolbox;

import com.flipkart.flipperf.model.RequestStats;

/**
 * Created by anirudh.r on 08/05/16 at 1:17 AM.
 */
public final class NetworkStat {

    private static float mTotalSize = 0F;
    private static float mTotalTime = 0F;
    private static int mTotalResponseCount = 0;

    private NetworkStat() {
    }

    public static synchronized void calculateNetworkAvgSpeed(final RequestStats requestStats, int responseCount) {
        mTotalSize += Float.parseFloat(requestStats.getResponseSize());
        mTotalTime += (requestStats.getEndTime() - requestStats.getStartTime());
        mTotalResponseCount = responseCount;
    }

    public static float getAverageSpeed() {
        float mTotalAPISpeed = mTotalSize / mTotalTime;
        return mTotalAPISpeed / mTotalResponseCount;
    }
}