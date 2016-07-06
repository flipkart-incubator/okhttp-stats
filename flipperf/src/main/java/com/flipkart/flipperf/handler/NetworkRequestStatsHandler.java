package com.flipkart.flipperf.handler;

import android.support.annotation.Nullable;

import com.flipkart.flipperf.model.RequestStats;

import java.io.IOException;

public interface NetworkRequestStatsHandler {

    /**
     * Indicates a successful response was received.
     *
     * @param requestStats {@link RequestStats}
     */
    void onResponseReceived(RequestStats requestStats);

    /**
     * Indicates the connection failed, which implies that we dont have information like response status code, size etc.
     * Typically, socket timeouts, connect errors etc.
     *
     * @param requestStats {@link RequestStats}
     */
    void onHttpExchangeError(RequestStats requestStats, @Nullable IOException e);

    /**
     * Indicates that connection was successful, but we could not read the response stream.
     * This implies that we have access to response status code etc.
     *
     * @param requestStats {@link RequestStats}
     * @param e            {@link IOException}
     */
    void onResponseInputStreamError(RequestStats requestStats, @Nullable Exception e);
}