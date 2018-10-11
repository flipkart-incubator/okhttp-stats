package com.flipkart.okhttpstats.handler;

import android.net.NetworkInfo;

import com.flipkart.okhttpstats.model.RequestStats;

import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by anirudh.r on 15/07/16.
 * Test for {@link ForwardingResponse}
 */
public class ForwardingResponseTest {

    /**
     * Test to verify that {@link OnStatusCodeAwareResponseListener#onResponseServerSuccess(NetworkInfo, RequestStats)}
     * gets called for all status code from range 200 to 399.
     *
     * @throws Exception
     */
    @Test
    public void testOnResponseServerSuccessCalledFor2XX3XX() {
        OnStatusCodeAwareResponseListener onStatusCodeAwareResponseListener = mock(OnStatusCodeAwareResponseListener.class);
        ForwardingResponse forwardingResponse = new ForwardingResponse(onStatusCodeAwareResponseListener);

        RequestStats requestStats = new RequestStats(1);
        requestStats.statusCode = (200);
        requestStats.hostName = ("flipkart.com");

        NetworkInfo info = mock(NetworkInfo.class);

        forwardingResponse.onResponseSuccess(info, requestStats);

        //verify that onResponseServerSuccess gets called once for status 200
        verify(onStatusCodeAwareResponseListener, times(1)).onResponseServerSuccess(info, requestStats);
        reset(onStatusCodeAwareResponseListener);

        requestStats = new RequestStats(1);
        requestStats.statusCode = (299);
        requestStats.hostName = ("flipkart.com");

        forwardingResponse.onResponseSuccess(info, requestStats);

        //verify that onResponseServerSuccess gets called once for status 299
        verify(onStatusCodeAwareResponseListener, times(1)).onResponseServerSuccess(info, requestStats);
        reset(onStatusCodeAwareResponseListener);

        requestStats = new RequestStats(1);
        requestStats.statusCode = (300);
        requestStats.hostName = ("flipkart.com");

        forwardingResponse.onResponseSuccess(info, requestStats);

        //verify that onResponseServerSuccess gets called once for status 300
        verify(onStatusCodeAwareResponseListener, times(1)).onResponseServerSuccess(info, requestStats);
        reset(onStatusCodeAwareResponseListener);

        requestStats = new RequestStats(1);
        requestStats.statusCode = (399);
        requestStats.hostName = ("flipkart.com");

        forwardingResponse.onResponseSuccess(info, requestStats);

        //verify that onResponseServerSuccess gets called once for status 399
        verify(onStatusCodeAwareResponseListener, times(1)).onResponseServerSuccess(info, requestStats);
        reset(onStatusCodeAwareResponseListener);
    }

    /**
     * Test to verify that {@link OnStatusCodeAwareResponseListener#onResponseServerError(NetworkInfo, RequestStats)}
     * gets called for all status code from range 400 to 599.
     *
     * @throws Exception
     */
    @Test
    public void testOnResponseServerErrorCalledFor4XX5XX() {
        OnStatusCodeAwareResponseListener onStatusCodeAwareResponseListener = mock(OnStatusCodeAwareResponseListener.class);
        ForwardingResponse forwardingResponse = new ForwardingResponse(onStatusCodeAwareResponseListener);

        RequestStats requestStats = new RequestStats(1);
        requestStats.statusCode = (400);
        requestStats.hostName = ("flipkart.com");

        NetworkInfo info = mock(NetworkInfo.class);

        forwardingResponse.onResponseSuccess(info, requestStats);

        //verify that onResponseServerError gets called once for status 400
        verify(onStatusCodeAwareResponseListener, times(1)).onResponseServerError(info, requestStats);
        reset(onStatusCodeAwareResponseListener);

        requestStats = new RequestStats(1);
        requestStats.statusCode = (499);
        requestStats.hostName = ("flipkart.com");

        forwardingResponse.onResponseSuccess(info, requestStats);

        //verify that onResponseServerError gets called once for status 499
        verify(onStatusCodeAwareResponseListener, times(1)).onResponseServerError(info, requestStats);
        reset(onStatusCodeAwareResponseListener);

        requestStats = new RequestStats(1);
        requestStats.statusCode = (500);
        requestStats.hostName = ("flipkart.com");

        forwardingResponse.onResponseSuccess(info, requestStats);

        //verify that onResponseServerError gets called once for status 500
        verify(onStatusCodeAwareResponseListener, times(1)).onResponseServerError(info, requestStats);
        reset(onStatusCodeAwareResponseListener);

        requestStats = new RequestStats(1);
        requestStats.statusCode = (599);
        requestStats.hostName = ("flipkart.com");

        forwardingResponse.onResponseSuccess(info, requestStats);

        //verify that onResponseServerError gets called once for status 599
        verify(onStatusCodeAwareResponseListener, times(1)).onResponseServerError(info, requestStats);
        reset(onStatusCodeAwareResponseListener);
    }

    /**
     * Test to verify that {@link OnStatusCodeAwareResponseListener#onResponseNetworkError(NetworkInfo, RequestStats, Exception)}
     * gets called for any network related errors.
     *
     * @throws Exception
     */
    @Test
    public void testOnResponseNetworkErrorCalledForNetworkErrors() {
        OnStatusCodeAwareResponseListener onStatusCodeAwareResponseListener = mock(OnStatusCodeAwareResponseListener.class);
        ForwardingResponse forwardingResponse = new ForwardingResponse(onStatusCodeAwareResponseListener);

        RequestStats requestStats = new RequestStats(1);
        requestStats.statusCode = (400);
        requestStats.hostName = ("flipkart.com");

        NetworkInfo info = mock(NetworkInfo.class);
        IOException e = new IOException("Test error");

        forwardingResponse.onResponseError(info, requestStats, e);

        //verify that onResponseNetworkError gets called once
        verify(onStatusCodeAwareResponseListener, times(1)).onResponseNetworkError(info, requestStats, e);
    }
}
