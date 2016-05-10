package com.flipkart.flipperf;

import com.flipkart.flipperf.model.RequestResponseModel;

/**
 * Created by anirudh.r on 06/05/16 at 5:31 PM.
 */
public interface NetworkCallManager {
    void onResponseReceived(RequestResponseModel requestResponseModel);

    void onHttpExchangeError(RequestResponseModel requestResponseModel);

    void onResponseInputStreamError(RequestResponseModel requestResponseModel);
}