package com.flipkart.flipperf;

import com.flipkart.flipperf.model.RequestStats;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.net.MalformedURLException;
import java.net.URL;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RequestStatsTest {

    /**
     * Test to verify the setters and getters for {@link RequestStats}
     */
    @Test
    public void testDataIntegrity() throws MalformedURLException {
        RequestStats requestStats = new RequestStats(1);

        requestStats.setUrl(new URL("http://www.flipkart.com"));
        requestStats.setSize("20");
        requestStats.setMethodType("POST");
        requestStats.setResponseSize("40");
        requestStats.setStartTime(2);
        requestStats.setEndTime(3);
        requestStats.setHttpStatusCode(200);
        requestStats.setHostName("flipkart.com");

        Assert.assertTrue(requestStats.getId() == 1);
        Assert.assertTrue(requestStats.getUrl().toString().equals("http://www.flipkart.com"));
        Assert.assertTrue(requestStats.getSize().equals("20"));
        Assert.assertTrue(requestStats.getMethodType().equals("POST"));
        Assert.assertTrue(requestStats.getResponseSize().equals("40"));
        Assert.assertTrue(requestStats.getStartTime() == 2);
        Assert.assertTrue(requestStats.getEndTime() == 3);
        Assert.assertTrue(requestStats.getHttpStatusCode() == 200);
        Assert.assertTrue(requestStats.getHostName().equals("flipkart.com"));
    }
}
