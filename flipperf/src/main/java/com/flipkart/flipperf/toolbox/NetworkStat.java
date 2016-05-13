package com.flipkart.flipperf.toolbox;

import com.flipkart.flipperf.model.RequestStats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anirudh.r on 08/05/16 at 1:17 AM.
 */
public final class NetworkStat {

    private static float mTotalSize = 0F;
    private static float mTotalTime = 0F;
    private static List<RequestStats> mRequestStatsList = new ArrayList<>();

    private NetworkStat() {
    }

    public static float getAverageSpeed() {
        for (RequestStats requestStats : mRequestStatsList) {
            mTotalSize += Float.parseFloat(requestStats.getResponseSize());
            mTotalTime += (requestStats.getEndTime() - requestStats.getStartTime());
        }
        return (mTotalSize / mTotalTime) / mRequestStatsList.size();
    }

    public static void reset() {
        mTotalSize = 0F;
        mTotalTime = 0F;
        mRequestStatsList.clear();
    }

    public synchronized static void addResponseData(RequestStats requestStats) {
        mRequestStatsList.add(requestStats);
    }
}