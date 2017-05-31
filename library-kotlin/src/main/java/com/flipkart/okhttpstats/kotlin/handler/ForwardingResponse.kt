package com.flipkart.okhttpstats.kotlin.handler

import android.net.NetworkInfo
import com.flipkart.okhttpstats.kotlin.model.RequestStats
import com.flipkart.okhttpstats.kotlin.toolbox.HttpStatusCode

class ForwardingResponse(private val mOnStatusCodeAwareResponseListener: OnStatusCodeAwareResponseListener): OnResponseListener {

    override fun onResponseError(info: NetworkInfo?, requestStats: RequestStats, e: Exception) {
        mOnStatusCodeAwareResponseListener.onResponseNetworkError(info, requestStats, e)
    }

    override fun onResponseSuccess(info: NetworkInfo?, requestStats: RequestStats) {
        val statusCode = requestStats.statusCode
        if (statusCode >= HttpStatusCode.HTTP_2XX_START && statusCode <= HttpStatusCode.HTTP_2XX_END || statusCode >= HttpStatusCode.HTTP_3XX_START && statusCode <= HttpStatusCode.HTTP_3XX_END) {
            mOnStatusCodeAwareResponseListener.onResponseServerSuccess(info, requestStats)
        } else if (statusCode >= HttpStatusCode.HTTP_4XX_START && statusCode <= HttpStatusCode.HTTP_4XX_END || statusCode >= HttpStatusCode.HTTP_5XX_START && statusCode <= HttpStatusCode.HTTP_5XX_END) {
            mOnStatusCodeAwareResponseListener.onResponseServerError(info, requestStats)
        }
    }
}