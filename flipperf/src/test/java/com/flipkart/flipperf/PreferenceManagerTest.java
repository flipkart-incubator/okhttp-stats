package com.flipkart.flipperf;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.flipkart.flipperf.newlib.toolbox.PreferenceManager;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

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
}
