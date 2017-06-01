package com.flipkart.okhttpstats.interpreter

import com.flipkart.okhttpstats.NetworkInterceptor
import com.flipkart.okhttpstats.reporter.NetworkEventReporter
import com.flipkart.okhttpstats.response.CountingInputStream
import com.flipkart.okhttpstats.response.DefaultResponseHandler
import com.flipkart.okhttpstats.toolbox.Utils
import okhttp3.ResponseBody

/**
 * Default implementation of [NetworkInterpreter]
 */
class DefaultInterpreter(internal var eventReporter: NetworkEventReporter) : NetworkInterpreter {

    @Throws(java.io.IOException::class)
    override fun interpretResponseStream(requestId: Int, timeInfo: NetworkInterceptor.TimeInfo, request: okhttp3.Request, response: okhttp3.Response): okhttp3.Response {
        var response = response
        val responseBody = response.body()

        val okHttpInspectorRequest = DefaultInterpreter.OkHttpInspectorRequest(requestId, request.url().url(), request.method(), Utils.contentLength(request.headers()), request.url().host())
        val okHttpInspectorResponse = DefaultInterpreter.OkHttpInspectorResponse(requestId, response.code(), Utils.contentLength(response.headers()), timeInfo.startTime, timeInfo.endTime, responseBody)
        //if response does not have content length, using CountingInputStream to read its bytes
        if (response.header(DefaultInterpreter.Companion.CONTENT_LENGTH) == null) {
            var responseStream: java.io.InputStream? = null
            try {
                responseStream = responseBody?.byteStream()
            } catch (e: Exception) {
                if (Utils.isLoggingEnabled) {
                    android.util.Log.d("Error reading IS : ", e.message)
                }
                //notify event reporter in case there is any exception while getting the input stream of response
                eventReporter.responseInputStreamError(okHttpInspectorRequest, okHttpInspectorResponse, e)
                throw e
            }

            //interpreting the response stream using CountingInputStream, once the counting is done, notify the event reporter that response has been received
            responseStream = CountingInputStream(responseStream, DefaultResponseHandler(object : DefaultResponseHandler.ResponseCallback {
                override fun onEOF(bytesRead: Long) {
                    okHttpInspectorResponse.responseSize = bytesRead
                    eventReporter.responseReceived(okHttpInspectorRequest, okHttpInspectorResponse)
                }
            }))

            //creating response object using the interpreted stream
            response = response.newBuilder().body(DefaultInterpreter.ForwardingResponseBody(responseBody, responseStream)).build()
        } else {
            //if response has content length, notify the event reporter that response has been received.
            eventReporter.responseReceived(okHttpInspectorRequest, okHttpInspectorResponse)
        }

        return response
    }

    override fun interpretError(requestId: Int, timeInfo: NetworkInterceptor.TimeInfo, request: okhttp3.Request, e: java.io.IOException) {
        if (Utils.isLoggingEnabled) {
            android.util.Log.d("Error response: ", e.message)
        }
        val okHttpInspectorRequest = DefaultInterpreter.OkHttpInspectorRequest(requestId, request.url().url(), request.method(), Utils.contentLength(request.headers()), request.header(HOST_NAME))
        eventReporter.httpExchangeError(okHttpInspectorRequest, e)
    }

    /**
     * Implementation of [NetworkEventReporter.InspectorRequest]
     */
    internal class OkHttpInspectorRequest(val requestId: Int, val requestUrl: java.net.URL, val methodType: String, val contentLength: Long, val hostName: String) : NetworkEventReporter.InspectorRequest {

        override fun requestId(): Int {
            return requestId
        }

        override fun url(): java.net.URL {
            return requestUrl
        }

        override fun method(): String {
            return methodType
        }

        override fun requestSize(): Long {
            return contentLength
        }

        override fun hostName(): String {
            return hostName
        }
    }

    /**
     * Implementation of [NetworkEventReporter.InspectorResponse]
     */
    internal class OkHttpInspectorResponse(var requestId: Int, var statusCode: Int, var responseSize: Long, var startTime: Long, var endTime: Long, var responseBody: okhttp3.ResponseBody?) : NetworkEventReporter.InspectorResponse {

        override fun requestId(): Int {
            return requestId
        }

        override fun statusCode(): Int {
            return statusCode
        }

        override fun responseSize(): Long {
            return responseSize
        }

        override fun startTime(): Long {
            return startTime
        }

        override fun endTime(): Long {
            return endTime
        }

        override fun responseBody(): okhttp3.ResponseBody? {
            return responseBody
        }
    }

    /**
     * Wrapper for [ResponseBody]
     * Will only be used in case the response does not have the content-length
     */
    @android.support.annotation.VisibleForTesting
    internal class ForwardingResponseBody(val body: okhttp3.ResponseBody, interceptedStream: java.io.InputStream) : okhttp3.ResponseBody() {
        val interceptedSource: okio.BufferedSource

        init {
            interceptedSource = okio.Okio.buffer(okio.Okio.source(interceptedStream))
        }

        override fun contentType(): okhttp3.MediaType {
            return body.contentType()
        }

        override fun contentLength(): Long {
            return body.contentLength()
        }

        override fun source(): okio.BufferedSource {
            return interceptedSource
        }
    }

    companion object {
        private val HOST_NAME = "HOST"
        private val CONTENT_LENGTH = "Content-Length"
    }
}