package com.flipkart.flipperf;

import com.flipkart.flipperf.response.DefaultResponseHandler;
import com.flipkart.flipperf.response.ResponseHandler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by anirudh.r on 05/05/16 at 7:53 PM.
 * Test for {@link DefaultResponseHandler}
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DefaultResponseHandlerTest {

    /**
     * Test for {@link ResponseHandler#onEOF()}
     */
    @Test
    public void testOnEOF() {
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        NetworkEventReporter.InspectorResponse inspectorResponse = mock(NetworkEventReporter.InspectorResponse.class);
        NetworkEventReporter.InspectorRequest inspectorRequest = mock(NetworkEventReporter.InspectorRequest.class);

        DefaultResponseHandler defaultResponseHandler = new DefaultResponseHandler(networkEventReporter, inspectorRequest, inspectorResponse);
        defaultResponseHandler.onEOF();

        //verify that responseDataReceived gets called once
        verify(networkEventReporter, times(1)).responseDataReceived(inspectorRequest, inspectorResponse, 0);
    }

    /**
     * Test for {@link ResponseHandler#onRead(int)}
     */
    @Test
    public void testOnRead() {
        NetworkEventReporter networkEventReporter = mock(NetworkEventReporter.class);
        NetworkEventReporter.InspectorResponse inspectorResponse = mock(NetworkEventReporter.InspectorResponse.class);
        NetworkEventReporter.InspectorRequest inspectorRequest = mock(NetworkEventReporter.InspectorRequest.class);

        String requestId = "Hello";
        DefaultResponseHandler defaultResponseHandler = new DefaultResponseHandler(networkEventReporter, inspectorRequest, inspectorResponse);
        defaultResponseHandler.onRead(requestId.length());
    }
}
