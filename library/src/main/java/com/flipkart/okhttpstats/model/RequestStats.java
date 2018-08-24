/*
 * The MIT License
 *
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.flipkart.okhttpstats.model;

import java.net.URL;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * P.O.J.O for RequestStats
 */
public class RequestStats {

    public final int id;
    public URL url;
    public String methodType;
    public long requestSize;
    public long responseSize;
    public RequestBody requestBody;
    public ResponseBody responseBody;
    public String hostName;
    public int statusCode;
    public long startTime;
    public long endTime;

    public RequestStats(int requestId) {
        this.id = requestId;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Id : ").append(id)
                .append("\nMethod : ").append(methodType)
                .append("\nHost : ").append(hostName)
                .append("\nStatusCode : ").append(statusCode)
                .append("\nRequest Size : ").append(requestSize)
                .append("\nResponse Size : ").append(responseSize)
                .append("\nTime Taken : ").append(endTime - startTime)
                .append("\nUrl : ").append(url)
                .append("\nRequest Body : ").append(requestBody)
                .append("\nResponse Body : ").append(responseBody);
        return stringBuilder.toString();
    }
}