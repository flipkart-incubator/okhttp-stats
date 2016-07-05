package com.flipkart.flipperf.newlib.interpreter;

import com.flipkart.flipperf.newlib.NetworkInterceptor;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Implementations are supposed to interpret the input stream and call either the success of failure methods
 */
public interface NetworkInterpreter {

    /**
     * Interpret the input stream received from the {@link ResponseBody}
     *
     * @param requestId requestId
     * @param timeInfo  timeInfo
     * @param request   request
     * @param response  response
     * @return Response
     * @throws IOException
     */
    Response interpretResponseStream(int requestId, NetworkInterceptor.TimeInfo timeInfo, Request request, Response response) throws IOException;

    /**
     * Interpre the error received
     *
     * @param requestId requestId
     * @param timeInfo  timeInfo
     * @param request   request
     * @param e         e
     */
    void interpretError(int requestId, NetworkInterceptor.TimeInfo timeInfo, Request request, IOException e);
}
