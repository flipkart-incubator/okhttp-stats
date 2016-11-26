package com.flipkart.okhttpstats.handler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.flipkart.okhttpstats.BuildConfig;
import com.flipkart.okhttpstats.model.RequestStats;
import com.flipkart.okhttpstats.toolbox.PreferenceManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;

import java.io.IOException;
import java.net.SocketTimeoutException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by anirudh.r on 13/05/16 at 12:28 AM.
 * Test for {@link PersistentStatsHandler}
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PersistentStatsHandlerTest {

    /**
     * Test for {@link PersistentStatsHandler#addListener(OnResponseListener)}
     *
     * @throws Exception
     */
    @Test
    public void testAddListener() throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        ShadowConnectivityManager shadowConnectivityManager = (ShadowConnectivityManager) ShadowExtractor.extract(connectivityManager);
        ShadowNetworkInfo shadowOfActiveNetworkInfo = (ShadowNetworkInfo) ShadowExtractor.extract(connectivityManager.getActiveNetworkInfo());
        shadowOfActiveNetworkInfo.setConnectionType(ConnectivityManager.TYPE_WIFI);

        shadowConnectivityManager.setNetworkInfo(ConnectivityManager.TYPE_WIFI, connectivityManager.getActiveNetworkInfo());

        OnResponseListener onResponseListener = mock(OnResponseListener.class);

        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application);
        persistentStatsHandler.addListener(onResponseListener);

        //assert size is 1
        Assert.assertTrue(persistentStatsHandler.getOnResponseListeners().size() == 1);
    }

    /**
     * Test for {@link PersistentStatsHandler#removeListener(OnResponseListener)}
     *
     * @throws Exception
     */
    @Test
    public void testRemoveListener() throws Exception {
        OnResponseListener onResponseListener = mock(OnResponseListener.class);

        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application);
        persistentStatsHandler.addListener(onResponseListener);

        //assert size is 1
        Assert.assertTrue(persistentStatsHandler.getOnResponseListeners().size() == 1);
        persistentStatsHandler.removeListener(onResponseListener);

        //assert size is 0
        Assert.assertTrue(persistentStatsHandler.getOnResponseListeners().size() == 0);
    }

    /**
     * Test for {@link PersistentStatsHandler#onResponseReceived(RequestStats)}
     *
     * @throws Exception
     */
    @Test
    public void testOnResponseReceived() throws Exception {
        OnResponseListener onResponseListener = mock(OnResponseListener.class);
        OnResponseListener onResponseListener1 = mock(OnResponseListener.class);

        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application);
        persistentStatsHandler.addListener(onResponseListener);

        //assert size is 1
        Assert.assertTrue(persistentStatsHandler.getOnResponseListeners().size() == 1);

        RequestStats requestStats = new RequestStats(1);
        persistentStatsHandler.onResponseReceived(requestStats);

        //verify onResponseReceived gets called once
        verify(onResponseListener, times(1)).onResponseSuccess(any(NetworkInfo.class), eq(requestStats));
        reset(onResponseListener);

        //adding another listener
        persistentStatsHandler.addListener(onResponseListener1);

        //assert size is 2
        Assert.assertTrue(persistentStatsHandler.getOnResponseListeners().size() == 2);
        persistentStatsHandler.onResponseReceived(requestStats);

        //verify onResponseReceived of 1st listener gets called once
        verify(onResponseListener, times(1)).onResponseSuccess(any(NetworkInfo.class), eq(requestStats));
        //verify onResponseReceived of 2nd listener gets called once
        verify(onResponseListener1, times(1)).onResponseSuccess(any(NetworkInfo.class), eq(requestStats));
    }

    /**
     * Test for {@link PersistentStatsHandler#onHttpExchangeError(RequestStats, IOException)}
     *
     * @throws Exception
     */
    @Test
    public void testOnHttpExchangeError() throws Exception {
        OnResponseListener onResponseListener = mock(OnResponseListener.class);
        OnResponseListener onResponseListener1 = mock(OnResponseListener.class);

        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application);
        persistentStatsHandler.addListener(onResponseListener);

        //assert size is 1
        Assert.assertTrue(persistentStatsHandler.getOnResponseListeners().size() == 1);

        RequestStats requestStats = new RequestStats(1);
        persistentStatsHandler.onHttpExchangeError(requestStats, new IOException(""));

        //verify onHttpErrorReceived gets called once
        verify(onResponseListener, times(1)).onResponseError(any(NetworkInfo.class), eq(requestStats), any(IOException.class));

        //adding another listener
        persistentStatsHandler.addListener(onResponseListener1);
        reset(onResponseListener);

        //assert size is 2
        Assert.assertTrue(persistentStatsHandler.getOnResponseListeners().size() == 2);
        persistentStatsHandler.onHttpExchangeError(requestStats, new IOException(""));

        verify(onResponseListener, times(1)).onResponseError(any(NetworkInfo.class), eq(requestStats), any(IOException.class));
        verify(onResponseListener1, times(1)).onResponseError(any(NetworkInfo.class), eq(requestStats), any(IOException.class));
    }

    /**
     * Test for number of {@link OnResponseListener} in {@link PersistentStatsHandler}
     *
     * @throws Exception
     */
    @Test
    public void testOnInputStreamError() throws Exception {
        OnResponseListener onResponseListener = mock(OnResponseListener.class);
        OnResponseListener onResponseListener1 = mock(OnResponseListener.class);

        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application);
        persistentStatsHandler.addListener(onResponseListener);

        //assert size is 1
        Assert.assertTrue(persistentStatsHandler.getOnResponseListeners().size() == 1);

        RequestStats requestStats = new RequestStats(1);
        persistentStatsHandler.onResponseInputStreamError(requestStats, new SocketTimeoutException());

        //verify onInputStreamReadError gets called once
        reset(onResponseListener);

        //adding another listener
        persistentStatsHandler.addListener(onResponseListener1);

        //assert size is 2
        Assert.assertTrue(persistentStatsHandler.getOnResponseListeners().size() == 2);
    }

    /**
     * Test for {@link PersistentStatsHandler#getWifiSSID()}
     *
     * @throws Exception
     */
    @Test
    public void testGetWifiSSID() throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        ShadowConnectivityManager shadowConnectivityManager = (ShadowConnectivityManager) ShadowExtractor.extract(connectivityManager);
        ShadowNetworkInfo shadowOfActiveNetworkInfo = (ShadowNetworkInfo) ShadowExtractor.extract(connectivityManager.getActiveNetworkInfo());
        shadowOfActiveNetworkInfo.setConnectionType(ConnectivityManager.TYPE_WIFI);
        shadowConnectivityManager.setNetworkInfo(ConnectivityManager.TYPE_WIFI, connectivityManager.getActiveNetworkInfo());

        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application);

        Assert.assertTrue(persistentStatsHandler.getWifiSSID() != 0);
    }

    /**
     * Test for {@link PersistentStatsHandler#getNetworkKey(NetworkInfo)}
     *
     * @throws Exception
     */
    @Test
    public void testGetNetworkKey() throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        ShadowConnectivityManager shadowConnectivityManager = (ShadowConnectivityManager) ShadowExtractor.extract(connectivityManager);
        shadowConnectivityManager.setNetworkInfo(ConnectivityManager.TYPE_WIFI, connectivityManager.getActiveNetworkInfo());

        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application);
        Assert.assertTrue(persistentStatsHandler.getNetworkKey(shadowConnectivityManager.getActiveNetworkInfo()) != null);
    }

    /**
     * Test to verify that {@link PreferenceManager#setAverageSpeed(String, float)} gets called when number of request sent is greater
     * than the max number
     *
     * @throws Exception
     */
    @Test
    public void testSaveToSharedPreferenceCalled() throws Exception {
        PreferenceManager preferenceManager = mock(PreferenceManager.class);

        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application, preferenceManager);
        persistentStatsHandler.setMaxSizeForPersistence(3);

        RequestStats requestStats = new RequestStats(2);
        requestStats.statusCode = (200);

        RequestStats requestStats1 = new RequestStats(2);
        requestStats.statusCode = (200);

        RequestStats requestStats2 = new RequestStats(2);
        requestStats.statusCode = (200);

        persistentStatsHandler.onResponseReceived(requestStats);
        persistentStatsHandler.onResponseReceived(requestStats1);
        persistentStatsHandler.onResponseReceived(requestStats2);

        verify(preferenceManager, times(1)).setAverageSpeed(anyString(), anyFloat());
    }
}
