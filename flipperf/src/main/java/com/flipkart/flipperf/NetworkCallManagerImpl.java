package com.flipkart.flipperf;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;

import com.flipkart.flipperf.model.RequestResponseModel;
import com.flipkart.flipperf.network.NetworkChangeReceiver;
import com.flipkart.flipperf.network.NetworkHelper;
import com.flipkart.flipperf.network.NetworkStatManager;
import com.flipkart.flipperf.network.OnNetworkChangeListener;

/**
 * Created by anirudh.r on 06/05/16 at 5:31 PM.
 */
public class NetworkCallManagerImpl implements NetworkCallManager, OnNetworkChangeListener {

    private static final String TAG = NetworkCallManagerImpl.class.getName();
    private String mNetworkType;
    private NetworkStatManager mNetworkStatManager;

    public NetworkCallManagerImpl(Context context, Handler handler) {
        mNetworkType = NetworkHelper.getDetailedNetworkType(context);
        mNetworkStatManager = new NetworkStatManager(handler);
        Log.d(TAG, "onInitialized networkeventreporter: \nnetworktype : " + mNetworkType);
    }

    @Override
    public void onHttpExchangeError(RequestResponseModel requestResponseModel) {
        Log.d(TAG, "onHttpExchangeError : "
                + "\nId : " + requestResponseModel.getRequestId()
                + "\nUrl : " + requestResponseModel.getRequestUrl()
                + "\nMethod : " + requestResponseModel.getRequestMethodType()
                + "\nError Message : " + requestResponseModel.getHttpExchangeErrorMessage());
    }

    @Override
    public void onResponseInputStreamError(RequestResponseModel requestResponseModel) {
        Log.d(TAG, "onResponseInputStreamError : "
                + "\nId : " + requestResponseModel.getRequestId()
                + "\nError Message : " + requestResponseModel.getResponseInputStreamError());
    }

    @Override
    public void onResponseReceived(RequestResponseModel requestResponseModel) {
        Log.d(TAG, "onResponseReceived :  "
                + "\nId : " + requestResponseModel.getRequestId()
                + "\nUrl : " + requestResponseModel.getRequestUrl()
                + "\nMethod : " + requestResponseModel.getRequestMethodType()
                + "\nRequest Size : " + requestResponseModel.getRequestSize()
                + "\nResponse Size : " + requestResponseModel.getResponseSize()
                + "\nResponse Time : " + requestResponseModel.getResponseTime()
                + "\nApi Speed : " + requestResponseModel.getApiSpeed()
                + "\nStatus Code : " + requestResponseModel.getResponseStatusCode());

        mNetworkStatManager.recordResponseCalls(mNetworkType, requestResponseModel);
    }

    private void registerReceiver(Context context) {
        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    public void onNetworkChange(String networkType) {
        this.mNetworkType = networkType;
    }
}
