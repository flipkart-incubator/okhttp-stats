package com.flipkart.okhttpstats.kotlin.toolbox

import android.content.Context
import android.content.SharedPreferences

/**
 * Preference Manager to store network speed
 */
class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences

    init {
        this.sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    }

    fun setAverageSpeed(networkType: String, avgSpeed: Float) {
        val editor = sharedPreferences.edit()
        editor.putFloat(networkType, avgSpeed)
        editor.apply()
    }

    fun getAverageSpeed(networkType: String): Float {
        return this.sharedPreferences.getFloat(networkType, 0f)
    }

    companion object {
        private val PREFERENCES = "PREFERENCES"
    }
}