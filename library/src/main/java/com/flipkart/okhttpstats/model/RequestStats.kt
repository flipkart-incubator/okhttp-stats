package com.flipkart.okhttpstats.model

/**
 * P.O.J.O for RequestStats
 */
open class RequestStats(val id: Int) {
    var url: java.net.URL? = null
    var methodType: String? = null
    var requestSize: Long = 0
    var responseSize: Long = 0
    var responseBody: okhttp3.ResponseBody? = null
    var hostName: String? = null
    var statusCode: Int = 0
    var startTime: Long = 0
    var endTime: Long = 0
}