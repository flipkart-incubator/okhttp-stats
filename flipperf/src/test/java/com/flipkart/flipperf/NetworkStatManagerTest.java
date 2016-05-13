package com.flipkart.flipperf;

import com.flipkart.flipperf.model.RequestResponseModel;

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
     * Test for {@link NetworkStatManager#onResponseReceived(RequestResponseModel)}
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

        RequestResponseModel requestResponseModel = new RequestResponseModel();
        networkStatManager.onResponseReceived(requestResponseModel);

        //verify onResponseReceived gets called once
        verify(onResponseReceivedListener, times(1)).onResponseReceived(requestResponseModel);
        reset(onResponseReceivedListener);

        //adding another listener
        networkStatManager.addListener(onResponseReceivedListener1);

        //assert size is 2
        Assert.assertTrue(networkStatManager.getOnResponseReceivedListenerList().size() == 2);
        networkStatManager.onResponseReceived(requestResponseModel);

        //verify onResponseReceived of 1st listener gets called once
        verify(onResponseReceivedListener, times(1)).onResponseReceived(requestResponseModel);
        //verify onResponseReceived of 2nd listener gets called once
        verify(onResponseReceivedListener1, times(1)).onResponseReceived(requestResponseModel);
    }

    /**
     * Test for {@link NetworkStatManager#onHttpExchangeError(RequestResponseModel)}
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

        RequestResponseModel requestResponseModel = new RequestResponseModel();
        networkStatManager.onHttpExchangeError(requestResponseModel);

        //verify onHttpErrorReceived gets called once
        verify(onResponseReceivedListener, times(1)).onHttpErrorReceived(requestResponseModel);
        reset(onResponseReceivedListener);

        //adding another listener
        networkStatManager.addListener(onResponseReceivedListener1);

        //assert size is 2
        Assert.assertTrue(networkStatManager.getOnResponseReceivedListenerList().size() == 2);
        networkStatManager.onHttpExchangeError(requestResponseModel);

        //verify onHttpErrorReceived of 1st listener gets called once
        verify(onResponseReceivedListener, times(1)).onHttpErrorReceived(requestResponseModel);
        //verify onHttpErrorReceived of 2nd listener gets called once
        verify(onResponseReceivedListener1, times(1)).onHttpErrorReceived(requestResponseModel);
    }

    /**
     * Test for {@link NetworkStatManager#onResponseInputStreamError(RequestResponseModel)}
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

        RequestResponseModel requestResponseModel = new RequestResponseModel();
        networkStatManager.onResponseInputStreamError(requestResponseModel);

        //verify onInputStreamReadError gets called once
        verify(onResponseReceivedListener, times(1)).onInputStreamReadError(requestResponseModel);
        reset(onResponseReceivedListener);

        //adding another listener
        networkStatManager.addListener(onResponseReceivedListener1);

        //assert size is 2
        Assert.assertTrue(networkStatManager.getOnResponseReceivedListenerList().size() == 2);
        networkStatManager.onResponseInputStreamError(requestResponseModel);

        //verify onInputStreamReadError of 1st listener gets called once
        verify(onResponseReceivedListener, times(1)).onInputStreamReadError(requestResponseModel);
        //verify onInputStreamReadError of 2nd listener gets called once
        verify(onResponseReceivedListener1, times(1)).onInputStreamReadError(requestResponseModel);
    }

    /**
     * Test for {@link NetworkStatManager#setNetworkType(String)}
     *
     * @throws Exception
     */
    @Test
    public void testNetworkType() throws Exception {
        NetworkStatManager networkStatManager = new NetworkStatManager(RuntimeEnvironment.application);
        networkStatManager.setNetworkType("WIFI");
    }
}
