package com.flipkart.okhttpstats.model;

import android.net.NetworkInfo;

import com.flipkart.okhttpstats.BuildConfig;

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

        requestStats.url = new URL("http://www.flipkart.com");
        requestStats.requestSize = 20;
        requestStats.methodType = "POST";
        requestStats.responseSize = 40;
        requestStats.startTime = 2;
        requestStats.endTime = 3;
        requestStats.statusCode = 200;
        requestStats.hostName = "flipkart.com";

        Assert.assertTrue(requestStats.id == 1);
        Assert.assertTrue(requestStats.url.toString().equals("http://www.flipkart.com"));
        Assert.assertTrue(requestStats.requestSize == 20);
        Assert.assertTrue(requestStats.methodType.equals("POST"));
        Assert.assertTrue(requestStats.responseSize == 40);
        Assert.assertTrue(requestStats.startTime == 2);
        Assert.assertTrue(requestStats.endTime == 3);
        Assert.assertTrue(requestStats.statusCode == 200);
        Assert.assertTrue(requestStats.hostName.equals("flipkart.com"));
    }
}
