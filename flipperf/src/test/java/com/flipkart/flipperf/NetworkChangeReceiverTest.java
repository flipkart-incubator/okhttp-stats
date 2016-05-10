package com.flipkart.flipperf;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.flipkart.flipperf.network.NetworkChangeReceiver;
import com.flipkart.flipperf.network.OnNetworkChangeListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by anirudh.r on 10/05/16 at 2:07 PM.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NetworkChangeReceiverTest {

    @Test
    public void testNetworkChangeReceiver() {
        OnNetworkChangeListener onNetworkChangeListener = mock(OnNetworkChangeListener.class);
        NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver(onNetworkChangeListener);

        Intent fakeBroadcastIntent = sendFakeNetworkBroadcast(RuntimeEnvironment.application);
        networkChangeReceiver.onReceive(RuntimeEnvironment.application, fakeBroadcastIntent);

        //verify onNetworkChange got called once
        verify(onNetworkChangeListener, times(1)).onNetworkChange(anyString());
    }

    /**
     * Method to send fake BroadcastReceiver for network change
     *
     * @param context {@link Context}
     */
    private Intent sendFakeNetworkBroadcast(Context context) {
        Intent intent = new Intent();
        intent.setAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.sendBroadcast(intent);

        return intent;
    }
}
