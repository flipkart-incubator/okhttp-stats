package com.flipkart.flipperf.newlib.toolbox;

import com.flipkart.flipperf.newlib.model.RequestStats;

import java.util.List;

/**
 * Created by anirudh.r on 08/05/16 at 1:17 AM.
 */
public final class NetworkStat {

    private NetworkStat() {
    }

    public static double getAverageSpeed(List<RequestStats> requestStatList) {
        double size, time, totalApiTime = 0;
        for (RequestStats requestStats : requestStatList) {
            size = Double.parseDouble(requestStats.getResponseSize());
            time = (requestStats.getEndTime() - requestStats.getStartTime());
            totalApiTime += (size / time);
        }
        return totalApiTime / requestStatList.size();
    }
}