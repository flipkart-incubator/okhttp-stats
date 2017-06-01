package com.flipkart.okhttpstats.handler


import com.flipkart.okhttpstats.model.RequestStats

import java.io.IOException

interface NetworkRequestStatsHandler {

    /**
     * Indicates a successful response was received.

     * @param requestStats [RequestStats]
     */
    fun onResponseReceived(requestStats: RequestStats)

    /**
     * Indicates the connection failed, which implies that we dont have information like response status code, size etc.
     * Typically, socket timeouts, connect errors etc.

     * @param requestStats [RequestStats]
     */
    fun onHttpExchangeError(requestStats: RequestStats, e: java.io.IOException)

    /**
     * Indicates that connection was successful, but we could not read the response stream.
     * This implies that we have access to response status code etc.

     * @param requestStats [RequestStats]
     * *
     * @param e            [IOException]
     */
    fun onResponseInputStreamError(requestStats: RequestStats, e: Exception)
}