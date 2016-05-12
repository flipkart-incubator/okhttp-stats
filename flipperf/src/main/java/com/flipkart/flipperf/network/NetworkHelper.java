package com.flipkart.flipperf.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/*
 *Helper class for all network information
 */
public final class NetworkHelper {

    public static String getNetworkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return NetworkType.UNKNOWN;
        } else {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return NetworkType.WIFI;
                case ConnectivityManager.TYPE_MOBILE:
                    switch (info.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                            return NetworkType.RTT; // ~ 50-100 kbps
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                            return NetworkType.CDMA; // ~ 14-64 kbps
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                            return NetworkType.EDGE; // ~ 50-100 kbps
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            return NetworkType.EVDO; // ~ 400-1000 kbps
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            return NetworkType.EVDO; // ~ 600-1400 kbps
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                            return NetworkType.GPRS; // ~ 100 kbps
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                            return NetworkType.HSDPA; // ~ 2-14 Mbps
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                            return NetworkType.HSPA; // ~ 700-1700 kbps
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                            return NetworkType.HSUPA; // ~ 1-23 Mbps
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                            return NetworkType.UMTS; // ~ 400-7000 kbps
                        case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                            return NetworkType.HIGHSPEED; // ~ 1-2 Mbps
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                            return NetworkType.HIGHSPEED; // ~ 5 Mbps
                        case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                            return NetworkType.HIGHSPEED; // ~ 10-20 Mbps
                        case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                            return NetworkType.HIGHSPEED; // ~25 kbps
                        case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                            return NetworkType.HIGHSPEED; // ~ 10+ Mbps
                        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                            return NetworkType.UNKNOWN;
                        default:
                            return NetworkType.UNKNOWN;
                    }
                default:
                    return NetworkType.UNKNOWN;
            }
        }
    }
    
    public static String getMobileOperatorName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperatorName();
    }

    public static String getWifiSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        return info.getSSID();
    }
}
