package com.flipkart.flipperf;

import com.flipkart.flipperf.model.RequestResponseModel;

/**
 * Created by anirudh.r on 11/05/16 at 3:41 PM.
 */
public interface NetworkManager {
    void onResponseReceived(RequestResponseModel requestResponseModel);

    void onHttpExchangeError(RequestResponseModel requestResponseModel);

    void onResponseInputStreamError(RequestResponseModel requestResponseModel);

    void addListener(OnResponseReceivedListener networkManager);

    void unregisterListener(OnResponseReceivedListener networkManager);

    void flush();

    void setNetworkType(String networkType);
}
