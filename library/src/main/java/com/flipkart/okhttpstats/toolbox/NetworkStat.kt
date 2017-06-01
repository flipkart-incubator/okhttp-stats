package com.flipkart.okhttpstats.toolbox

import com.flipkart.okhttpstats.model.RequestStats

/**
 * Utility class to calculate the average network speed
 */
class NetworkStat {
    private var peakSpeed = 0.0
    private val requestStatQueue: java.util.Queue<RequestStats>
    private var totalSize = 0.0
    var currentAvgSpeed = 0.0

    init {
        requestStatQueue = java.util.LinkedList<RequestStats>()
    }

    @Synchronized fun addRequestStat(requestStats: RequestStats) {
        var apiSpeed: Long = 0
        if (requestStats.endTime > requestStats.startTime) {
            apiSpeed = requestStats.responseSize / (requestStats.endTime - requestStats.startTime)
        }
        if (apiSpeed > peakSpeed) {
            peakSpeed = apiSpeed.toDouble()
        }
        requestStatQueue.add(requestStats)
        totalSize += requestStats.responseSize.toDouble()
        if (requestStatQueue.size > Companion.MAX_QUEUE_SIZE) {
            val requestStat = requestStatQueue.poll()
            totalSize -= requestStat.responseSize.toDouble()
        }
        calculateAvgSpeed()
    }

    private fun calculateAvgSpeed() {
        var newAvgSpeed = 0.0
        for (requestStats in requestStatQueue) {
            var apiSpeed: Long = 0
            if (requestStats.endTime > requestStats.startTime) {
                apiSpeed = requestStats.responseSize / (requestStats.endTime - requestStats.startTime)
            }
            val proportion = requestStats.responseSize / totalSize
            newAvgSpeed += apiSpeed * proportion
        }
        currentAvgSpeed = newAvgSpeed
    }

    companion object {
        private val MAX_QUEUE_SIZE = 5
    }
}

private operator fun Long.compareTo(startTime: Long?): Int {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
