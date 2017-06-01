package com.flipkart.okhttpstats.handler

import android.net.NetworkInfo
import com.flipkart.okhttpstats.model.RequestStats
import java.io.IOException

/**
 * This interface to be consumed by the client, to get callbacks on success/failure cases
 * It is more of a generic listener, where if server gives back any response [OnResponseListener.onResponseSuccess] gets called, independent of the status code.
 * When there is no response, or any error before server responds [OnResponseListener.onResponseError] gets called
 *
 *
 * If the client wants callback more specific to server status code such as 2XX, 3XX, 4XX and 5XX, implement the [ForwardingResponse]
 */
interface OnResponseListener {

    /**
     * This callback includes response with 2XX, 3XX, 4XX, 5XX status codes

     * @param info         [NetworkInfo]
     * *
     * @param requestStats [RequestStats]
     */
    fun onResponseSuccess(info: android.net.NetworkInfo?, requestStats: RequestStats)

    /**
     * This callback includes failure request cases, in cases when there are no response such as NoInternet and more

     * @param info         [NetworkInfo]
     * *
     * @param requestStats [RequestStats]
     * *
     * @param e            [IOException]
     */
    fun onResponseError(info: android.net.NetworkInfo?, requestStats: RequestStats, e: Exception)
}