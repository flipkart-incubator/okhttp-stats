package com.flipkart.okhttpstats.handler

import android.net.NetworkInfo
import com.flipkart.okhttpstats.model.RequestStats
import com.flipkart.okhttpstats.toolbox.NetworkStat
import com.flipkart.okhttpstats.toolbox.PreferenceManager
import com.flipkart.okhttpstats.toolbox.Utils

/**
 * Default implementation of [NetworkRequestStatsHandler]
 *
 *
 * Responsibilities:
 *
 *
 * 1. Allows to register/deregister listeners, and gives callback to all the registered listeners in case of success or errors
 * 2. Gives the current network info for a particular request
 * 3. Gives the network speed based upon the type of current network
 * 4. Allows to calculate the average network speed, and save it to [android.content.SharedPreferences] to retrieve it later
 */
class PersistentStatsHandler : NetworkRequestStatsHandler {
    private val preferenceManager: PreferenceManager
    internal var onResponseListeners: MutableSet<OnResponseListener> = java.util.HashSet()
    private var responseCount = 0
    private var MAX_SIZE: Int = 0
    private var wifiManager: android.net.wifi.WifiManager?
    private var networkStat: NetworkStat = NetworkStat()
    /**
     * Exposed to the client to get the average network speed

     * @return avg speed
     */
    var averageNetworkSpeed = 0f
    private var connectivityManager: android.net.ConnectivityManager? = null

    constructor(context: android.content.Context) {
        this.preferenceManager = PreferenceManager(context)
        this.MAX_SIZE = PersistentStatsHandler.Companion.DEFAULT_MAX_SIZE
        this.wifiManager = context.getSystemService(android.content.Context.WIFI_SERVICE) as android.net.wifi.WifiManager
        this.connectivityManager = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        this.averageNetworkSpeed = preferenceManager.getAverageSpeed(getNetworkKey(activeNetworkInfo))
    }

    @android.support.annotation.VisibleForTesting
    internal constructor(context: android.content.Context, preferenceManager: PreferenceManager) {
        this.preferenceManager = preferenceManager
        this.MAX_SIZE = PersistentStatsHandler.Companion.DEFAULT_MAX_SIZE
        this.wifiManager = context.getSystemService(android.content.Context.WIFI_SERVICE) as android.net.wifi.WifiManager
        this.connectivityManager = context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        this.averageNetworkSpeed = preferenceManager.getAverageSpeed(getNetworkKey(activeNetworkInfo))
    }

    /**
     * Client can call this to get the current network info

     * @return [NetworkInfo]
     */
    val activeNetworkInfo: android.net.NetworkInfo?
        get() {
            return connectivityManager?.getActiveNetworkInfo()
        }

    /**
     * Client can add listeners to listen for the callbacks.

     * @param onResponseListener : [OnResponseListener]
     */
    fun addListener(onResponseListener: OnResponseListener) {
        onResponseListeners.add(onResponseListener)
    }

    /**
     * Client can remove listeners

     * @param onResponseListener : [OnResponseListener]
     */
    fun removeListener(onResponseListener: OnResponseListener) {
        onResponseListeners.remove(onResponseListener)
    }

    /**
     * The client can set the max number of request before it stores the speed to shared preference

     * @param size : int
     */
    fun setMaxSizeForPersistence(size: Int) {
        this.MAX_SIZE = size
    }

    override fun onResponseReceived(requestStats: RequestStats) {
        if (Utils.isLoggingEnabled) {
            android.util.Log.d("Response Received : ", requestStats.url.toString() + " ")
        }

        //call all the registered listeners
        for (onResponseListener in onResponseListeners) {
            onResponseListener.onResponseSuccess(activeNetworkInfo, requestStats)
        }

        //save to shared prefs if condition is satisfied
        synchronized(this) {
            responseCount += 1
            if (responseCount >= MAX_SIZE) {
                //calculate the new average speed
                val newAvgSpeed = networkStat.currentAvgSpeed
                averageNetworkSpeed = ((averageNetworkSpeed + newAvgSpeed) / 2).toFloat()
                //save it in shared preference
                val networkKey = getNetworkKey(activeNetworkInfo)
                preferenceManager.setAverageSpeed(networkKey, averageNetworkSpeed)
                //reset the response count
                responseCount = 0
            }
        }

        networkStat.addRequestStat(requestStats)
    }

    override fun onHttpExchangeError(requestStats: RequestStats, e: java.io.IOException) {
        if (Utils.isLoggingEnabled) {
            android.util.Log.d("Response Http Error :", requestStats.url.toString())
        }

        for (onResponseListener in onResponseListeners) {
            onResponseListener.onResponseError(activeNetworkInfo, requestStats, e)
        }
    }

    override fun onResponseInputStreamError(requestStats: RequestStats, e: Exception) {
        if (Utils.isLoggingEnabled) {
            android.util.Log.d("Response InputStream : ", requestStats.url.toString())
        }

        for (onResponseListener in onResponseListeners) {
            onResponseListener.onResponseError(activeNetworkInfo, requestStats, e)
        }
    }

    /**
     * Generates the network key based on the type of network

     * @param networkInfo [NetworkInfo]
     * *
     * @return string
     */
    @android.support.annotation.VisibleForTesting
    internal fun getNetworkKey(networkInfo: android.net.NetworkInfo?): String {
        val networkType = networkInfo?.getTypeName()
        if (networkType == Companion.WIFI_NETWORK) {
            return Companion.WIFI_NETWORK + "_" + wifiSSID
        } else if (networkType == Companion.MOBILE_NETWORK) {
            return Companion.MOBILE_NETWORK + "_" + networkInfo.getSubtypeName()
        }
        return Companion.UNKNOWN_NETWORK
    }

    internal val wifiSSID: Int?
        @android.support.annotation.VisibleForTesting
        get() {
            val wifiInfo = wifiManager?.getConnectionInfo()
            val ssid = wifiInfo?.getSSID()
            if (!android.text.TextUtils.isEmpty(ssid)) {
                return ssid?.hashCode()
            }
            return -1
        }

    companion object {
        private val DEFAULT_MAX_SIZE = 10
        private val WIFI_NETWORK = "WIFI"
        private val MOBILE_NETWORK = "mobile"
        private val UNKNOWN_NETWORK = "unknown"
    }
}