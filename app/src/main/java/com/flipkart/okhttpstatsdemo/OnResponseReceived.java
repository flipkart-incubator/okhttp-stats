package com.flipkart.okhttpstatsdemo;

import android.net.NetworkInfo;
import android.util.Log;

import com.flipkart.okhttpstats.handler.OnResponseListener;
import com.flipkart.okhttpstats.model.RequestStats;

public class OnResponseReceived implements OnResponseListener {

    @Override
    public void onResponseSuccess(NetworkInfo info, RequestStats requestStats) {
        Log.d(MainActivity.class.getName(), "onResponseSuccessReceived : "
                + "\nId : " + requestStats.id
                + "\nUrl : " + requestStats.url
                + "\nMethod : " + requestStats.methodType
                + "\nHost : " + requestStats.hostName
                + "\nRequest Size : " + requestStats.requestSize
                + "\nResponse Size : " + requestStats.responseSize
                + "\nTime Taken: " + (requestStats.endTime - requestStats.startTime)
                + "\nStatus Code : " + requestStats.statusCode);
    }

    @Override
    public void onResponseError(NetworkInfo info, RequestStats requestStats, Exception e) {
        Log.d(MainActivity.class.getName(), "onResponseErrorReceived : "
                + "\nId : " + requestStats.id
                + "\nUrl : " + requestStats.url
                + "\nMethod : " + requestStats.methodType
                + "\nHost : " + requestStats.hostName
                + "\nRequest Size : " + requestStats.requestSize
                + "\nResponse Size : " + requestStats.responseSize
                + "\nTime Taken: " + (requestStats.endTime - requestStats.startTime)
                + "\nStatus Code : " + requestStats.statusCode
                + "\nException : " + e.getMessage());
    }
}