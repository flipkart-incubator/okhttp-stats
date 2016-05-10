package com.flipkart.flipperf.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by anirudh.r on 06/05/16 at 11:55 AM.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private OnNetworkChangeListener onNetworkChangeListener;

    public NetworkChangeReceiver(OnNetworkChangeListener onNetworkChangeListener) {
        this.onNetworkChangeListener = onNetworkChangeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        onNetworkChangeListener.onNetworkChange(NetworkHelper.getDetailedNetworkType(context));
    }
}