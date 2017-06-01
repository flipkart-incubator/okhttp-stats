package com.flipkart.okhttpstats.handler

import com.flipkart.okhttpstats.model.RequestStats

interface OnStatusCodeAwareResponseListener {
    fun onResponseServerSuccess(networkInfo: android.net.NetworkInfo?, requestStats: RequestStats)

    fun onResponseServerError(networkInfo: android.net.NetworkInfo?, requestStats: RequestStats)

    fun onResponseNetworkError(info: android.net.NetworkInfo?, requestStats: RequestStats, e: Exception)
}