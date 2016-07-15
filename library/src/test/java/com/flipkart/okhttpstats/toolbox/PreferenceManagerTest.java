package com.flipkart.okhttpstats.toolbox;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.flipkart.okhttpstats.BuildConfig;
import com.flipkart.okhttpstats.handler.PersistentStatsHandler;
import com.flipkart.okhttpstats.toolbox.PreferenceManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.internal.ShadowExtractor;
import org.robolectric.shadows.ShadowConnectivityManager;

/**
 * Created by anirudh.r on 13/05/16 at 10:48 AM.
 * Test for {@link PreferenceManager}
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class PreferenceManagerTest {

    /**
     * Test for {@link PreferenceManager}
     *
     * @throws Exception
     */
    @Test
    public void testSharedPreference() throws Exception {

        PreferenceManager preferenceManager = new PreferenceManager(RuntimeEnvironment.application);

        ConnectivityManager connectivityManager = (ConnectivityManager) RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        preferenceManager.setAverageSpeed(networkInfo.getTypeName(), 20.1F);

        Float avgSpeed = preferenceManager.getAverageSpeed(networkInfo.getTypeName());

        Assert.assertTrue(avgSpeed == 20.1F);
    }

    /**
     * Test to save network speed for different types of network
     *
     * @throws Exception
     */
    @Test
    public void testSharedPrefForDiffNetwork() throws Exception {
        PreferenceManager preferenceManager = new PreferenceManager(RuntimeEnvironment.application);
        PersistentStatsHandler persistentStatsHandler = new PersistentStatsHandler(RuntimeEnvironment.application);

        ConnectivityManager connectivityManager = (ConnectivityManager) RuntimeEnvironment.application.getSystemService(Context.CONNECTIVITY_SERVICE);
        ShadowConnectivityManager shadowConnectivityManager = (ShadowConnectivityManager) ShadowExtractor.extract(connectivityManager);
        shadowConnectivityManager.setNetworkInfo(ConnectivityManager.TYPE_WIFI, connectivityManager.getActiveNetworkInfo());

        String networkKey = persistentStatsHandler.getNetworkKey(shadowConnectivityManager.getActiveNetworkInfo());

        preferenceManager.setAverageSpeed(networkKey, 20.1F);
        //assert that avg speed for WIFI is same as saved
        Assert.assertTrue(preferenceManager.getAverageSpeed(networkKey) == 20.1F);

        shadowConnectivityManager.setNetworkInfo(ConnectivityManager.TYPE_MOBILE, connectivityManager.getActiveNetworkInfo());
        networkKey = persistentStatsHandler.getNetworkKey(shadowConnectivityManager.getActiveNetworkInfo());
        preferenceManager.setAverageSpeed(networkKey, 1.1F);

        //assert that avg speed for MOBILE is same as saved
        Assert.assertTrue(preferenceManager.getAverageSpeed(networkKey) == 1.1F);
    }
}
