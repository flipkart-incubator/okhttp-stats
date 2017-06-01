package com.flipkart.okhttpstatsdemo;

import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.flipkart.okhttpstats.handler.OnResponseListener;
import com.flipkart.okhttpstats.model.RequestStats;


public class OnResponseReceived implements OnResponseListener {

    @Override
    public void onResponseSuccess(NetworkInfo info, @NonNull RequestStats requestStats) {
        Log.d(MainActivity.class.getName(), "onResponseSuccessReceived : "
                + "\nId : " + requestStats.getId()
                + "\nUrl : " + requestStats.getUrl()
                + "\nMethod : " + requestStats.getMethodType()
                + "\nHost : " + requestStats.getHostName()
                + "\nRequest Size : " + requestStats.getRequestSize()
                + "\nResponse Size : " + requestStats.getResponseSize()
                + "\nTime Taken: " + (requestStats.getEndTime() - requestStats.getStartTime())
                + "\nStatus Code : " + requestStats.getStatusCode());
    }

    @Override
    public void onResponseError(NetworkInfo info, RequestStats requestStats, Exception e) {
        Log.d(MainActivity.class.getName(), "onResponseErrorReceived : "
                + "\nId : " + requestStats.getId()
                + "\nUrl : " + requestStats.getUrl()
                + "\nMethod : " + requestStats.getMethodType()
                + "\nHost : " + requestStats.getHostName()
                + "\nRequest Size : " + requestStats.getRequestSize()
                + "\nResponse Size : " + requestStats.getResponseSize()
                + "\nTime Taken: " + (requestStats.getEndTime() - requestStats.getStartTime())
                + "\nStatus Code : " + requestStats.getStatusCode()
                + "\nException : " + e.getMessage());
    }
}