package com.flipkart.flipperf;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowLooper;

import java.io.IOException;

/**
 * Created by anirudh.r on 10/05/16 at 3:18 PM.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NetworkEventReporterImplTest {

    /**
     * Test to verify {@link NetworkEventReporterImpl#isReporterEnabled()}
     */
    @Test
    public void testOnEnabled() {
        NetworkEventReporterImpl networkEventReporter = new NetworkEventReporterImpl();

        networkEventReporter.setEnabled(true);
        Assert.assertTrue(networkEventReporter.isReporterEnabled());

        networkEventReporter.setEnabled(false);
        Assert.assertTrue(!networkEventReporter.isReporterEnabled());
    }

    /**
     * Test for {@link NetworkEventReporterImpl#requestToBeSent(NetworkEventReporter.InspectorRequest)}
     */
    @Test
    public void testRequestToBeSent() {
        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkEventReporterImpl networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler);

        CustomInspectorRequest customInspectorRequest = new CustomInspectorRequest("1", "flipkart.com", "POST", "20");
        networkEventReporter.requestToBeSent(customInspectorRequest);
        shadowLooper.runToEndOfTasks();

        CustomInspectorRequest customInspectorRequest1 = new CustomInspectorRequest("2", "flipkart.com", "GET", "202");
        networkEventReporter.requestToBeSent(customInspectorRequest1);
        shadowLooper.runToEndOfTasks();

        Assert.assertTrue(networkEventReporter.getCurrentRequestMap().size() == 2);
    }

    /**
     * Test for {@link NetworkEventReporter#responseReceived(NetworkEventReporter.InspectorResponse)}
     */
    @Test
    public void testResponseReceived() {
        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkEventReporterImpl networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler);

        CustomInspectorRequest customInspectorRequest = new CustomInspectorRequest("1", "flipkart.com", "POST", "20");
        networkEventReporter.requestToBeSent(customInspectorRequest);
        shadowLooper.runToEndOfTasks();

        //assert that hashMap contains 1 request
        Assert.assertTrue(networkEventReporter.getCurrentRequestMap().size() == 1);

        CustomInspectorResponse customInspectorResponse = new CustomInspectorResponse("1", "22", 200, 1, true);
        //received response
        networkEventReporter.responseReceived(customInspectorResponse);
        shadowLooper.runToEndOfTasks();

        //assert that there are no more pending request in the hashmap
        Assert.assertTrue(networkEventReporter.getCurrentRequestMap().size() == 0);
    }

    /**
     * Test for {@link NetworkEventReporter#responseDataReceived(NetworkEventReporter.InspectorResponse, int)}
     */
    @Test
    public void testResponseDataReceived() {
        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkEventReporterImpl networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler);

        CustomInspectorRequest customInspectorRequest = new CustomInspectorRequest("1", "flipkart.com", "POST", "20");
        networkEventReporter.requestToBeSent(customInspectorRequest);
        shadowLooper.runToEndOfTasks();

        //assert that hashMap contains 1 request
        Assert.assertTrue(networkEventReporter.getCurrentRequestMap().size() == 1);

        CustomInspectorResponse customInspectorResponse = new CustomInspectorResponse("1", "22", 200, 1, false);
        //received response
        networkEventReporter.responseDataReceived(customInspectorResponse, 22);
        shadowLooper.runToEndOfTasks();

        //assert that there are no more pending request in the hashmap
        Assert.assertTrue(networkEventReporter.getCurrentRequestMap().size() == 0);
    }

    /**
     * Test for {@link NetworkEventReporter#httpExchangeError(NetworkEventReporter.InspectorRequest, IOException)}
     */
    @Test
    public void testHttpExchangeError() {
        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkEventReporterImpl networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler);

        CustomInspectorRequest customInspectorRequest = new CustomInspectorRequest("1", "flipkart.com", "POST", "20");
        networkEventReporter.requestToBeSent(customInspectorRequest);
        shadowLooper.runToEndOfTasks();

        //assert that hashMap contains 1 request
        Assert.assertTrue(networkEventReporter.getCurrentRequestMap().size() == 1);

        networkEventReporter.httpExchangeError(customInspectorRequest, new IOException("Error proceeding request"));
        shadowLooper.runToEndOfTasks();

        //assert that there are no more pending request in the hashmap
        Assert.assertTrue(networkEventReporter.getCurrentRequestMap().size() == 0);
    }

    /**
     * Test for {@link NetworkEventReporter#responseInputStreamError(NetworkEventReporter.InspectorResponse, IOException)}
     */
    @Test
    public void testResponseInputStreamError() {
        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkEventReporterImpl networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler);

        CustomInspectorRequest customInspectorRequest = new CustomInspectorRequest("1", "flipkart.com", "POST", "20");
        networkEventReporter.requestToBeSent(customInspectorRequest);
        shadowLooper.runToEndOfTasks();

        //assert that hashMap contains 1 request
        Assert.assertTrue(networkEventReporter.getCurrentRequestMap().size() == 1);

        CustomInspectorResponse customInspectorResponse = new CustomInspectorResponse("1", "22", 200, 1, false);
        //received response
        networkEventReporter.responseInputStreamError(customInspectorResponse, new IOException("Error while reading inputstream"));
        shadowLooper.runToEndOfTasks();

        //assert that there are no more pending request in the hashmap
        Assert.assertTrue(networkEventReporter.getCurrentRequestMap().size() == 0);
    }

    /**
     * Custom Inspector Request class for testing purposes
     */
    private class CustomInspectorRequest implements NetworkEventReporter.InspectorRequest {

        private String requestId, requestUrl, requestMethod, requestSize;

        public CustomInspectorRequest(String requestId, String requestUrl, String requestMethod, String requestSize) {
            this.requestId = requestId;
            this.requestUrl = requestUrl;
            this.requestMethod = requestMethod;
            this.requestSize = requestSize;
        }

        @Override
        public String requestId() {
            return requestId;
        }

        @Override
        public String url() {
            return requestUrl;
        }

        @Override
        public String method() {
            return requestMethod;
        }

        @Override
        public String requestSize() {
            return requestSize;
        }
    }

    private class CustomInspectorResponse implements NetworkEventReporter.InspectorResponse {

        private String requestId, responseSize;
        private int statusCode;
        private long responseTime;
        private boolean hasContentLength;

        public CustomInspectorResponse(String requestId, String responseSize, int statusCode, long responseTime, boolean hasContentLength) {
            this.requestId = requestId;
            this.responseSize = responseSize;
            this.statusCode = statusCode;
            this.responseTime = responseTime;
            this.hasContentLength = hasContentLength;
        }

        @Override
        public boolean hasContentLength() {
            return hasContentLength;
        }

        @Override
        public String requestId() {
            return requestId;
        }

        @Override
        public int statusCode() {
            return statusCode;
        }

        @Override
        public String responseSize() {
            return responseSize;
        }

        @Override
        public long responseTime() {
            return responseTime;
        }
    }
}
