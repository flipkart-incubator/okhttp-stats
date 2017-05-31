package com.flipkart.okhttpstats.kotlin.reporter

import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.net.URL

/**
 * Interface to report events in case of response or any errors.
 */
interface NetworkEventReporter {

    /**
     * Notifies the [NetworkEventReporter] that the intercepted [Response] headers has been received

     * @param inspectorResponse : contains response headers
     */
    fun responseReceived(inspectorRequest: InspectorRequest, inspectorResponse: InspectorResponse)

    /**
     * Reports any [IOException] while [Response] is being proceeded.

     * @param inspectorRequest [InspectorRequest]
     * *
     * @param e                : error message
     */
    fun httpExchangeError(inspectorRequest: InspectorRequest, e: IOException)

    /**
     * Reports error while getting the input steam from [ResponseBody]

     * @param inspectorResponse [InspectorResponse]
     * *
     * @param e                 : error message
     */
    fun responseInputStreamError(inspectorRequest: InspectorRequest, inspectorResponse: InspectorResponse, e: Exception)

    interface InspectorRequest {
        fun requestId(): Int

        fun url(): URL

        fun method(): String

        fun requestSize(): Long

        fun hostName(): String
    }

    interface InspectorResponse {

        fun requestId(): Int

        fun statusCode(): Int

        fun responseSize(): Long

        fun startTime(): Long

        fun endTime(): Long

        fun responseBody(): ResponseBody?
    }
}