package com.flipkart.flipperf;

import com.flipkart.flipperf.response.DefaultResponseHandler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by anirudh.r on 05/05/16 at 7:53 PM.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DefaultResponseHandlerTest {

    NetworkEventReporter networkEventReporter;

    /**
     * Test to verify that {@link NetworkEventReporter#dataReceived(String, int)} gets called once
     */
    @Test
    public void testOnEOF() {
        networkEventReporter = mock(NetworkEventReporter.class);

        String requestId = "Hello";
        DefaultResponseHandler defaultResponseHandler = new DefaultResponseHandler(networkEventReporter, requestId);
        defaultResponseHandler.onEOF();

        //verify that dataReceived gets called once
        verify(networkEventReporter, times(1)).dataReceived(requestId, 0);
    }

    @Test
    public void testOnRead() {
        networkEventReporter = mock(NetworkEventReporter.class);

        String requestId = "Hello";
        DefaultResponseHandler defaultResponseHandler = new DefaultResponseHandler(networkEventReporter, requestId);
        defaultResponseHandler.onRead(requestId.length());
    }
}
