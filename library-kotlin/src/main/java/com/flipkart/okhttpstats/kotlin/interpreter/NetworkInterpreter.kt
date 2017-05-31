package com.flipkart.okhttpstats.kotlin.interpreter

import com.flipkart.okhttpstats.kotlin.NetworkInterceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException

/**
 * Implementations are supposed to interpret the input stream and call either the success of failure methods
 */
interface NetworkInterpreter {

    /**
     * Interpret the input stream received from the [ResponseBody]

     * @param requestId requestId
     * *
     * @param timeInfo  timeInfo
     * *
     * @param request   request
     * *
     * @param response  response
     * *
     * @return Response
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun interpretResponseStream(requestId: Int, timeInfo: NetworkInterceptor.TimeInfo, request: Request, response: Response): Response

    /**
     * Interpre the error received

     * @param requestId requestId
     * *
     * @param timeInfo  timeInfo
     * *
     * @param request   request
     * *
     * @param e         e
     */
    fun interpretError(requestId: Int, timeInfo: NetworkInterceptor.TimeInfo, request: Request, e: IOException)
}