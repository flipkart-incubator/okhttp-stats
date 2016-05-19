package com.flipkart.flipperf;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.flipkart.flipperf.newlib.model.RequestStats;
import com.flipkart.flipperf.newlib.NetworkEventReporter;
import com.flipkart.flipperf.newlib.NetworkEventReporterImpl;
import com.flipkart.flipperf.newlib.NetworkManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowLooper;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by anirudh.r on 17/05/16 at 10:55 PM.
 * Test for {@link NetworkEventReporter}
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NetworkEventReporterTest {

    /**
     * Test for {@link NetworkEventReporter#onInitialized(Context, Handler, NetworkManager)}
     *
     * @throws Exception
     */
    @Test
    public void testOnInitialized() throws Exception {

        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkManager networkManager = mock(NetworkManager.class);

        NetworkEventReporter networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler, networkManager);
    }

    /**
     * Test for {@link NetworkEventReporter#isReporterEnabled()}
     *
     * @throws Exception
     */
    @Test
    public void testIsEnabled() throws Exception {
        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkManager networkManager = mock(NetworkManager.class);

        NetworkEventReporter networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler, networkManager);

        networkEventReporter.setEnabled(true);

        //assert that reporter is enabled
        Assert.assertTrue(networkEventReporter.isReporterEnabled());
    }

    /**
     * Test for {@link NetworkEventReporter#responseReceived(NetworkEventReporter.InspectorRequest, NetworkEventReporter.InspectorResponse)}
     *
     * @throws Exception
     */
    @Test
    public void testResponseReceived() throws Exception {
        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkManager networkManager = mock(NetworkManager.class);

        NetworkEventReporter networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler, networkManager);

        NetworkEventReporter.InspectorRequest inspectorRequest = mock(NetworkEventReporter.InspectorRequest.class);
        NetworkEventReporter.InspectorResponse inspectorResponse = mock(NetworkEventReporter.InspectorResponse.class);

        networkEventReporter.responseReceived(inspectorRequest, inspectorResponse);
        shadowLooper.runToEndOfTasks();

        //verify onResponseReceived gets called once
        verify(networkManager, times(1)).onResponseReceived(any(RequestStats.class));
    }

    /**
     * Test for {@link NetworkEventReporter#httpExchangeError(NetworkEventReporter.InspectorRequest, IOException)}
     *
     * @throws Exception
     */
    @Test
    public void testHttpExchangeError() throws Exception {
        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkManager networkManager = mock(NetworkManager.class);

        NetworkEventReporter networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler, networkManager);

        NetworkEventReporter.InspectorRequest inspectorRequest = mock(NetworkEventReporter.InspectorRequest.class);

        networkEventReporter.httpExchangeError(inspectorRequest, new IOException("Error"));
        shadowLooper.runToEndOfTasks();

        //verify onHttpExchangeError gets called once
        verify(networkManager, times(1)).onHttpExchangeError(any(RequestStats.class));
    }

    /**
     * Test for {@link NetworkEventReporter#responseDataReceived(NetworkEventReporter.InspectorRequest, NetworkEventReporter.InspectorResponse, int)}
     *
     * @throws Exception
     */
    @Test
    public void testResponseDataReceived() throws Exception {
        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkManager networkManager = mock(NetworkManager.class);

        NetworkEventReporter networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler, networkManager);

        NetworkEventReporter.InspectorRequest inspectorRequest = mock(NetworkEventReporter.InspectorRequest.class);
        NetworkEventReporter.InspectorResponse inspectorResponse = mock(NetworkEventReporter.InspectorResponse.class);

        networkEventReporter.responseDataReceived(inspectorRequest, inspectorResponse, 10);
        shadowLooper.runToEndOfTasks();

        //verify onResponseReceived gets called once
        verify(networkManager, times(1)).onResponseReceived(any(RequestStats.class));
    }

    /**
     * Test for {@link NetworkEventReporter#responseInputStreamError(NetworkEventReporter.InspectorRequest, NetworkEventReporter.InspectorResponse, IOException)}
     *
     * @throws Exception
     */
    @Test
    public void testResponseInputStreamError() throws Exception {
        HandlerThread handlerThread = new HandlerThread("back");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler handler = new Handler(looper);
        ShadowLooper shadowLooper = (ShadowLooper) ShadowExtractor.extract(looper);

        NetworkManager networkManager = mock(NetworkManager.class);

        NetworkEventReporter networkEventReporter = new NetworkEventReporterImpl();
        networkEventReporter.onInitialized(RuntimeEnvironment.application, handler, networkManager);

        NetworkEventReporter.InspectorRequest inspectorRequest = mock(NetworkEventReporter.InspectorRequest.class);
        NetworkEventReporter.InspectorResponse inspectorResponse = mock(NetworkEventReporter.InspectorResponse.class);

        networkEventReporter.responseInputStreamError(inspectorRequest, inspectorResponse, new IOException(""));
        shadowLooper.runToEndOfTasks();

        //verify onResponseInputStreamError gets called once
        verify(networkManager, times(1)).onResponseInputStreamError(any(RequestStats.class));
    }
}
