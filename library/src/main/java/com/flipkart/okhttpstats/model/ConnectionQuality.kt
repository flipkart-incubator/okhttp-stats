package com.flipkart.okhttpstats.model


enum class ConnectionQuality(private val min: Int, private val max: Int) {
    /**
     * Bandwidth under 150 kbps.
     */
    POOR(0, 150),
    /**
     * Bandwidth between 150 and 550 kbps.
     */
    MODERATE(151, 550),
    /**
     * Bandwidth between 550 and 2000 kbps.
     */
    GOOD(551, 2000),
    /**
     * EXCELLENT - Bandwidth over 2000 kbps.
     */
    EXCELLENT(2001, Int.MAX_VALUE),
    /**
     * Placeholder for unknown bandwidth. This is the initial value and will stay at this value
     * if a bandwidth cannot be accurately found.
     */
    UNKNOWN(0, Int.MAX_VALUE);

    fun inRange(bandWidth: Int): Boolean {
        return bandWidth in min until max
    }

    override fun toString(): String {
        return "$name min = $min max =$max" // NON-NLS
    }

    companion object {
        /**
         * @param networkSpeed in Kbps
         * @return ConnectionQuality derived from networkSpeed.
         */
        @JvmStatic
        fun getConnectionQualityFromSpeed(networkSpeed: Int): ConnectionQuality {
            val bandwidth = networkSpeed * 8
            return getConnectionQualityFromBandWidth(bandwidth)
        }

        @JvmStatic
        fun getConnectionQualityFromBandWidth(bandwidth: Int): ConnectionQuality {
            return when {
                POOR.inRange(bandwidth) -> {
                    POOR
                }
                MODERATE.inRange(bandwidth) -> {
                    MODERATE
                }
                GOOD.inRange(bandwidth) -> {
                    GOOD
                }
                EXCELLENT.inRange(bandwidth) -> {
                    EXCELLENT
                }
                else -> {
                    UNKNOWN
                }
            }
        }
    }
}