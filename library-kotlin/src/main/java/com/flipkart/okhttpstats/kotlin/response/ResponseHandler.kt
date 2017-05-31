package com.flipkart.okhttpstats.kotlin.response

/**
 * Implementations are to read the number of bytes in case the response header do not have content-length
 */
interface ResponseHandler {

    fun onRead(numBytes: Int)

    /**
     * Signals that EOF has been reached reading the response stream from the network
     * stack.
     */
    fun onEOF()
}