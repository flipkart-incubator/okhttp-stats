package com.flipkart.flipperf;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by anirudh.r on 12/05/16 at 3:24 PM.
 */
public class FlipperfPreferenceManager {

    private static final String MyPREFERENCES = "MyPrefs";
    private final SharedPreferences mSharedPreferences;

    public FlipperfPreferenceManager(Context context) {
        this.mSharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public void setAverageSpeed(String networkType, float avgSpeed) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat(networkType, avgSpeed);
        editor.apply();
    }

    public void getAverageSpeed(String networkType) {
        this.mSharedPreferences.getFloat(networkType, 0F);
    }
}
