package com.flipkart.okhttpstats.kotlin.handler

import android.net.NetworkInfo
import com.flipkart.okhttpstats.kotlin.model.RequestStats

interface OnStatusCodeAwareResponseListener {
    fun onResponseServerSuccess(networkInfo: NetworkInfo?, requestStats: RequestStats)

    fun onResponseServerError(networkInfo: NetworkInfo?, requestStats: RequestStats)

    fun onResponseNetworkError(info: NetworkInfo?, requestStats: RequestStats, e: Exception)
}