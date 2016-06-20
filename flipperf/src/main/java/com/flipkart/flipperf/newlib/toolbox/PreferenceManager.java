package com.flipkart.flipperf.newlib.toolbox;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Preference Manager to store network speed
 */
public class PreferenceManager {

    private static final String PREFERENCES = "PREFERENCES";
    private final SharedPreferences mSharedPreferences;

    public PreferenceManager(Context context) {
        this.mSharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
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
