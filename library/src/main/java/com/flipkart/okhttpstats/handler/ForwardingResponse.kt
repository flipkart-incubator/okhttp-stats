package com.flipkart.okhttpstats.handler

import com.flipkart.okhttpstats.model.RequestStats
import com.flipkart.okhttpstats.toolbox.HttpStatusCode

class ForwardingResponse(private val mOnStatusCodeAwareResponseListener: OnStatusCodeAwareResponseListener) : OnResponseListener {

    override fun onResponseError(info: android.net.NetworkInfo?, requestStats: RequestStats, e: Exception) {
        mOnStatusCodeAwareResponseListener.onResponseNetworkError(info, requestStats, e)
    }

    override fun onResponseSuccess(info: android.net.NetworkInfo?, requestStats: RequestStats) {
        val statusCode = requestStats.statusCode
        if (statusCode >= HttpStatusCode.HTTP_2XX_START && statusCode <= HttpStatusCode.HTTP_2XX_END || statusCode >= HttpStatusCode.HTTP_3XX_START && statusCode <= HttpStatusCode.HTTP_3XX_END) {
            mOnStatusCodeAwareResponseListener.onResponseServerSuccess(info, requestStats)
        } else if (statusCode >= HttpStatusCode.HTTP_4XX_START && statusCode <= HttpStatusCode.HTTP_4XX_END || statusCode >= HttpStatusCode.HTTP_5XX_START && statusCode <= HttpStatusCode.HTTP_5XX_END) {
            mOnStatusCodeAwareResponseListener.onResponseServerError(info, requestStats)
        }
    }
}