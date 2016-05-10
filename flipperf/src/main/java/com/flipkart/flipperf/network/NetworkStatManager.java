package com.flipkart.flipperf.network;

import android.os.Handler;

import com.flipkart.flipperf.model.RequestResponseModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anirudh.r on 10/05/16 at 11:59 AM.
 */
public class NetworkStatManager {

    private Map<String, NetworkStat> mNetworkStatMap;
    private Handler mHandler;

    public NetworkStatManager(Handler handler) {
        this.mNetworkStatMap = new HashMap<>();
        this.mHandler = handler;
    }

    public void recordResponseCalls(String networkType, RequestResponseModel requestResponseModel) {
        if (Double.parseDouble(requestResponseModel.getResponseSize()) > 3000.0D) {
            NetworkStat networkStat;
            if (mNetworkStatMap.containsKey(networkType)) {
                networkStat = mNetworkStatMap.get(networkType);
            } else {
                networkStat = new NetworkStat(mHandler, 5);
                mNetworkStatMap.put(networkType, networkStat);
            }
            networkStat.addResponseModel(requestResponseModel);
        }
    }
}
