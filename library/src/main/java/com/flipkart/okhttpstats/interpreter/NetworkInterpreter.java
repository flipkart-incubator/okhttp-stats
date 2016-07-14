/*
 *
 *  * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.flipkart.okhttpstats.interpreter;

import com.flipkart.okhttpstats.NetworkInterceptor;

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
