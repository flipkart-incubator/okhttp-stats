package com.flipkart.okhttpstats.handler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.flipkart.okhttpstats.model.RequestStats;
import com.flipkart.okhttpstats.toolbox.HTTPStatusCode;
import com.flipkart.okhttpstats.toolbox.NetworkSpeed;
import com.flipkart.okhttpstats.toolbox.NetworkStat;
import com.flipkart.okhttpstats.toolbox.PreferenceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Default implementation of {@link NetworkRequestStatsHandler}
 * <p>
 * Responsibilities:
 * <p>
 * 1. Allows to register/deregister listeners, and gives callback to all the registered listeners in case of success or errors
 * 2. Gives the current network info for a particular request
 * 3. Gives the network speed based upon the type of current network
 * 4. Allows to calculate the average network speed, and save it to {@link android.content.SharedPreferences} to retrieve it later
 */
public class PersistentStatsHandler implements NetworkRequestStatsHandler {

    private static final int DEFAULT_MAX_SIZE = 10;
    private static final String WIFI_NETWORK = "WIFI";
    private static final String MOBILE_NETWORK = "mobile";
    private static final String UNKNOWN_NETWORK = "unknown";
    private final PreferenceManager mPreferenceManager;
    private Logger mLogger = LoggerFactory.getLogger(PersistentStatsHandler.class);
    private Set<OnResponseReceivedListener> mOnResponseReceivedListeners = new HashSet<>();
    private Set<OnServerResponseReceivedListener> mOnServerResponseReceivedListeners = new HashSet<>();
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
    public Set<OnServerResponseReceivedListener> getOnServerResponseReceivedListeners() {
        return mOnServerResponseReceivedListeners;
    }

    /**
     * Client can add listeners to listen for the callbacks.
     *
     * @param onResponseReceivedListener : {@link OnResponseReceivedListener}
     */
    public void addListener(OnResponseReceivedListener onResponseReceivedListener) {
        if (mOnResponseReceivedListeners != null) {
            mOnResponseReceivedListeners.add(onResponseReceivedListener);
        }
    }

    /**
     * Client can remove listeners
     *
     * @param onResponseReceivedListener : {@link OnResponseReceivedListener}
     */
    public void removeListener(OnResponseReceivedListener onResponseReceivedListener) {
        if (mOnResponseReceivedListeners != null) {
            mOnResponseReceivedListeners.remove(onResponseReceivedListener);
        }
    }

    /**
     * Client can add {@link OnServerResponseReceivedListener}
     *
     * @param onServerResponseReceivedListener : {@link OnResponseReceivedListener}
     */
    public void addListener(OnServerResponseReceivedListener onServerResponseReceivedListener) {
        if (mOnServerResponseReceivedListeners != null) {
            mOnServerResponseReceivedListeners.add(onServerResponseReceivedListener);
        }
    }

    /**
     * Client can remove {@link OnServerResponseReceivedListener}
     *
     * @param onServerResponseReceivedListener : {@link OnResponseReceivedListener}
     */
    public void removeListener(OnServerResponseReceivedListener onServerResponseReceivedListener) {
        if (mOnServerResponseReceivedListeners != null) {
            mOnServerResponseReceivedListeners.remove(onServerResponseReceivedListener);
        }
    }

    /**
     * The client can set the max number of request before it stores the speed to shared preference
     *
     * @param size : int
     */
    public void setMaxSizeForPersistence(int size) {
        this.MAX_SIZE = size;
    }

    /**
     * Exposed to the client so that he can get the average network speed
     *
     * @return avg speed
     */
    public float getAverageNetworkSpeed() {
        return mCurrentAvgSpeed;
    }

    @Override
    public void onResponseReceived(final RequestStats requestStats) {
        if (mLogger.isDebugEnabled()) {
            mLogger.debug("Response Received : {}", requestStats);
        }

        //call all the registered listeners
        for (OnResponseReceivedListener onResponseReceivedListener : mOnResponseReceivedListeners) {
            if (onResponseReceivedListener != null) {
                onResponseReceivedListener.onResponseSuccess(getActiveNetworkInfo(), requestStats);
            }
        }

        //call the specific registered listener based on status code
        for (OnServerResponseReceivedListener onServerResponseReceivedListener : mOnServerResponseReceivedListeners) {
            if (onServerResponseReceivedListener != null) {
                if (requestStats != null) {
                    int statusCode = requestStats.getStatusCode();
                    if (statusCode >= HTTPStatusCode.HTTP_2XX_START && statusCode <= HTTPStatusCode.HTTP_2XX_END) {
                        onServerResponseReceivedListener.on2XXStatusResponseReceived(getActiveNetworkInfo(), requestStats);
                    } else if (statusCode >= HTTPStatusCode.HTTP_3XX_START && statusCode <= HTTPStatusCode.HTTP_3XX_END) {
                        onServerResponseReceivedListener.on3XXStatusResponseReceived(getActiveNetworkInfo(), requestStats);
                    } else if (statusCode >= HTTPStatusCode.HTTP_4XX_START && statusCode <= HTTPStatusCode.HTTP_4XX_END) {
                        onServerResponseReceivedListener.on4XXStatusResponseReceived(getActiveNetworkInfo(), requestStats);
                    } else if (statusCode >= HTTPStatusCode.HTTP_5XX_START && statusCode <= HTTPStatusCode.HTTP_5XX_END) {
                        onServerResponseReceivedListener.on5XXStatusResponseReceived(getActiveNetworkInfo(), requestStats);
                    }
                }
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

        for (OnResponseReceivedListener onResponseReceivedListener : mOnResponseReceivedListeners) {
            if (onResponseReceivedListener != null) {
                onResponseReceivedListener.onResponseError(getActiveNetworkInfo(), requestStats, e);
            }
        }

        for (OnServerResponseReceivedListener onServerResponseReceivedListener : mOnServerResponseReceivedListeners) {
            if (onServerResponseReceivedListener != null) {
                onServerResponseReceivedListener.onResponseError(getActiveNetworkInfo(), requestStats, e);
            }
        }
    }

    @Override
    public void onResponseInputStreamError(RequestStats requestStats, Exception e) {
        if (mLogger.isDebugEnabled()) {
            mLogger.debug("Response Received With InputStream Error : {}", requestStats);
        }

        for (OnResponseReceivedListener onResponseReceivedListener : mOnResponseReceivedListeners) {
            if (onResponseReceivedListener != null) {
                onResponseReceivedListener.onResponseError(getActiveNetworkInfo(), requestStats, e);
            }
        }

        for (OnServerResponseReceivedListener onServerResponseReceivedListener : mOnServerResponseReceivedListeners) {
            if (onServerResponseReceivedListener != null) {
                onServerResponseReceivedListener.onResponseError(getActiveNetworkInfo(), requestStats, e);
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
    @VisibleForTesting
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

    @VisibleForTesting
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