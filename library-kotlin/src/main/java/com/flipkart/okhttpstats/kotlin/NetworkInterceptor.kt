package com.flipkart.okhttpstats.kotlin

import com.flipkart.okhttpstats.kotlin.interpreter.NetworkInterpreter
import com.flipkart.okhttpstats.kotlin.toolbox.Utils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger

class NetworkInterceptor(builder: Builder) : Interceptor {

    private var interpreter: NetworkInterpreter
    private val nextRequestId = AtomicInteger(1)
    private var enabled = true

    init {
        enabled = builder.enabled
        if (builder.interpreter == null) {
            throw IllegalStateException("NetworkInterpreter cannot be null")
        }
        interpreter = builder.interpreter!!
        Utils.isLoggingEnabled = builder.isLoggingEnabled
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestId = nextRequestId.getAndIncrement()
        val request = chain.request()
        val timeInfo = TimeInfo()
        var response: Response
        try {
            //note the time taken for the response
            timeInfo.startTime = System.currentTimeMillis()
            response = chain.proceed(request)
            timeInfo.endTime = System.currentTimeMillis()
        } catch (e: IOException) {
            if (enabled) {
                //notify event reporter in case there is any exception while proceeding request
                interpreter.interpretError(requestId, timeInfo, request, e)
            }
            throw e
        }

        if (enabled) {
            response = interpreter.interpretResponseStream(requestId, timeInfo, request, response)
        }

        return response
    }

    class TimeInfo {
        var startTime: Long = 0
        var endTime: Long = 0
    }

    /**
     * Builder Pattern for [NetworkInterceptor]
     */
    class Builder {
        internal var enabled = true
        internal var isLoggingEnabled = false
        internal var interpreter: NetworkInterpreter? = null

        /**
         * To enable/disable the calls to [NetworkInterpreter]
         * If disabled, the interceptor continues to operate without reporting to the [NetworkInterpreter]

         * @param enabled boolean
         * *
         * @return [Builder]
         */
        fun setEnabled(enabled: Boolean): Builder {
            this.enabled = enabled
            return this
        }

        /**
         * Can leave it null for the default implementation

         * @param interpreter [NetworkInterpreter]
         * *
         * @return [Builder]
         */
        fun setNetworkInterpreter(interpreter: NetworkInterpreter): Builder {
            this.interpreter = interpreter
            return this
        }

        /**
         * To enable/disable logging. By default logging is disabled.

         * @param isLoggingEnabled : boolean
         * *
         * @return [Builder]
         */
        fun setLoggingEnabled(isLoggingEnabled: Boolean): Builder {
            this.isLoggingEnabled = isLoggingEnabled
            return this
        }

        fun build(): NetworkInterceptor {
            return NetworkInterceptor(this)
        }
    }
}