package com.flipkart.flipperf.models;

/**
 * Created by nikhil.n on 15/01/15.
 */
public enum NetworkType {

    WIFI(NetworkSpeed.FAST_NETWORK),
    HSDPA(NetworkSpeed.FAST_NETWORK),
    HSPA(NetworkSpeed.FAST_NETWORK),
    HIGHSPEED(NetworkSpeed.FAST_NETWORK),
    HSUPA(NetworkSpeed.FAST_NETWORK),
    EVDO(NetworkSpeed.MEDIUM_NETWORK),
    UMTS(NetworkSpeed.MEDIUM_NETWORK),
    GPRS(NetworkSpeed.SLOW_NETWORK),
    RTT(NetworkSpeed.SLOW_NETWORK),
    UNKNOWN(NetworkSpeed.SLOW_NETWORK),
    CDMA(NetworkSpeed.SLOW_NETWORK),
    EDGE(NetworkSpeed.SLOW_NETWORK);

    private NetworkSpeed networkSpeed;

    NetworkType(NetworkSpeed networkSpeed) {
        this.networkSpeed = networkSpeed;
    }

    public NetworkSpeed getNetworkSpeed() {
        return networkSpeed;
    }

}
