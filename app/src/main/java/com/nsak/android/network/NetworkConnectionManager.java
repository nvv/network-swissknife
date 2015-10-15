package com.nsak.android.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.nsak.android.App;
import com.nsak.android.event.NetworkStateChangedEvent;

/**
 * Monitors connection and notifies listeners
 */
public class NetworkConnectionManager {

    private static BroadcastReceiver sSystemBroadCastReciever;

    public static void init() {
        sSystemBroadCastReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo.State state = getState();
//                if (state != NetworkInfo.State.CONNECTING && state != NetworkInfo.State.DISCONNECTING) {
//
//                }

                NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                String typeName = info.getTypeName();
                String subtypeName = info.getSubtypeName();
                boolean available = info.isAvailable();

                System.out.println("::::::::::::::::::   <<<<<<<<<  onReceive ");
                System.out.println("Network Type: " + typeName
                        + ", subtype: " + subtypeName
                        + ", available: " + available);

                // TODO:
                //EventBus.getDefault().post(new NetworkStateChangedEvent());
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        App.sInstance.registerReceiver(sSystemBroadCastReciever, filter);
    }

    public static boolean isWifiAvailable() {
        ConnectivityManager mgr = (ConnectivityManager) App.sInstance.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mgr == null) {
            return false;
        }
        try {
            NetworkInfo wifi = mgr.getActiveNetworkInfo();
            return wifi != null && wifi.getType() == ConnectivityManager.TYPE_WIFI && wifi.isConnected();
        } catch (SecurityException ignored) {
            return false;
        }
    }

    public static void shutdown() {
        App.sInstance.unregisterReceiver(sSystemBroadCastReciever);
    }

    public static boolean isNetworkAvailable() {
        return NetworkInfo.State.CONNECTED == getState();
    }

    public static NetworkInfo.State getState() {
        ConnectivityManager mgr = (ConnectivityManager) App.sInstance.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mgr != null) {
            try {
                NetworkInfo info = mgr.getActiveNetworkInfo();
                if (info != null) {
                    return info.getState();
                }
            } catch (SecurityException ignored) {
            }
        }
        return null;
    }

    public static boolean isEnoughRights() {
        ConnectivityManager mgr = (ConnectivityManager) App.sInstance.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (mgr == null) {
            return false;
        }
        try {
            NetworkInfo[] info = mgr.getAllNetworkInfo();
            return info != null;
        } catch (SecurityException ignored) {
            return false;
        }
    }

    public static String getNetworkId() {
        if (isWifiAvailable()) {
            final WifiManager wifiManager = (WifiManager) App.sInstance.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                    return connectionInfo.getSSID();
                }
            }
        }
        return null;
    }

}
