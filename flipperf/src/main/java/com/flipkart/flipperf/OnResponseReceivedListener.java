package com.flipkart.flipperf;

import com.flipkart.flipperf.model.RequestResponseModel;

/**
 * Created by anirudh.r on 11/05/16 at 3:48 PM.
 */
public interface OnResponseReceivedListener {
    void onResponseReceived(RequestResponseModel requestResponseModel);

    void onHttpErrorReceived(RequestResponseModel requestResponseModel);

    void onInputStreamReadError(RequestResponseModel requestResponseModel);
}
