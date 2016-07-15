package com.flipkart.okhttpstats.model;

import android.net.NetworkInfo;

import com.flipkart.okhttpstats.BuildConfig;
import com.flipkart.okhttpstats.model.RequestStats;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.net.MalformedURLException;
import java.net.URL;

import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RequestStatsTest {

    /**
     * Test to verify the setters and getters for {@link RequestStats}
     */
    @Test
    public void testDataIntegrity() throws MalformedURLException {
        RequestStats requestStats = new RequestStats(1);

        NetworkInfo networkInfo = mock(NetworkInfo.class);

        requestStats.setUrl(new URL("http://www.flipkart.com"));
        requestStats.setRequestSize(20);
        requestStats.setMethodType("POST");
        requestStats.setResponseSize(40);
        requestStats.setStartTime(2);
        requestStats.setEndTime(3);
        requestStats.setStatusCode(200);
        requestStats.setHostName("flipkart.com");

        Assert.assertTrue(requestStats.getId() == 1);
        Assert.assertTrue(requestStats.getUrl().toString().equals("http://www.flipkart.com"));
        Assert.assertTrue(requestStats.getRequestSize() == 20);
        Assert.assertTrue(requestStats.getMethodType().equals("POST"));
        Assert.assertTrue(requestStats.getResponseSize() == 40);
        Assert.assertTrue(requestStats.getStartTime() == 2);
        Assert.assertTrue(requestStats.getEndTime() == 3);
        Assert.assertTrue(requestStats.getStatusCode() == 200);
        Assert.assertTrue(requestStats.getHostName().equals("flipkart.com"));
    }
}
