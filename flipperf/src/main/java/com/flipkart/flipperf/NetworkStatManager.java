package com.flipkart.flipperf;

import android.content.Context;
import android.net.NetworkInfo;
import android.support.annotation.VisibleForTesting;

import com.flipkart.flipperf.model.RequestStats;
import com.flipkart.flipperf.toolbox.FlipperfPreferenceManager;
import com.flipkart.flipperf.toolbox.NetworkStat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anirudh.r on 10/05/16 at 11:59 AM.
 */
public class NetworkStatManager implements NetworkManager {

    private static final int DEFAULT_MAX_SIZE = 10;
    private final FlipperfPreferenceManager mFlipperfPreferenceManager;
    private Logger mLogger = LoggerFactory.getLogger(NetworkStatManager.class);
    private List<OnResponseReceivedListener> mOnResponseReceivedListenerList = new ArrayList<>();
    private int mResponseCount = 0;
    private NetworkInfo mNetworkInfo;
    private int MAX_SIZE;

    public NetworkStatManager(Context context) {
        this.mFlipperfPreferenceManager = new FlipperfPreferenceManager(context);
        this.MAX_SIZE = DEFAULT_MAX_SIZE;
    }

    @VisibleForTesting
    public List<OnResponseReceivedListener> getOnResponseReceivedListenerList() {
        return mOnResponseReceivedListenerList;
    }

    @Override
    public void addListener(OnResponseReceivedListener onResponseReceivedListener) {
        if (mOnResponseReceivedListenerList != null) {
            mOnResponseReceivedListenerList.add(onResponseReceivedListener);
        }
    }

    @Override
    public void unregisterListener(OnResponseReceivedListener onResponseReceivedListener) {
        if (mOnResponseReceivedListenerList != null) {
            mOnResponseReceivedListenerList.remove(onResponseReceivedListener);
        }
    }

    @Override
    public void setNetworkType(NetworkInfo networkType) {
        this.mNetworkInfo = networkType;
    }

    @Override
    public void setMaxSize(int size) {
        this.MAX_SIZE = size;
    }

    @Override
    public void onResponseReceived(RequestStats requestStats) {
        mResponseCount += 1;
        if (mLogger.isDebugEnabled()) {
            mLogger.debug("Response Received : {}", requestStats);
        }

        for (OnResponseReceivedListener onResponseReceivedListener : mOnResponseReceivedListenerList) {
            if (onResponseReceivedListener != null) {
                onResponseReceivedListener.onResponseReceived(requestStats);
            }
        }

        //save to shared prefs if condition is satisfied
        if (mResponseCount >= MAX_SIZE) {
            if (mLogger.isDebugEnabled()) {
                mLogger.debug("Response Count reached, Saved to shared preference , Avg Speed : {}", NetworkStat.getAverageSpeed());
            }
            saveToSharedPreference();
        }

        //adding response to list
        NetworkStat.addResponseData(requestStats);
    }

    @Override
    public void onHttpExchangeError(RequestStats requestStats) {
        if (mLogger.isDebugEnabled()) {
            mLogger.debug("Response Received With Http Exchange Error : {}", requestStats);
        }
        for (OnResponseReceivedListener onResponseReceivedListener : mOnResponseReceivedListenerList) {
            if (onResponseReceivedListener != null) {
                onResponseReceivedListener.onResponseReceived(requestStats);
            }
        }
    }

    @Override
    public void onResponseInputStreamError(RequestStats requestStats) {
        if (mLogger.isDebugEnabled()) {
            mLogger.debug("Response Received With InputStream Error : {}", requestStats);
        }
        for (OnResponseReceivedListener onResponseReceivedListener : mOnResponseReceivedListenerList) {
            if (onResponseReceivedListener != null) {
                onResponseReceivedListener.onResponseReceived(requestStats);
            }
        }
    }

    private void saveToSharedPreference() {
        mFlipperfPreferenceManager.setAverageSpeed(mNetworkInfo.getTypeName(), NetworkStat.getAverageSpeed());
        NetworkStat.reset();
        mResponseCount = 0;
    }
}