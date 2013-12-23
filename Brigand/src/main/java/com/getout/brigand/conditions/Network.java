package com.getout.brigand.conditions;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by steven on 12/23/13.
 */
public class Network {

    /**
     * @return 0-1000 the amount of power consumed to send some data at the moment
     */
    public static int dataCost(Context context) {
        int type = Network.networkType(context);
        if (type == 0) {
            return 10000;
        } else if (type < 10) {
            return 10;
        } else if (type < 20) {
            return 100;
        } else if (type < 50) {
            return 200;
        } else if (type < 100) {
            return mobileDataCost(context);
        } else {
            return 1000;
        }
    }

    /**
     * @return 0-1000 the amount of power necessary to send data on the mobile network
     */
    public static int mobileDataCost(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int dataState = tm.getDataState();
        switch (dataState) {
            case TelephonyManager.DATA_SUSPENDED:
            case TelephonyManager.DATA_DISCONNECTED:
                return 2000;
            case TelephonyManager.DATA_CONNECTING:
                return 500;
            case TelephonyManager.DATA_CONNECTED:
                return 400;
        }
        return 1000;
    }

    /**
     * This snippet sets the dataNetwork to either:
     *
     * 0 - no network connectivity (only if we are certain there is no connectivity)
     * 1 - Ethernet
     * 11 - WiFi (802.11x)
     * 12 - WiMAX
     * 21 - Bluetooth
     * 50 - Unknown mobile network type
     * 51 - GPRS
     * 52 - EDGE
     * 53 - HSPA+
     * 54 - LTE
     *
     * 100 - unknown network type
     */
    public static int networkType(Context context) {
        int networkType = 0;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_ETHERNET:
                    networkType = 1;
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    networkType = 11;
                    break;
                case ConnectivityManager.TYPE_WIMAX:
                    networkType = 12;
                    break;
                case ConnectivityManager.TYPE_BLUETOOTH:
                    networkType = 21;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    switch (tm.getNetworkType()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                            networkType = 51;
                            break;
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                            networkType = 52;
                            break;
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            networkType = 53;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            networkType = 54;
                            break;
                        default:
                            networkType = 50;
                    }
                    break;
                default:
                    networkType = 100;
            }
        }
        return networkType;
    }
}
