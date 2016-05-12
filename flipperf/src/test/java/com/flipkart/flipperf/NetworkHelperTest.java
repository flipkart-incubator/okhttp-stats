package com.flipkart.flipperf;

import com.flipkart.flipperf.network.NetworkHelper;
import com.flipkart.flipperf.network.NetworkType;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by anirudh.r on 12/05/16 at 12:02 PM.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NetworkHelperTest {

    /**
     * Verify that networktype is not null
     */
    @Test
    public void testNetworkType() {
        String networkType = NetworkHelper.getNetworkType(RuntimeEnvironment.application);
        Assert.assertNotNull(networkType);
    }

    @Test
    public void testWifiSSID() {
        String wifiSSID = NetworkHelper.getWifiSSID(RuntimeEnvironment.application);
        Assert.assertNotNull(wifiSSID);
    }

    @Test
    public void testOperatorName() {
        String operatorName = NetworkHelper.getMobileOperatorName(RuntimeEnvironment.application);
        Assert.assertTrue(operatorName == null);
    }

    @Test
    public void testNetworkEquals() {
        //since it is running in emulator environment
        String currentNetwork = NetworkType.EDGE;
        String networkType = NetworkHelper.getNetworkType(RuntimeEnvironment.application);
        Assert.assertTrue(currentNetwork.equals(networkType));
    }
}
