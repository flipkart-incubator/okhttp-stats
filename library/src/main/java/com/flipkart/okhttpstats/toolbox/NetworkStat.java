/*
 *
 *  * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

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