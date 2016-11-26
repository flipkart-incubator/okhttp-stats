package com.flipkart.okhttpstats.reporter;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.flipkart.okhttpstats.BuildConfig;
import com.flipkart.okhttpstats.handler.NetworkRequestStatsHandler;
import com.flipkart.okhttpstats.model.RequestStats;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
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
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NetworkEventReporterTest {

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

        NetworkRequestStatsHandler networkRequestStatsHandler = mock(NetworkRequestStatsHandler.class);

        NetworkEventReporter networkEventReporter = new NetworkEventReporterImpl(networkRequestStatsHandler);

        NetworkEventReporter.InspectorRequest inspectorRequest = mock(NetworkEventReporter.InspectorRequest.class);
        NetworkEventReporter.InspectorResponse inspectorResponse = mock(NetworkEventReporter.InspectorResponse.class);

        networkEventReporter.responseReceived(inspectorRequest, inspectorResponse);
        shadowLooper.runToEndOfTasks();

        //verify onResponseReceived gets called once
        verify(networkRequestStatsHandler, times(1)).onResponseReceived(any(RequestStats.class));
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

        NetworkRequestStatsHandler networkRequestStatsHandler = mock(NetworkRequestStatsHandler.class);

        NetworkEventReporter networkEventReporter = new NetworkEventReporterImpl(networkRequestStatsHandler);

        NetworkEventReporter.InspectorRequest inspectorRequest = mock(NetworkEventReporter.InspectorRequest.class);

        networkEventReporter.httpExchangeError(inspectorRequest, new IOException("Error"));
        shadowLooper.runToEndOfTasks();

        //verify onHttpExchangeError gets called once
        verify(networkRequestStatsHandler, times(1)).onHttpExchangeError(any(RequestStats.class), any(IOException.class));
    }


    /**
     * Test for {@link NetworkEventReporter#responseInputStreamError(NetworkEventReporter.InspectorRequest, NetworkEventReporter.InspectorResponse, Exception)}
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

        NetworkRequestStatsHandler networkRequestStatsHandler = mock(NetworkRequestStatsHandler.class);

        NetworkEventReporter networkEventReporter = new NetworkEventReporterImpl(networkRequestStatsHandler);

        NetworkEventReporter.InspectorRequest inspectorRequest = mock(NetworkEventReporter.InspectorRequest.class);
        NetworkEventReporter.InspectorResponse inspectorResponse = mock(NetworkEventReporter.InspectorResponse.class);

        networkEventReporter.responseInputStreamError(inspectorRequest, inspectorResponse, new IOException(""));
        shadowLooper.runToEndOfTasks();

        //verify onResponseInputStreamError gets called once
        verify(networkRequestStatsHandler, times(1)).onResponseInputStreamError(any(RequestStats.class), any(IOException.class));
    }
}
