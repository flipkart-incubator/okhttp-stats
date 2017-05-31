package com.flipkart.okhttpstats.kotlin.toolbox

import okhttp3.Headers

object Utils {

    var isLoggingEnabled = false

    fun contentLength(headers: Headers): Long {
        return stringToLong(headers.get("Content-Length"))
    }

    private fun stringToLong(s: String?): Long {
        if (s == null) return -1
        try {
            return java.lang.Long.parseLong(s)
        } catch (e: NumberFormatException) {
            return -1
        }

    }
}