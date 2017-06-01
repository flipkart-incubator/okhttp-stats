package com.flipkart.okhttpstats.reporter

import com.flipkart.okhttpstats.handler.NetworkRequestStatsHandler
import com.flipkart.okhttpstats.model.RequestStats
import okhttp3.Interceptor
import okhttp3.ResponseBody
import java.io.IOException

/**
 * Created by anirudh.r on 09/05/16 at 12:32 PM.
 * Default implementation of [NetworkEventReporter]
 *
 *
 * In case of any [IOException] during the [Interceptor.Chain.proceed])},
 * [NetworkEventReporter.httpExchangeError] gets called with appropriate error message.
 *
 *
 * In case of any [Exception] during the [ResponseBody.byteStream], [NetworkEventReporter.responseInputStreamError]
 *
 *
 */
class NetworkEventReporterImpl(private val networkRequestStatsHandler: NetworkRequestStatsHandler) : NetworkEventReporter {

    override fun responseReceived(inspectorRequest: NetworkEventReporter.InspectorRequest, inspectorResponse: NetworkEventReporter.InspectorResponse) {
        val requestId = inspectorResponse.requestId()
        val requestStats = RequestStats(requestId)
        requestStats.requestSize = inspectorRequest.requestSize()
        requestStats.url = inspectorRequest.url()
        requestStats.methodType = inspectorRequest.method()
        requestStats.hostName = inspectorRequest.hostName()
        requestStats.responseSize = inspectorResponse.responseSize()
        requestStats.statusCode = inspectorResponse.statusCode()
        requestStats.startTime = inspectorResponse.startTime()
        requestStats.endTime = inspectorResponse.endTime()
        requestStats.responseBody = inspectorResponse.responseBody()
        networkRequestStatsHandler.onResponseReceived(requestStats)
    }

    override fun httpExchangeError(inspectorRequest: NetworkEventReporter.InspectorRequest, e: java.io.IOException) {
        val requestId = inspectorRequest.requestId()
        val requestStats = RequestStats(requestId)
        requestStats.url = inspectorRequest.url()
        requestStats.methodType = inspectorRequest.method()
        requestStats.hostName = inspectorRequest.hostName()
        requestStats.requestSize = inspectorRequest.requestSize()
        networkRequestStatsHandler.onHttpExchangeError(requestStats, e)
    }

    override fun responseInputStreamError(inspectorRequest: NetworkEventReporter.InspectorRequest, inspectorResponse: NetworkEventReporter.InspectorResponse, e: Exception) {
        val requestId = inspectorResponse.requestId()
        val requestStats = RequestStats(requestId)
        requestStats.requestSize = inspectorRequest.requestSize()
        requestStats.url = inspectorRequest.url()
        requestStats.methodType = inspectorRequest.method()
        requestStats.hostName = inspectorRequest.hostName()
        requestStats.statusCode = inspectorResponse.statusCode()
        requestStats.startTime = inspectorResponse.startTime()
        requestStats.endTime = inspectorResponse.endTime()
        requestStats.responseBody = inspectorResponse.responseBody()
        networkRequestStatsHandler.onResponseInputStreamError(requestStats, e)
    }
}