package com.flipkart.okhttpstats.response

import java.io.InputStream

/**
 * An [InputStream] that counts the number of bytes read.

 * @author Chris Nokleberg
 * *
 * @since 2009.09.15 **tentative**
 */
class CountingInputStream(`in`: java.io.InputStream?, private val responseHandler: ResponseHandler) : java.io.FilterInputStream(`in`) {
    private var count: Int = 0

    @Synchronized private fun checkEOF(n: Int): Int {
        if (n == -1) {
            responseHandler.onEOF()
        }
        return n
    }

    @Throws(java.io.IOException::class)
    override fun read(): Int {
        try {
            val result = checkEOF(`in`.read())
            if (result != -1) {
                count++
            }
            responseHandler.onRead(count)
            return result
        } catch (ex: java.io.IOException) {
            return 0
        }
    }

    @Throws(java.io.IOException::class)
    override fun read(b: ByteArray): Int {
        return this.read(b, 0, b.size)
    }

    @Throws(java.io.IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        try {
            val result = checkEOF(`in`.read(b, off, len))
            if (result != -1) {
                count += result
            }
            responseHandler.onRead(result)
            return result
        } catch (ex: java.io.IOException) {
            return 0
        }

    }

    override fun markSupported(): Boolean {
        return false
    }

    @Throws(java.io.IOException::class)
    override fun reset() {
        throw UnsupportedOperationException("Mark not supported")
    }
}