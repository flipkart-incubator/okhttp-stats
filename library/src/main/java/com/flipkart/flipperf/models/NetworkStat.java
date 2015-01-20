package com.flipkart.flipperf.models;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.flipkart.flipperf.trackers.APIEvent;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by nikhil.n on 15/01/15.
 */
public class NetworkStat {

    private double currentAvgSpeed;
    private double totalSize;
    private Queue<APIEvent> apiEventQueue;
    private static int MAX_QUEUE_SIZE = 5;
    private Handler mHandler;
    private final String TAG = NetworkStat.class.getName();

    public NetworkStat() {
        Looper.prepare();
        apiEventQueue = new LinkedList<APIEvent>();
        mHandler = new Handler(Looper.getMainLooper());
    }

    public synchronized void addAPiEvent(final APIEvent apiEvent) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                apiEventQueue.add(apiEvent);
                totalSize += apiEvent.getResponseSize();
                if(apiEventQueue.size() > MAX_QUEUE_SIZE) {
                    APIEvent lastEvent = apiEventQueue.poll();
                    totalSize -= lastEvent.getResponseSize();
                }
                calculateAvgSpeed();
            }
        };
        mHandler.post(runnable);
    }

    private void calculateAvgSpeed() {
        Log.d(TAG, "total size = "+totalSize);
        double newAvgSpeed = 0;
        for(APIEvent apiEvent : apiEventQueue) {
            double proportion = apiEvent.getResponseSize() / totalSize;
            newAvgSpeed += apiEvent.getApiSpeed() * proportion;
            Log.d(TAG, "response size = "+apiEvent.getResponseSize() +" Rtt = "+apiEvent.getRtt()+ " proportion = "+proportion + " apispeed "+apiEvent.getApiSpeed()+" newavg = "+newAvgSpeed);
        }
        currentAvgSpeed = newAvgSpeed;
    }

    public double getCurrentAvgSpeed() {
        return currentAvgSpeed;
    }

    public void setCurrentAvgSpeed(double currentAvgSpeed) {
        this.currentAvgSpeed = currentAvgSpeed;
    }

    public double getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(double totalSize) {
        this.totalSize = totalSize;
    }

}
