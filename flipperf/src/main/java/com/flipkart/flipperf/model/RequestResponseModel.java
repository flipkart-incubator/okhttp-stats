package com.flipkart.flipperf.model;

/**
 * Created by anirudh.r on 09/05/16 at 12:32 PM.
 * Model for RequestResponse
 */
public class RequestResponseModel {

    private String mRequestId;
    private String mRequestMethodType;
    private String mRequestSize;
    private String mRequestUrl;
    private String mResponseSize;
    private int mResponseStatusCode;
    private long mResponseTime;
    private String mHttpExchangeErrorMessage;
    private String mResponseInputStreamError;
    private double mApiSpeed;

    public String getResponseInputStreamError() {
        return mResponseInputStreamError;
    }

    public void setResponseInputStreamError(String mResponseInputStreamError) {
        this.mResponseInputStreamError = mResponseInputStreamError;
    }

    public double getApiSpeed() {
        return mApiSpeed;
    }

    public void setApiSpeed(double mApiSpeed) {
        this.mApiSpeed = mApiSpeed;
    }

    public String getHttpExchangeErrorMessage() {
        return mHttpExchangeErrorMessage;
    }

    public void setHttpExchangeErrorMessage(String mHttpExchangeErrorCount) {
        this.mHttpExchangeErrorMessage = mHttpExchangeErrorCount;
    }

    public long getResponseTime() {
        return mResponseTime;
    }

    public void setResponseTime(long mResponseTime) {
        this.mResponseTime = mResponseTime;
    }

    public String getRequestId() {
        return mRequestId;
    }

    public void setRequestId(String mRequestId) {
        this.mRequestId = mRequestId;
    }

    public String getRequestMethodType() {
        return mRequestMethodType;
    }

    public void setRequestMethodType(String mRequestMethodType) {
        this.mRequestMethodType = mRequestMethodType;
    }

    public String getRequestSize() {
        return mRequestSize;
    }

    public void setRequestSize(String mRequestSize) {
        this.mRequestSize = mRequestSize;
    }

    public String getRequestUrl() {
        return mRequestUrl;
    }

    public void setRequestUrl(String mRequestUrl) {
        this.mRequestUrl = mRequestUrl;
    }

    public String getResponseSize() {
        return mResponseSize;
    }

    public void setResponseSize(String mResponseSize) {
        this.mResponseSize = mResponseSize;
    }

    public int getResponseStatusCode() {
        return mResponseStatusCode;
    }

    public void setResponseStatusCode(int mResponseStatusCode) {
        this.mResponseStatusCode = mResponseStatusCode;
    }
}
