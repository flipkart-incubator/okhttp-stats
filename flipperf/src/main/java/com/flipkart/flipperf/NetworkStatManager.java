package com.flipkart.flipperf;

import com.flipkart.flipperf.NetworkManager;
import com.flipkart.flipperf.OnResponseBatchReadyListener;
import com.flipkart.flipperf.model.RequestResponseModel;

/**
 * Created by anirudh.r on 10/05/16 at 11:59 AM.
 */
public class NetworkStatManager implements NetworkManager {

    private OnResponseBatchReadyListener mOnNetworkDataBatchReady;

    public NetworkStatManager(OnResponseBatchReadyListener onNetworkDataBatchReady) {
        this.mOnNetworkDataBatchReady = onNetworkDataBatchReady;
    }

    @Override
    public void onResponseReceived(RequestResponseModel requestResponseModel) {
        mOnNetworkDataBatchReady.onBatchResponseReceived(requestResponseModel);
    }

    @Override
    public void onHttpExchangeError(RequestResponseModel requestResponseModel) {

    }

    @Override
    public void onResponseInputStreamError(RequestResponseModel requestResponseModel) {

    }

//    private NetworkStat getNetworkStat(String networkType) {
//        NetworkStat networkStat;
//        if (mNetworkStatMap.containsKey(networkType)) {
//            networkStat = mNetworkStatMap.get(networkType);
//        } else {
//            networkStat = new NetworkStat();
//            mNetworkStatMap.put(networkType, networkStat);
//        }
//        return networkStat;
//    }
//
//    private boolean isReadyToFlush(int i) {
//        return mCount >= i;
//    }
//
//    private void flush() {
//        mCount = 0;
//        Map<String, NetworkStat> tempMap = new HashMap<>(mNetworkStatMap);
//        mOnNetworkDataBatchReady.onBatchResponseReceived(tempMap);
//        mNetworkStatMap.clear();
//    }
}
