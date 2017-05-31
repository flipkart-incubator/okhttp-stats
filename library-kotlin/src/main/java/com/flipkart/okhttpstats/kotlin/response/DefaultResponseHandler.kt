package com.flipkart.okhttpstats.kotlin.response

/**
 * Default implementation of [ResponseHandler]
 */
class DefaultResponseHandler(private val responseCallback: DefaultResponseHandler.ResponseCallback) : ResponseHandler {
    private var bytesRead = 0

    override fun onRead(numBytes: Int) {
        bytesRead += numBytes
    }

    override fun onEOF() {
        responseCallback.onEOF(bytesRead.toLong())
    }

    interface ResponseCallback {
        fun onEOF(bytesRead: Long)
    }
}