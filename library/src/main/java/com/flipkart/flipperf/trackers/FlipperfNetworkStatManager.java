package com.flipkart.flipperf.trackers;


import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.flipkart.fk_android_batchnetworking.Connectivity;
import com.flipkart.flipperf.models.NetworkStat;
import com.flipkart.flipperf.models.NetworkType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikhil.n on 15/01/15.
 */
public class FlipperfNetworkStatManager {

    private Map<NetworkType, NetworkStat> networkStatMap;
    private final double MIN_RESPONSE_SIZE = 3000;
    private NetworkType currentNetworkType;
    private Context context;
    private static final String TAG = FlipperfNetworkStatManager.class.getSimpleName();

    // Receiver to monitor connectivity change
    private ConnectivityChangeReceiver receiver;

    private static FlipperfNetworkStatManager flipperfNetworkStatManager;

    private FlipperfNetworkStatManager(Context context) {
        networkStatMap = new HashMap<NetworkType, NetworkStat>();

        // registering connectivity change receiver.
        this.context  = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        receiver = new ConnectivityChangeReceiver();
        context.registerReceiver(receiver, filter);
    }

    public NetworkType getCurrentNetworkType(){
        if(currentNetworkType == null){
            currentNetworkType =getNetworkType(context);
        }
        return currentNetworkType;
    }

    public void setCurrentNetworkType(NetworkType networkType){
        currentNetworkType = networkType;
    }


    public static synchronized FlipperfNetworkStatManager getInstance(Context context){
        if(flipperfNetworkStatManager == null)
            flipperfNetworkStatManager = new FlipperfNetworkStatManager(context);
        return flipperfNetworkStatManager;
    }

    public NetworkStat getNetworkStatData(NetworkType networkType){
        return networkStatMap.get(networkType);
    }

    public NetworkStat getNetworkStatData(Context context) {
        return networkStatMap.get(getNetworkType(context));
    }

    public void logAPiCallEvent(APIEvent apiEvent) {
        if(apiEvent.getResponseSize() >= MIN_RESPONSE_SIZE) {
            NetworkStat networkStat = null;
            if (networkStatMap.containsKey(apiEvent.getNetworkType())) {
                networkStat = networkStatMap.get(apiEvent.getNetworkType());
            } else {
                networkStat = new NetworkStat();
                networkStatMap.put(apiEvent.getNetworkType(), networkStat);
            }
            networkStat.addAPiEvent(apiEvent);
        }
    }

    private NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static NetworkType getNetworkType(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        if(info == null || !info.isConnected()) {
            return NetworkType.UNKNOWN;
        } else {
            switch(info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return NetworkType.WIFI;
                case ConnectivityManager.TYPE_MOBILE:
                    switch(info.getSubtype()) {
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

    public static boolean isConnected(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    public static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
            /*
			 * Above API level 7, make sure to set android:targetSdkVersion
			 * to appropriate level to use these
			 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        /*
         * Incase the receiver was attached, unregister the same.
         */
        if(context != null && receiver != null){
            context.unregisterReceiver(receiver);
        }
        super.finalize();
    }

    /**
     * Destroy any references held.
     */
    public void destroy(){
        if(context != null && receiver != null){
            context.unregisterReceiver(receiver);
        }
    }
}
