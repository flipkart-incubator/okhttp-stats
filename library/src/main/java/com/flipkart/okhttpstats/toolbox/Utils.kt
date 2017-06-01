package com.flipkart.okhttpstats.toolbox

object Utils {

    var isLoggingEnabled = false

    fun contentLength(headers: okhttp3.Headers): Long {
        return Utils.stringToLong(headers.get("Content-Length"))
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