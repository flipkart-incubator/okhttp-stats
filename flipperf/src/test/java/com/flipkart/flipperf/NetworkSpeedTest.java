package com.flipkart.flipperf;

import com.flipkart.flipperf.toolbox.NetworkSpeed;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by anirudh.r on 17/05/16 at 10:52 PM.
 * Test for {@link NetworkSpeed}.
 * Pretty much nothing. This test just for sake of coverage
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NetworkSpeedTest {

    @Test
    public void testNetworkSpeed() throws Exception {

        NetworkSpeed fastNetwork = NetworkSpeed.FAST_NETWORK;
        NetworkSpeed mediumNetwork = NetworkSpeed.MEDIUM_NETWORK;
        NetworkSpeed slowNetwork = NetworkSpeed.SLOW_NETWORK;
    }
}
