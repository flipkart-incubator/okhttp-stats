package com.flipkart.okhttpstats.handler;

import android.net.NetworkInfo;

import com.flipkart.okhttpstats.model.RequestStats;
import com.flipkart.okhttpstats.toolbox.HTTPStatusCode;

public class ForwardingResponse implements OnResponseListener {

    private OnStatusCodeAwareResponseListener mOnStatusCodeAwareResponseListener;

    public ForwardingResponse(OnStatusCodeAwareResponseListener onStatusCodeAwareResponseListener) {
        mOnStatusCodeAwareResponseListener = onStatusCodeAwareResponseListener;
    }

    @Override
    public void onResponseSuccess(NetworkInfo info, RequestStats requestStats) {
        if (requestStats != null) {
            int statusCode = requestStats.getStatusCode();
            if ((statusCode >= HTTPStatusCode.HTTP_2XX_START && statusCode <= HTTPStatusCode.HTTP_2XX_END) ||
                    (statusCode >= HTTPStatusCode.HTTP_3XX_START && statusCode <= HTTPStatusCode.HTTP_3XX_END)) {
                mOnStatusCodeAwareResponseListener.onResponseServerSuccess(info, requestStats);
            } else if ((statusCode >= HTTPStatusCode.HTTP_4XX_START && statusCode <= HTTPStatusCode.HTTP_4XX_END) ||
                    (statusCode >= HTTPStatusCode.HTTP_5XX_START && statusCode <= HTTPStatusCode.HTTP_5XX_END)) {
                mOnStatusCodeAwareResponseListener.onResponseServerError(info, requestStats);
            }
        }
    }

    @Override
    public void onResponseError(NetworkInfo info, RequestStats requestStats, Exception e) {
        mOnStatusCodeAwareResponseListener.onResponseNetworkError(info, requestStats, e);
    }
}