package com.flipkart.flipperf.trackers;

import com.flipkart.flipperf.models.NetworkType;

/**
 * Created by nikhil.n on 15/01/15.
 */
public class APIEvent implements Event<String> {

    private String eventId;
    public double apiStartTime;
    public double apiStopTime;
    private double responseSize;
    private double apiSpeed;
    private NetworkType networkType;

    private APIEvent() {

    }

    public APIEvent(NetworkType networkType, String eventUrl) {
        this.networkType = networkType;
        this.eventId = eventUrl;
    }

    @Override
    public void onEventStarted() {
        apiStartTime = System.currentTimeMillis();
    }

    public void setResponseSize(double responseSize) {
        this.responseSize = responseSize;
    }

    @Override
    public void onEventFinished() {
        apiStopTime = System.currentTimeMillis();
        double apiRTT = apiStopTime - apiStartTime;
        apiSpeed = responseSize / apiRTT;
        FlipperfNetworkStatManager.getInstance().logAPiCallEvent(this);
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public double getResponseSize() {
        return responseSize;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public double getApiSpeed() {
        return apiSpeed;
    }

    public double getRtt() {
        return apiStopTime - apiStartTime;
    }

    public void setApiSpeed(double apiSpeed) {
        this.apiSpeed = apiSpeed;
    }

}
