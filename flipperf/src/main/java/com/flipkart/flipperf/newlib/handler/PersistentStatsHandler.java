package com.flipkart.flipperf.newlib.handler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.VisibleForTesting;
import android.telephony.TelephonyManager;

import com.flipkart.flipperf.NetworkSpeed;
import com.flipkart.flipperf.newlib.model.RequestStats;
import com.flipkart.flipperf.newlib.toolbox.NetworkStat;
import com.flipkart.flipperf.newlib.toolbox.PreferenceManager;

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

    private NetworkInfo getActiveNetworkInfo() {
        return mConnectivityManager.getActiveNetworkInfo();
    }

    public NetworkSpeed getNetworkSpeed() {
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return NetworkSpeed.SLOW_NETWORK;
        } else {
            switch (activeNetworkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return NetworkSpeed.FAST_NETWORK;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (activeNetworkInfo.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                            return NetworkSpeed.SLOW_NETWORK; // ~ 50-100 kbps
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                            return NetworkSpeed.SLOW_NETWORK; // ~ 14-64 kbps
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                            return NetworkSpeed.SLOW_NETWORK; // ~ 50-100 kbps
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            return NetworkSpeed.MEDIUM_NETWORK; // ~ 400-1000 kbps
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            return NetworkSpeed.MEDIUM_NETWORK; // ~ 600-1400 kbps
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                            return NetworkSpeed.SLOW_NETWORK; // ~ 100 kbps
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                            return NetworkSpeed.FAST_NETWORK; // ~ 2-14 Mbps
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                            return NetworkSpeed.FAST_NETWORK; // ~ 700-1700 kbps
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                            return NetworkSpeed.FAST_NETWORK; // ~ 1-23 Mbps
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                            return NetworkSpeed.MEDIUM_NETWORK; // ~ 400-7000 kbps
                        case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                            return NetworkSpeed.FAST_NETWORK; // ~ 1-2 Mbps
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                            return NetworkSpeed.FAST_NETWORK; // ~ 5 Mbps
                        case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                            return NetworkSpeed.FAST_NETWORK; // ~ 10-20 Mbps
                        case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                            return NetworkSpeed.FAST_NETWORK; // ~25 kbps
                        case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                            return NetworkSpeed.FAST_NETWORK; // ~ 10+ Mbps
                        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                            return NetworkSpeed.SLOW_NETWORK;
                        default:
                            return NetworkSpeed.SLOW_NETWORK;
                    }
                default:
                    return NetworkSpeed.SLOW_NETWORK;
            }
        }
    }

    @VisibleForTesting
    public List<OnResponseReceivedListener> getOnResponseReceivedListenerList() {
        return mOnResponseReceivedListenerList;
    }

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
        mResponseCount += 1;
        if (mLogger.isDebugEnabled()) {
            mLogger.debug("Response Received : {}", requestStats);
        }

        for (OnResponseReceivedListener onResponseReceivedListener : mOnResponseReceivedListenerList) {
            if (onResponseReceivedListener != null) {
                onResponseReceivedListener.onResponseSuccess(getActiveNetworkInfo(), requestStats);
            }
        }

        //save to shared prefs if condition is satisfied
        if (mResponseCount >= MAX_SIZE) {
            saveToSharedPreference(mCurrentAvgSpeed);
        }

        mNetworkStat.addRequestStat(requestStats);
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
    public void onResponseInputStreamError(RequestStats requestStats, IOException e) {
        if (mLogger.isDebugEnabled()) {
            mLogger.debug("Response Received With InputStream Error : {}", requestStats);
        }

        for (OnResponseReceivedListener onResponseReceivedListener : mOnResponseReceivedListenerList) {
            if (onResponseReceivedListener != null) {
                onResponseReceivedListener.onResponseError(getActiveNetworkInfo(), requestStats, e);
            }
        }
    }

    private void saveToSharedPreference(float currentAvgSpeed) {
        if (mLogger.isDebugEnabled()) {
            mLogger.debug("avg speed", "saveToSharedPreference: " + mNetworkStat.getCurrentAvgSpeed());
        }

        String networkKey = getNetworkKey(getActiveNetworkInfo());
        double newAvgSpeed = mNetworkStat.getCurrentAvgSpeed();
        currentAvgSpeed = (float) ((currentAvgSpeed + newAvgSpeed) / 2);
        mCurrentAvgSpeed = currentAvgSpeed;
        mPreferenceManager.setAverageSpeed(networkKey, currentAvgSpeed);
        mResponseCount = 0;
    }

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
        return wifiInfo.getSSID().hashCode();
    }
}