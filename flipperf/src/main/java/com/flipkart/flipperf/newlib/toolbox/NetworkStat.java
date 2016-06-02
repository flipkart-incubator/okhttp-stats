package com.flipkart.flipperf.newlib.toolbox;

import com.flipkart.flipperf.newlib.model.RequestStats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by anirudh.r on 08/05/16 at 1:17 AM.
 */
public final class NetworkStat {

    private static Logger mLogger = LoggerFactory.getLogger(NetworkStat.class.getName());

    private NetworkStat() {
    }

    public static double getAverageSpeed(List<RequestStats> requestStatList) {
        double size, time, totalApiTime = 0;
        for (RequestStats requestStats : requestStatList) {
            size = Double.parseDouble(requestStats.getResponseSize());
            time = (requestStats.getEndTime() - requestStats.getStartTime());
            try {
                totalApiTime += (size / time);
            } catch (ArithmeticException e) {
                if (mLogger.isDebugEnabled()) {
                    mLogger.debug(e.getMessage());
                }
            }
        }
        return totalApiTime / requestStatList.size();
    }
}