package com.flipkart.flipperf;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.flipkart.flipperf.response.DefaultResponseHandler;
import com.flipkart.flipperf.response.ResponseHandler;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowLooper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static org.mockito.Mockito.mock;

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
    public void testRequestToBeSent() throws MalformedURLException {
        NetworkManager networkManager = mock(NetworkManager.class);

        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkEventReporterImpl networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler, networkManager);

        URL url = new URL("http://www.flipkart.com");
        CustomInspectorRequest customInspectorRequest = new CustomInspectorRequest(1, url, "POST", "20", "");
        networkEventReporter.requestToBeSent(customInspectorRequest);
        shadowLooper.runToEndOfTasks();

        CustomInspectorRequest customInspectorRequest1 = new CustomInspectorRequest(2, url, "GET", "202", "");
        networkEventReporter.requestToBeSent(customInspectorRequest1);
        shadowLooper.runToEndOfTasks();

        Assert.assertTrue(networkEventReporter.getCurrentRequestArray().size() == 2);
    }

    /**
     * Test for {@link NetworkEventReporter#responseReceived(NetworkEventReporter.InspectorResponse)}
     */
    @Test
    public void testResponseReceived() throws MalformedURLException {
        NetworkManager networkManager = mock(NetworkManager.class);

        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkEventReporterImpl networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler, networkManager);

        URL url = new URL("http://www.flipkart.com");
        CustomInspectorRequest customInspectorRequest = new CustomInspectorRequest(1, url, "POST", "20", "http://www.flipkart.com");
        networkEventReporter.requestToBeSent(customInspectorRequest);
        shadowLooper.runToEndOfTasks();

        //assert that hashMap contains 1 request
        Assert.assertTrue(networkEventReporter.getCurrentRequestArray().size() == 1);

        CustomInspectorResponse customInspectorResponse = new CustomInspectorResponse(1, "22", 200, 100, 150, true);
        //received response
        networkEventReporter.responseReceived(customInspectorResponse);
        shadowLooper.runToEndOfTasks();

        //assert that there are no more pending request in the hashmap
        Assert.assertTrue(networkEventReporter.getCurrentRequestArray().size() == 0);
    }

    /**
     * Test for {@link NetworkEventReporter#responseDataReceived(NetworkEventReporter.InspectorResponse, int)}
     */
    @Test
    public void testResponseDataReceived() throws MalformedURLException {
        NetworkManager networkManager = mock(NetworkManager.class);

        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkEventReporterImpl networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler, networkManager);

        URL url = new URL("http://www.flipkart.com");
        CustomInspectorRequest customInspectorRequest = new CustomInspectorRequest(1, url, "POST", "20", "http://www.flipkart.com");
        networkEventReporter.requestToBeSent(customInspectorRequest);
        shadowLooper.runToEndOfTasks();

        //assert that hashMap contains 1 request
        Assert.assertTrue(networkEventReporter.getCurrentRequestArray().size() == 1);

        CustomInspectorResponse customInspectorResponse = new CustomInspectorResponse(1, "22", 200, 100, 150, false);
        //received response
        networkEventReporter.responseDataReceived(customInspectorResponse, 22);
        shadowLooper.runToEndOfTasks();

        //assert that there are no more pending request in the hashmap
        Assert.assertTrue(networkEventReporter.getCurrentRequestArray().size() == 0);
    }

    /**
     * Test for {@link NetworkEventReporter#httpExchangeError(NetworkEventReporter.InspectorRequest, IOException)}
     */
    @Test
    public void testHttpExchangeError() throws MalformedURLException {
        NetworkManager networkManager = mock(NetworkManager.class);

        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkEventReporterImpl networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler, networkManager);

        URL url = new URL("http://www.flipkart.com");
        CustomInspectorRequest customInspectorRequest = new CustomInspectorRequest(1, url, "POST", "20", "http://www.flipkart.com");
        networkEventReporter.requestToBeSent(customInspectorRequest);
        shadowLooper.runToEndOfTasks();

        //assert that hashMap contains 1 request
        Assert.assertTrue(networkEventReporter.getCurrentRequestArray().size() == 1);

        networkEventReporter.httpExchangeError(customInspectorRequest, new IOException("Error proceeding request"));
        shadowLooper.runToEndOfTasks();

        //assert that there are no more pending request in the hashmap
        Assert.assertTrue(networkEventReporter.getCurrentRequestArray().size() == 0);
    }

    /**
     * Test for {@link NetworkEventReporter#responseInputStreamError(NetworkEventReporter.InspectorResponse, IOException)}
     */
    @Test
    public void testResponseInputStreamError() throws MalformedURLException {
        NetworkManager networkManager = mock(NetworkManager.class);

        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkEventReporterImpl networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler, networkManager);

        URL url = new URL("http://www.flipkart.com");
        CustomInspectorRequest customInspectorRequest = new CustomInspectorRequest(1, url, "POST", "20", "http://www.flipkart.com");
        networkEventReporter.requestToBeSent(customInspectorRequest);
        shadowLooper.runToEndOfTasks();

        //assert that hashMap contains 1 request
        Assert.assertTrue(networkEventReporter.getCurrentRequestArray().size() == 1);

        CustomInspectorResponse customInspectorResponse = new CustomInspectorResponse(1, "22", 200, 100, 150, false);
        //received response
        networkEventReporter.responseInputStreamError(customInspectorResponse, new IOException("Error while reading inputstream"));
        shadowLooper.runToEndOfTasks();

        //assert that there are no more pending request in the hashmap
        Assert.assertTrue(networkEventReporter.getCurrentRequestArray().size() == 0);
    }

    /**
     * Test for {@link NetworkEventReporter#interpretResponseStream(InputStream, ResponseHandler)}
     *
     * @throws Exception
     */
    @Test
    public void testInterpretInputStream() throws Exception {
        NetworkManager networkManager = mock(NetworkManager.class);

        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkEventReporterImpl networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler, networkManager);

        networkEventReporter.interpretResponseStream(new ByteArrayInputStream("Hello".getBytes()), new DefaultResponseHandler(networkEventReporter, null));
    }

    /**
     * Custom Inspector Request class for testing purposes
     */
    private class CustomInspectorRequest implements NetworkEventReporter.InspectorRequest {

        private final int requestId;
        private final URL requestUrl;
        private final String requestMethod;
        private final String requestSize;
        private final String hostName;

        public CustomInspectorRequest(int requestId, URL requestUrl, String requestMethod, String requestSize, String hostname) {
            this.requestId = requestId;
            this.requestUrl = requestUrl;
            this.requestMethod = requestMethod;
            this.requestSize = requestSize;
            this.hostName = hostname;
        }

        @Override
        public int requestId() {
            return requestId;
        }

        @Override
        public URL url() {
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

        @Override
        public String hostName() {
            return hostName;
        }
    }

    private class CustomInspectorResponse implements NetworkEventReporter.InspectorResponse {

        private final String responseSize;
        private final int statusCode;
        private final int requestId;
        private final long startTime;
        private final long endTime;
        private final boolean hasContentLength;

        public CustomInspectorResponse(int requestId, String responseSize, int statusCode, long startTime, long endTime, boolean hasContentLength) {
            this.requestId = requestId;
            this.responseSize = responseSize;
            this.statusCode = statusCode;
            this.startTime = startTime;
            this.endTime = endTime;
            this.hasContentLength = hasContentLength;
        }

        @Override
        public boolean hasContentLength() {
            return hasContentLength;
        }

        @Override
        public int requestId() {
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
        public long startTime() {
            return startTime;
        }

        @Override
        public long endTime() {
            return endTime;
        }
    }
}
