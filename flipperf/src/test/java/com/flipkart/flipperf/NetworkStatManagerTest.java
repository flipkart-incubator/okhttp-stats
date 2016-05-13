package com.flipkart.flipperf;

import android.net.NetworkInfo;

import com.flipkart.flipperf.model.RequestStats;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by anirudh.r on 13/05/16 at 12:28 AM.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NetworkStatManagerTest {

    /**
     * Test for {@link NetworkStatManager#addListener(OnResponseReceivedListener)}
     *
     * @throws Exception
     */
    @Test
    public void testAddListener() throws Exception {

        OnResponseReceivedListener onResponseReceivedListener = mock(OnResponseReceivedListener.class);

        NetworkStatManager networkStatManager = new NetworkStatManager(RuntimeEnvironment.application);
        networkStatManager.addListener(onResponseReceivedListener);

        //assert size is 1
        Assert.assertTrue(networkStatManager.getOnResponseReceivedListenerList().size() == 1);
    }

    /**
     * Test for {@link NetworkStatManager#unregisterListener(OnResponseReceivedListener)}
     *
     * @throws Exception
     */
    @Test
    public void testRemoveListener() throws Exception {

        OnResponseReceivedListener onResponseReceivedListener = mock(OnResponseReceivedListener.class);

        NetworkStatManager networkStatManager = new NetworkStatManager(RuntimeEnvironment.application);
        networkStatManager.addListener(onResponseReceivedListener);

        //assert size is 1
        Assert.assertTrue(networkStatManager.getOnResponseReceivedListenerList().size() == 1);

        networkStatManager.unregisterListener(onResponseReceivedListener);

        //assert size is 0
        Assert.assertTrue(networkStatManager.getOnResponseReceivedListenerList().size() == 0);
    }

    /**
     * Test for {@link NetworkStatManager#onResponseReceived(RequestStats)}
     *
     * @throws Exception
     */
    @Test
    public void testOnResponseReceived() throws Exception {

        OnResponseReceivedListener onResponseReceivedListener = mock(OnResponseReceivedListener.class);
        OnResponseReceivedListener onResponseReceivedListener1 = mock(OnResponseReceivedListener.class);

        NetworkStatManager networkStatManager = new NetworkStatManager(RuntimeEnvironment.application);
        networkStatManager.addListener(onResponseReceivedListener);

        //assert size is 1
        Assert.assertTrue(networkStatManager.getOnResponseReceivedListenerList().size() == 1);

        RequestStats requestStats = new RequestStats(1);
        networkStatManager.onResponseReceived(requestStats);

        //verify onResponseReceived gets called once
        verify(onResponseReceivedListener, times(1)).onResponseReceived(requestStats);
        reset(onResponseReceivedListener);

        //adding another listener
        networkStatManager.addListener(onResponseReceivedListener1);

        //assert size is 2
        Assert.assertTrue(networkStatManager.getOnResponseReceivedListenerList().size() == 2);
        networkStatManager.onResponseReceived(requestStats);

        //verify onResponseReceived of 1st listener gets called once
        verify(onResponseReceivedListener, times(1)).onResponseReceived(requestStats);
        //verify onResponseReceived of 2nd listener gets called once
        verify(onResponseReceivedListener1, times(1)).onResponseReceived(requestStats);
    }

    /**
     * Test for {@link NetworkStatManager#onHttpExchangeError(RequestStats)}
     *
     * @throws Exception
     */
    @Test
    public void testOnHttpExchangeError() throws Exception {

        OnResponseReceivedListener onResponseReceivedListener = mock(OnResponseReceivedListener.class);
        OnResponseReceivedListener onResponseReceivedListener1 = mock(OnResponseReceivedListener.class);

        NetworkStatManager networkStatManager = new NetworkStatManager(RuntimeEnvironment.application);
        networkStatManager.addListener(onResponseReceivedListener);

        //assert size is 1
        Assert.assertTrue(networkStatManager.getOnResponseReceivedListenerList().size() == 1);

        RequestStats requestStats = new RequestStats(1);
        networkStatManager.onHttpExchangeError(requestStats);

        //verify onHttpErrorReceived gets called once

        //adding another listener
        networkStatManager.addListener(onResponseReceivedListener1);

        //assert size is 2
        Assert.assertTrue(networkStatManager.getOnResponseReceivedListenerList().size() == 2);
        networkStatManager.onHttpExchangeError(requestStats);

    }

    /**
     * Test for {@link NetworkStatManager#onResponseInputStreamError(RequestStats)}
     *
     * @throws Exception
     */
    @Test
    public void testOnInputStreamError() throws Exception {

        OnResponseReceivedListener onResponseReceivedListener = mock(OnResponseReceivedListener.class);
        OnResponseReceivedListener onResponseReceivedListener1 = mock(OnResponseReceivedListener.class);

        NetworkStatManager networkStatManager = new NetworkStatManager(RuntimeEnvironment.application);
        networkStatManager.addListener(onResponseReceivedListener);

        //assert size is 1
        Assert.assertTrue(networkStatManager.getOnResponseReceivedListenerList().size() == 1);

        RequestStats requestStats = new RequestStats(1);
        networkStatManager.onResponseInputStreamError(requestStats);

        //verify onInputStreamReadError gets called once
        reset(onResponseReceivedListener);

        //adding another listener
        networkStatManager.addListener(onResponseReceivedListener1);

        //assert size is 2
        Assert.assertTrue(networkStatManager.getOnResponseReceivedListenerList().size() == 2);
        networkStatManager.onResponseInputStreamError(requestStats);

    }

    /**
     * Test for {@link NetworkStatManager#setNetworkType(NetworkInfo)}
     *
     * @throws Exception
     */
    @Test
    public void testNetworkType() throws Exception {
        NetworkStatManager networkStatManager = new NetworkStatManager(RuntimeEnvironment.application);
    }
}
