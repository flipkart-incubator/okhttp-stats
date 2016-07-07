package com.flipkart.okhttpstats.toolbox;

import com.flipkart.okhttpstats.model.RequestStats;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Utility class to calculate the average network speed
 */
public final class NetworkStat {

    private static final int MAX_QUEUE_SIZE = 5;
    private double mPeakSpeed = 0;
    private Queue<RequestStats> mRequestStatQueue;
    private double mTotalSize = 0;
    private double mCurrentAvgSpeed = 0;

    public NetworkStat() {
        mRequestStatQueue = new LinkedList<>();
    }

    public double getCurrentAvgSpeed() {
        return mCurrentAvgSpeed;
    }

    public synchronized void addRequestStat(final RequestStats requestStats) {
        if (requestStats != null) {
            long apiSpeed = 0;
            if (requestStats.getEndTime() > requestStats.getStartTime()) {
                apiSpeed = requestStats.getResponseSize() / (requestStats.getEndTime() - requestStats.getStartTime());
            }
            if (apiSpeed > mPeakSpeed) {
                mPeakSpeed = apiSpeed;
            }
            mRequestStatQueue.add(requestStats);
            mTotalSize += requestStats.getResponseSize();
            if (mRequestStatQueue.size() > MAX_QUEUE_SIZE) {
                RequestStats requestStat = mRequestStatQueue.poll();
                mTotalSize -= requestStat.getResponseSize();
            }
            calculateAvgSpeed();
        }
    }

    private void calculateAvgSpeed() {
        double newAvgSpeed = 0;
        for (RequestStats requestStats : mRequestStatQueue) {
            long apiSpeed = 0;
            if (requestStats.getEndTime() > requestStats.getStartTime()) {
                apiSpeed = requestStats.getResponseSize() / (requestStats.getEndTime() - requestStats.getStartTime());
            }
            double proportion = requestStats.getResponseSize() / mTotalSize;
            newAvgSpeed += apiSpeed * proportion;
        }
        mCurrentAvgSpeed = newAvgSpeed;
    }
}