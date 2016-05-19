package com.flipkart.flipperf.newlib.toolbox;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by anirudh.r on 12/05/16 at 3:24 PM.
 */
public class FlipperfPreferenceManager {

    private static final String FLIPPERF_PREFERENCES = "FLIPPERF_PREFERENCES";
    private final SharedPreferences mSharedPreferences;

    public FlipperfPreferenceManager(Context context) {
        this.mSharedPreferences = context.getSharedPreferences(FLIPPERF_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void setAverageSpeed(String networkType, float avgSpeed) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat(networkType, avgSpeed);
        editor.apply();
    }

    public float getAverageSpeed(String networkType) {
        return this.mSharedPreferences.getFloat(networkType, 0F);
    }
}
