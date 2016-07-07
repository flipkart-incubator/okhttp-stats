package com.flipkart.okhttpstats;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.flipkart.okhttpstats.handler.OnResponseReceivedListener;
import com.flipkart.okhttpstats.handler.PersistentStatsHandler;
import com.flipkart.okhttpstats.model.RequestStats;
import com.flipkart.okhttpstats.toolbox.NetworkSpeed;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;

import java.io.IOException;
import java.net.SocketTimeoutException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by anirudh.r on 13/05/16 at 12:28 AM.
 * Test for {@link PersistentStatsHandler}
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PersistentStatsHandlerTest {

    /**
     * Test for {@link PersistentStatsHandler#addListener(OnResponseReceivedListener)}
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

        OnResponseReceivedListener onResponseReceivedListener = mock(OnResponseReceivedListener.class);

        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application);
        persistentStatsHandler.addListener(onResponseReceivedListener);

        //assert size is 1
        Assert.assertTrue(persistentStatsHandler.getOnResponseReceivedListenerList().size() == 1);
    }

    /**
     * Test for {@link PersistentStatsHandler#removeListener(OnResponseReceivedListener)}
     *
     * @throws Exception
     */
    @Test
    public void testRemoveListener() throws Exception {
        OnResponseReceivedListener onResponseReceivedListener = mock(OnResponseReceivedListener.class);

        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application);
        persistentStatsHandler.addListener(onResponseReceivedListener);

        //assert size is 1
        Assert.assertTrue(persistentStatsHandler.getOnResponseReceivedListenerList().size() == 1);
        persistentStatsHandler.removeListener(onResponseReceivedListener);

        //assert size is 0
        Assert.assertTrue(persistentStatsHandler.getOnResponseReceivedListenerList().size() == 0);
    }

    /**
     * Test for {@link PersistentStatsHandler#onResponseReceived(RequestStats)}
     *
     * @throws Exception
     */
    @Test
    public void testOnResponseReceived() throws Exception {
        OnResponseReceivedListener onResponseReceivedListener = mock(OnResponseReceivedListener.class);
        OnResponseReceivedListener onResponseReceivedListener1 = mock(OnResponseReceivedListener.class);

        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application);
        persistentStatsHandler.addListener(onResponseReceivedListener);

        //assert size is 1
        Assert.assertTrue(persistentStatsHandler.getOnResponseReceivedListenerList().size() == 1);

        RequestStats requestStats = new RequestStats(1);
        persistentStatsHandler.onResponseReceived(requestStats);

        //verify onResponseReceived gets called once
        verify(onResponseReceivedListener, times(1)).onResponseSuccess(any(NetworkInfo.class), eq(requestStats));
        reset(onResponseReceivedListener);

        //adding another listener
        persistentStatsHandler.addListener(onResponseReceivedListener1);

        //assert size is 2
        Assert.assertTrue(persistentStatsHandler.getOnResponseReceivedListenerList().size() == 2);
        persistentStatsHandler.onResponseReceived(requestStats);

        //verify onResponseReceived of 1st listener gets called once
        verify(onResponseReceivedListener, times(1)).onResponseSuccess(any(NetworkInfo.class), eq(requestStats));
        //verify onResponseReceived of 2nd listener gets called once
        verify(onResponseReceivedListener1, times(1)).onResponseSuccess(any(NetworkInfo.class), eq(requestStats));
    }

    /**
     * Test for {@link PersistentStatsHandler#onHttpExchangeError(RequestStats, IOException)}
     *
     * @throws Exception
     */
    @Test
    public void testOnHttpExchangeError() throws Exception {
        OnResponseReceivedListener onResponseReceivedListener = mock(OnResponseReceivedListener.class);
        OnResponseReceivedListener onResponseReceivedListener1 = mock(OnResponseReceivedListener.class);

        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application);
        persistentStatsHandler.addListener(onResponseReceivedListener);

        //assert size is 1
        Assert.assertTrue(persistentStatsHandler.getOnResponseReceivedListenerList().size() == 1);

        RequestStats requestStats = new RequestStats(1);
        persistentStatsHandler.onHttpExchangeError(requestStats, new IOException(""));

        //verify onHttpErrorReceived gets called once
        verify(onResponseReceivedListener, times(1)).onResponseError(any(NetworkInfo.class), eq(requestStats), any(IOException.class));

        //adding another listener
        persistentStatsHandler.addListener(onResponseReceivedListener1);
        reset(onResponseReceivedListener);

        //assert size is 2
        Assert.assertTrue(persistentStatsHandler.getOnResponseReceivedListenerList().size() == 2);
        persistentStatsHandler.onHttpExchangeError(requestStats, new IOException(""));

        verify(onResponseReceivedListener, times(1)).onResponseError(any(NetworkInfo.class), eq(requestStats), any(IOException.class));
        verify(onResponseReceivedListener1, times(1)).onResponseError(any(NetworkInfo.class), eq(requestStats), any(IOException.class));
    }

    /**
     * Test for number of {@link OnResponseReceivedListener} in {@link PersistentStatsHandler}
     *
     * @throws Exception
     */
    @Test
    public void testOnInputStreamError() throws Exception {
        OnResponseReceivedListener onResponseReceivedListener = mock(OnResponseReceivedListener.class);
        OnResponseReceivedListener onResponseReceivedListener1 = mock(OnResponseReceivedListener.class);

        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application);
        persistentStatsHandler.addListener(onResponseReceivedListener);

        //assert size is 1
        Assert.assertTrue(persistentStatsHandler.getOnResponseReceivedListenerList().size() == 1);

        RequestStats requestStats = new RequestStats(1);
        persistentStatsHandler.onResponseInputStreamError(requestStats, new SocketTimeoutException());

        //verify onInputStreamReadError gets called once
        reset(onResponseReceivedListener);

        //adding another listener
        persistentStatsHandler.addListener(onResponseReceivedListener1);

        //assert size is 2
        Assert.assertTrue(persistentStatsHandler.getOnResponseReceivedListenerList().size() == 2);
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
     * Test for {@link PersistentStatsHandler#getNetworkSpeed()}
     *
     * @throws Exception
     */
    @Test
    public void testGetNetworkSpeed() throws Exception {
        ConnectivityManager connectivityManager = (ConnectivityManager) RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        ShadowConnectivityManager shadowConnectivityManager = (ShadowConnectivityManager) ShadowExtractor.extract(connectivityManager);
        ShadowNetworkInfo shadowOfActiveNetworkInfo = (ShadowNetworkInfo) ShadowExtractor.extract(connectivityManager.getActiveNetworkInfo());
        shadowOfActiveNetworkInfo.setConnectionType(ConnectivityManager.TYPE_WIFI);
        shadowConnectivityManager.setNetworkInfo(ConnectivityManager.TYPE_WIFI, connectivityManager.getActiveNetworkInfo());

        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application);

        //assert that TYPE_WIFI is fast network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.FAST_NETWORK));

        shadowOfActiveNetworkInfo.setConnectionType(ConnectivityManager.TYPE_MOBILE);
        shadowConnectivityManager.setNetworkInfo(ConnectivityManager.TYPE_MOBILE, connectivityManager.getActiveNetworkInfo());
        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_CDMA);
        //assert that NETWORK_TYPE_CDMA is slow network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.SLOW_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_1xRTT);
        //assert that NETWORK_TYPE_1xRTT is slow network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.SLOW_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_EDGE);
        //assert that NETWORK_TYPE_EDGE is slow network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.SLOW_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_EVDO_0);
        //assert that NETWORK_TYPE_EVDO_0 is medium network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.MEDIUM_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_EVDO_A);
        //assert that NETWORK_TYPE_EVDO_A is medium network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.MEDIUM_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_GPRS);
        //assert that NETWORK_TYPE_GPRS is slow network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.SLOW_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_HSDPA);
        //assert that NETWORK_TYPE_HSDPA is fast network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.FAST_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_HSPA);
        //assert that NETWORK_TYPE_HSPA is fast network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.FAST_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_HSUPA);
        //assert that NETWORK_TYPE_HSUPA is fast network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.FAST_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_UMTS);
        //assert that NETWORK_TYPE_UMTS is medium network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.MEDIUM_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_EHRPD);
        //assert that NETWORK_TYPE_EHRPD is fast network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.FAST_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_EVDO_B);
        //assert that NETWORK_TYPE_EVDO_B is fast network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.FAST_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_HSPAP);
        //assert that NETWORK_TYPE_HSPAP is fast network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.FAST_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_IDEN);
        //assert that NETWORK_TYPE_IDEN is fast network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.FAST_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_LTE);
        //assert that NETWORK_TYPE_LTE is fast network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.FAST_NETWORK));

        shadowOfActiveNetworkInfo.setSubType(TelephonyManager.NETWORK_TYPE_UNKNOWN);
        //assert that NETWORK_TYPE_UNKNOWN is slow network
        Assert.assertTrue(persistentStatsHandler.getNetworkSpeed().equals(NetworkSpeed.SLOW_NETWORK));
    }
}
