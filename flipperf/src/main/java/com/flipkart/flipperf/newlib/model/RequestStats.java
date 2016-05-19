package com.flipkart.flipperf.newlib.model;

import android.net.NetworkInfo;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.URL;

/**
 * Created by anirudh.r on 09/05/16 at 12:32 PM.
 * P.O.J.O for RequestStats
 */
public class RequestStats {

    private final int mId;
    private String mMethodType;
    private String mSize;
    private URL mUrl;
    private String mResponseSize;
    private String mHostName;
    private int mHttpStatusCode;
    private long mStartTime;
    private long mEndTime;
    private NetworkInfo mNetworkType;
    @Nullable
    private IOException mException;
    /**
     * -1 if none
     */
    private int mExceptionType = -1;

    public RequestStats(int requestId) {
        this.mId = requestId;
    }

    public int getExceptionType() {
        return mExceptionType;
    }

    public void setExceptionType(int mExceptionType) {
        this.mExceptionType = mExceptionType;
    }

    @Nullable
    public IOException getException() {
        return mException;
    }

    public void setException(@Nullable IOException mException) {
        this.mException = mException;
    }

    public int getHttpStatusCode() {
        return mHttpStatusCode;
    }

    public void setHttpStatusCode(int mHttpStatusCode) {
        this.mHttpStatusCode = mHttpStatusCode;
    }

    public NetworkInfo getNetworkType() {
        return mNetworkType;
    }

    public void setNetworkType(NetworkInfo mNetworkType) {
        this.mNetworkType = mNetworkType;
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

    public String getSize() {
        return mSize;
    }

    public void setSize(String mSize) {
        this.mSize = mSize;
    }

    public URL getUrl() {
        return mUrl;
    }

    public void setUrl(URL mUrl) {
        this.mUrl = mUrl;
    }

    public String getResponseSize() {
        return mResponseSize;
    }

    public void setResponseSize(String mResponseSize) {
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
