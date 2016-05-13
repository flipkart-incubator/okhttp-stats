package com.flipkart.flipperf;

import com.flipkart.flipperf.model.RequestStats;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RequestStatsTest {

    /**
     * Test to verify the setters and getters for {@link RequestStats}
     */
    @Test
    public void testDataIntegrity() {
        RequestStats requestStats = new RequestStats();

        requestStats.setRequestId(1);
        requestStats.setRequestUrl("Some_url");
        requestStats.setRequestSize("20");
        requestStats.setRequestMethodType("POST");
        requestStats.setResponseSize("40");
        requestStats.setApiSpeed(1.1);
        requestStats.setResponseInputStreamError("Error reading input stream");
        requestStats.setHttpExchangeErrorMessage("Error reading response");
        requestStats.setResponseTime(2);
        requestStats.setResponseStatusCode(200);
        requestStats.setNetworkType("WIFI");
        requestStats.setHostName("flipkart.com");

        Assert.assertTrue(requestStats.getRequestId() == 1);
        Assert.assertTrue(requestStats.getRequestUrl().equals("Some_url"));
        Assert.assertTrue(requestStats.getRequestSize().equals("20"));
        Assert.assertTrue(requestStats.getRequestMethodType().equals("POST"));
        Assert.assertTrue(requestStats.getResponseSize().equals("40"));
        Assert.assertTrue(requestStats.getApiSpeed() == 1.1);
        Assert.assertTrue(requestStats.getResponseInputStreamError().equals("Error reading input stream"));
        Assert.assertTrue(requestStats.getHttpExchangeErrorMessage().equals("Error reading response"));
        Assert.assertTrue(requestStats.getResponseTime() == 2);
        Assert.assertTrue(requestStats.getResponseStatusCode() == 200);
        Assert.assertTrue(requestStats.getNetworkType().equals("WIFI"));
        Assert.assertTrue(requestStats.getHostName().equals("flipkart.com"));
    }
}
