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

/**
 * P.O.J.O for RequestStats
 */
public class RequestStats {

    private final int mId;
    private URL mUrl;
    private String mMethodType;
    private long mRequestSize;
    private long mResponseSize;
    private String mHostName;
    private int mStatusCode;
    private long mStartTime;
    private long mEndTime;

    public RequestStats(int requestId) {
        this.mId = requestId;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(int mHttpStatusCode) {
        this.mStatusCode = mHttpStatusCode;
    }

    public String getHostName() {
        return mHostName;
    }

    public void setHostName(String mHostName) {
        this.mHostName = mHostName;
    }

    public int getId() {
        return mId;
    }

    public String getMethodType() {
        return mMethodType;
    }

    public void setMethodType(String mMethodType) {
        this.mMethodType = mMethodType;
    }

    public long getRequestSize() {
        return mRequestSize;
    }

    public void setRequestSize(long mSize) {
        this.mRequestSize = mSize;
    }

    public URL getUrl() {
        return mUrl;
    }

    public void setUrl(URL mUrl) {
        this.mUrl = mUrl;
    }

    public long getResponseSize() {
        return mResponseSize;
    }

    public void setResponseSize(long mResponseSize) {
        this.mResponseSize = mResponseSize;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long mEndTime) {
        this.mEndTime = mEndTime;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long mStartTime) {
        this.mStartTime = mStartTime;
    }
}
