package com.flipkart.okhttpstats.handler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.flipkart.okhttpstats.model.RequestStats;
import com.flipkart.okhttpstats.toolbox.NetworkStat;
import com.flipkart.okhttpstats.toolbox.PreferenceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PersistentStatsHandler implements NetworkRequestStatsHandler {

    private static final int DEFAULT_MAX_SIZE = 10;
    private static final String WIFI_NETWORK = "WIFI";
    private static final String MOBILE_NETWORK = "mobile";
    private static final String UNKNOWN_NETWORK = "unknown";
    private final PreferenceManager mPreferenceManager;
    private Logger mLogger = LoggerFactory.getLogger(PersistentStatsHandler.class);
    private List<OnResponseReceivedListener> mOnResponseReceivedListenerList = new ArrayList<>();
    private int mResponseCount = 0;
    private int MAX_SIZE;
    private WifiManager mWifiManager;
    private NetworkStat mNetworkStat;
    private float mCurrentAvgSpeed = 0;
    private ConnectivityManager mConnectivityManager;

    public PersistentStatsHandler(Context context) {
        this.mPreferenceManager = new PreferenceManager(context);
        this.MAX_SIZE = DEFAULT_MAX_SIZE;
        this.mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.mNetworkStat = new NetworkStat();
        this.mCurrentAvgSpeed = mPreferenceManager.getAverageSpeed(getNetworkKey(getActiveNetworkInfo()));
    }

    /**
     * Client can call this to get the current network info
     *
     * @return
     */
    public NetworkInfo getActiveNetworkInfo() {
        if (mConnectivityManager != null) {
            return mConnectivityManager.getActiveNetworkInfo();
        }
        return null;
    }

    @VisibleForTesting
    public List<OnResponseReceivedListener> getOnResponseReceivedListenerList() {
        return mOnResponseReceivedListenerList;
    }

    /**
     * Client can add listeners to listen for the callbacks.
     *
     * @param onResponseReceivedListener : {@link OnResponseReceivedListener}
     */
    public void addListener(OnResponseReceivedListener onResponseReceivedListener) {
        if (mOnResponseReceivedListenerList != null) {
            mOnResponseReceivedListenerList.add(onResponseReceivedListener);
        }
    }

    public void removeListener(OnResponseReceivedListener onResponseReceivedListener) {
        if (mOnResponseReceivedListenerList != null) {
            mOnResponseReceivedListenerList.remove(onResponseReceivedListener);
        }
    }

    public void setMaxSizeForPersistence(int size) {
        this.MAX_SIZE = size;
    }

    public float getAverageNetworkSpeed() {
        return mCurrentAvgSpeed;
    }

    @Override
    public void onResponseReceived(final RequestStats requestStats) {
        if (mLogger.isDebugEnabled()) {
            mLogger.debug("Response Received : {}", requestStats);
        }

        for (OnResponseReceivedListener onResponseReceivedListener : mOnResponseReceivedListenerList) {
            if (onResponseReceivedListener != null) {
                onResponseReceivedListener.onResponseSuccess(getActiveNetworkInfo(), requestStats);
            }
        }

        //save to shared prefs if condition is satisfied
        synchronized (this) {
            mResponseCount += 1;
            if (mResponseCount >= MAX_SIZE) {
                mCurrentAvgSpeed = calculateNewSpeed(mCurrentAvgSpeed);
                saveToSharedPreference(mCurrentAvgSpeed);
                mResponseCount = 0;
            }
        }

        mNetworkStat.addRequestStat(requestStats);
    }

    private float calculateNewSpeed(float currentAvgSpeed) {
        double newAvgSpeed = mNetworkStat.getCurrentAvgSpeed();
        currentAvgSpeed = (float) ((currentAvgSpeed + newAvgSpeed) / 2);
        return currentAvgSpeed;
    }

    @Override
    public void onHttpExchangeError(RequestStats requestStats, IOException e) {
        if (mLogger.isDebugEnabled()) {
            mLogger.debug("Response Received With Http Exchange Error : {}", requestStats);
        }

        for (OnResponseReceivedListener onResponseReceivedListener : mOnResponseReceivedListenerList) {
            if (onResponseReceivedListener != null) {
                onResponseReceivedListener.onResponseError(getActiveNetworkInfo(), requestStats, e);
            }
        }
    }

    @Override
    public void onResponseInputStreamError(RequestStats requestStats, Exception e) {
        if (mLogger.isDebugEnabled()) {
            mLogger.debug("Response Received With InputStream Error : {}", requestStats);
        }

        for (OnResponseReceivedListener onResponseReceivedListener : mOnResponseReceivedListenerList) {
            if (onResponseReceivedListener != null) {
                onResponseReceivedListener.onResponseError(getActiveNetworkInfo(), requestStats, e);
            }
        }
    }

    /**
     * Saves the network avg speed in the Shared Pref
     *
     * @param currentAvgSpeed : float
     */
    private void saveToSharedPreference(float currentAvgSpeed) {
        if (mLogger.isDebugEnabled()) {
            mLogger.debug("avg speed", "saveToSharedPreference: " + mNetworkStat.getCurrentAvgSpeed());
        }
        String networkKey = getNetworkKey(getActiveNetworkInfo());
        mPreferenceManager.setAverageSpeed(networkKey, currentAvgSpeed);
    }

    /**
     * Generates the network key based on the type of network
     *
     * @param networkInfo {@link NetworkInfo}
     * @return string
     */
    public String getNetworkKey(NetworkInfo networkInfo) {
        if (networkInfo != null && networkInfo.getTypeName() != null) {
            if (networkInfo.getTypeName().equals(WIFI_NETWORK)) {
                return WIFI_NETWORK + "_" + getWifiSSID();
            } else if (networkInfo.getTypeName().equals(MOBILE_NETWORK)) {
                return MOBILE_NETWORK + "_" + networkInfo.getSubtypeName();
            }
            return UNKNOWN_NETWORK;
        }
        return UNKNOWN_NETWORK;
    }

    public int getWifiSSID() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            String ssid = wifiInfo.getSSID();
            if (!TextUtils.isEmpty(ssid)) {
                return ssid.hashCode();
            }
        }
        return -1;
    }
}