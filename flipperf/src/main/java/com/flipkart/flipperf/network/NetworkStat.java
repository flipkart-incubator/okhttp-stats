package com.flipkart.flipperf.network;

import android.os.Handler;
import android.util.Log;

import com.flipkart.flipperf.model.RequestResponseModel;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by anirudh.r on 08/05/16 at 1:17 AM.
 */
public class NetworkStat {

    private static final String TAG = NetworkStat.class.getName();
    private int MAX_QUEUE_SIZE = 5;
    private double mTotalSize;
    private double mPeakSize;
    private Handler mHandler;
    private Queue<RequestResponseModel> mResponseModelQueue;

    public NetworkStat(Handler handler, int maxQueueSize) {
        this.mHandler = handler;
        this.MAX_QUEUE_SIZE = maxQueueSize;
        this.mResponseModelQueue = new LinkedList<>();
    }

    public synchronized void addResponseModel(final RequestResponseModel requestResponseModel) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (requestResponseModel.getApiSpeed() > mPeakSize) {
                    mPeakSize = requestResponseModel.getApiSpeed();
                }

                mResponseModelQueue.add(requestResponseModel);
                mTotalSize = Double.parseDouble(requestResponseModel.getResponseSize());
                if (mResponseModelQueue.size() >= MAX_QUEUE_SIZE) {
                    RequestResponseModel requestResponse = mResponseModelQueue.poll();
                    mTotalSize = Double.parseDouble(requestResponse.getResponseSize());
                }
                calculateAverageSpeed();
            }
        });
    }

    private void calculateAverageSpeed() {
        Log.d(TAG, "total size : " + mTotalSize);
        double newAvgSpeed;

        for (RequestResponseModel requestResponseModel : mResponseModelQueue) {
            double proportion = Double.parseDouble(requestResponseModel.getResponseSize()) / mTotalSize;
            newAvgSpeed = requestResponseModel.getApiSpeed() * proportion;
            Log.d(TAG, "calculateAverageSpeed: \nresponse size : " + requestResponseModel.getResponseSize()
                    + "\nproportion : " + proportion
                    + "\napi speed : " + requestResponseModel.getApiSpeed()
                    + "\nnew avg : " + newAvgSpeed);
        }
    }
}
