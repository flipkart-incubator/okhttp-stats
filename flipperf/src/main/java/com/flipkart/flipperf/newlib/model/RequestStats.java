package com.flipkart.flipperf.newlib.model;

import java.net.URL;

/**
 * Created by anirudh.r on 09/05/16 at 12:32 PM.
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
