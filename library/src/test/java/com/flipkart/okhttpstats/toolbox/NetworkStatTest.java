package com.flipkart.okhttpstats.toolbox;

import com.flipkart.okhttpstats.BuildConfig;
import com.flipkart.okhttpstats.model.RequestStats;
import com.flipkart.okhttpstats.toolbox.NetworkStat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by anirudh.r on 17/05/16 at 10:53 PM.
 * Test for {@link NetworkStat}
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NetworkStatTest {

    @Test
    public void testNetworkStat() throws Exception {

        RequestStats requestStats = new RequestStats(1);
        requestStats.setResponseSize(20);
        requestStats.setStartTime(1);
        requestStats.setEndTime(2);

        RequestStats requestStats1 = new RequestStats(1);
        requestStats.setResponseSize(20);
        requestStats.setStartTime(1);
        requestStats.setEndTime(2);

        RequestStats requestStats2 = new RequestStats(1);
        requestStats.setResponseSize(20);
        requestStats.setStartTime(1);
        requestStats.setEndTime(2);

    }
}
