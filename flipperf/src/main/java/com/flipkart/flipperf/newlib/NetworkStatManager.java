package com.flipkart.flipperf.newlib;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anirudh.r on 10/05/16 at 11:59 AM.
 */
public class NetworkStatManager implements NetworkManager {

    private static final int DEFAULT_MAX_SIZE = 10;
    private static final String WIFI_NETWORK = "WIFI";
    private static final String MOBILE_NETWORK = "mobile";
    private static final String UNKNOWN_NETWORK = "unknown";
    private final PreferenceManager mPreferenceManager;
    private Logger mLogger = LoggerFactory.getLogger(NetworkStatManager.class);
    private List<OnResponseReceivedListener> mOnResponseReceivedListenerList = new ArrayList<>();
    private int mResponseCount = 0;
    private NetworkInfo mNetworkInfo;
    private int MAX_SIZE;
    private List<RequestStats> mRequestStatsList;
    private WifiManager mWifiManager;

    public NetworkStatManager(Context context) {
        this.mPreferenceManager = new PreferenceManager(context);
        this.MAX_SIZE = DEFAULT_MAX_SIZE;
        this.mRequestStatsList = new ArrayList<>();
        this.mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public NetworkInfo getNetworkInfo() {
        return mNetworkInfo;
    }

    @Override
    public NetworkSpeed getNetworkSpeed() {
        if (mNetworkInfo == null || !mNetworkInfo.isConnected()) {
            return NetworkSpeed.SLOW_NETWORK;
        } else {
            switch (mNetworkInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return NetworkSpeed.FAST_NETWORK;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (mNetworkInfo.getSubtype()) {
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

    @Override
    public void addListener(OnResponseReceivedListener onResponseReceivedListener) {
        if (mOnResponseReceivedListenerList != null) {
            mOnResponseReceivedListenerList.add(onResponseReceivedListener);
        }
    }

    @Override
    public void removeListener(OnResponseReceivedListener onResponseReceivedListener) {
        if (mOnResponseReceivedListenerList != null) {
            mOnResponseReceivedListenerList.remove(onResponseReceivedListener);
        }
    }

    @Override
    public void setNetworkType(NetworkInfo networkType) {
        this.mNetworkInfo = networkType;
    }

    @Override
    public void setMaxSizeForPersistence(int size) {
        this.MAX_SIZE = size;
    }

    @Override
    public float getAverageNetworkSpeed() {
        if (mNetworkInfo != null) {
            return mPreferenceManager.getAverageSpeed(getNetworkKey(mNetworkInfo));
        } else {
            return 0;
        }
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
            saveToSharedPreference();
        }

        mRequestStatsList.add(requestStats);
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
        if (mLogger.isDebugEnabled()) {
            mLogger.debug("avg speed", "saveToSharedPreference: " + NetworkStat.getAverageSpeed(mRequestStatsList));
        }
        double oldAvgSpeed = mPreferenceManager.getAverageSpeed(getNetworkKey(mNetworkInfo));
        double newAvgSpeed = NetworkStat.getAverageSpeed(mRequestStatsList);
        float avgSpeed = (float) ((oldAvgSpeed + newAvgSpeed) / 2);

        mPreferenceManager.setAverageSpeed(getNetworkKey(mNetworkInfo), avgSpeed);
        mRequestStatsList.clear();
        mResponseCount = 0;
    }

    private String getNetworkKey(NetworkInfo networkInfo) {
        if (networkInfo != null) {
            if (networkInfo.getTypeName().equals(WIFI_NETWORK)) {
                return WIFI_NETWORK + "_" + getWifiSSID();
            } else if (networkInfo.getTypeName().equals(MOBILE_NETWORK)) {
                return MOBILE_NETWORK + "_" + networkInfo.getSubtypeName();
            }
            return UNKNOWN_NETWORK;
        } else {
            return UNKNOWN_NETWORK;
        }
    }

    private int getWifiSSID() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return wifiInfo.getSSID().hashCode();
    }
}