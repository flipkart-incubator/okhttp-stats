package com.flipkart.flipperf.oldlib.trackers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.flipkart.flipperf.oldlib.models.NetworkType;


/**
 * Created by udit.khandelwal on 01/09/15.
 */
public class ConnectivityChangeReceiver extends BroadcastReceiver {

    private static final String TAG = ConnectivityChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkType currentNetworkType = FlipperfNetworkStatManager.getNetworkType(context);
        FlipperfNetworkStatManager.getInstance(context).setCurrentNetworkType(currentNetworkType);
    }
}
