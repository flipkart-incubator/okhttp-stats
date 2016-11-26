/*
 * The MIT License
 *
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
            if (requestStats.endTime > requestStats.startTime) {
                apiSpeed = requestStats.responseSize / (requestStats.endTime - requestStats.startTime);
            }
            if (apiSpeed > mPeakSpeed) {
                mPeakSpeed = apiSpeed;
            }
            mRequestStatQueue.add(requestStats);
            mTotalSize += requestStats.responseSize;
            if (mRequestStatQueue.size() > MAX_QUEUE_SIZE) {
                RequestStats requestStat = mRequestStatQueue.poll();
                mTotalSize -= requestStat.responseSize;
            }
            calculateAvgSpeed();
        }
    }

    private void calculateAvgSpeed() {
        double newAvgSpeed = 0;
        for (RequestStats requestStats : mRequestStatQueue) {
            long apiSpeed = 0;
            if (requestStats.endTime > requestStats.startTime) {
                apiSpeed = requestStats.responseSize / (requestStats.endTime - requestStats.startTime);
            }
            double proportion = requestStats.responseSize / mTotalSize;
            newAvgSpeed += apiSpeed * proportion;
        }
        mCurrentAvgSpeed = newAvgSpeed;
    }
}