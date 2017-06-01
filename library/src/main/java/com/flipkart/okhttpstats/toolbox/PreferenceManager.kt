package com.flipkart.okhttpstats.toolbox

/**
 * Preference Manager to store network speed
 */
class PreferenceManager(context: android.content.Context) {
    private val sharedPreferences: android.content.SharedPreferences

    init {
        this.sharedPreferences = context.getSharedPreferences(PreferenceManager.Companion.PREFERENCES, android.content.Context.MODE_PRIVATE)
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