package com.flipkart.flipperf;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.flipkart.flipperf.model.RequestResponseModel;
import com.flipkart.flipperf.network.NetworkStat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anirudh.r on 10/05/16 at 11:59 AM.
 */
public class NetworkStatManager implements NetworkManager {

    private static final String TAG = NetworkStatManager.class.getName();

    @VisibleForTesting
    public List<OnResponseReceivedListener> getOnResponseReceivedListenerList() {
        return mOnResponseReceivedListenerList;
    }

    private List<OnResponseReceivedListener> mOnResponseReceivedListenerList = new ArrayList<>();
    private final FlipperfPreferenceManager flipperfPreferenceManager;
    private int responseCount = 0;
    private String mNetworkType;

    public NetworkStatManager(Context context) {
        flipperfPreferenceManager = new FlipperfPreferenceManager(context);
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
    public void flush() {
        flipperfPreferenceManager.setAverageSpeed(mNetworkType, NetworkStat.getAverageSpeed());
    }

    @Override
    public void setNetworkType(String networkType) {
        this.mNetworkType = networkType;
    }

    @Override
    public void onResponseReceived(RequestResponseModel requestResponseModel) {
        responseCount += 1;
        for (OnResponseReceivedListener onResponseReceivedListener : mOnResponseReceivedListenerList) {
            if (onResponseReceivedListener != null) {
                onResponseReceivedListener.onResponseReceived(requestResponseModel);
            }
        }
        NetworkStat.calculateNetworkAvgSpeed(requestResponseModel, responseCount);
    }

    @Override
    public void onHttpExchangeError(RequestResponseModel requestResponseModel) {
        for (OnResponseReceivedListener onResponseReceivedListener : mOnResponseReceivedListenerList) {
            if (onResponseReceivedListener != null) {
                onResponseReceivedListener.onHttpErrorReceived(requestResponseModel);
            }
        }
    }

    @Override
    public void onResponseInputStreamError(RequestResponseModel requestResponseModel) {
        for (OnResponseReceivedListener onResponseReceivedListener : mOnResponseReceivedListenerList) {
            if (onResponseReceivedListener != null) {
                onResponseReceivedListener.onInputStreamReadError(requestResponseModel);
            }
        }
    }
}
