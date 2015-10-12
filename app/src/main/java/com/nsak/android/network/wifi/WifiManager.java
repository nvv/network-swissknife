package com.nsak.android.network.wifi;

import android.content.Context;
import android.net.DhcpInfo;

import com.nsak.android.App;
import com.nsak.android.network.NetworkConnectionManager;

/**
 * @author Vlad Namashko.
 */
public class WifiManager {

    private android.net.wifi.WifiManager mWifiManager;

    public WifiManager() {
        initWifiManager();
    }


    /**
     * Init wifi manager instance with system service.
     *
     * Extract into separate method.
     *
     * Should be invoked periodically due to potential bug when instance is nulled unexpectedly.
     */
    public void initWifiManager() {
        if (mWifiManager == null) {
            mWifiManager = (android.net.wifi.WifiManager) App.sInstance.getSystemService(Context.WIFI_SERVICE);
        }
    }

    public DhcpInfo getDhcpInfo() {
        return mWifiManager.getDhcpInfo();
    }

    public android.net.wifi.WifiInfo getConnectionInfo() {
        return mWifiManager.getConnectionInfo();
    }

    public boolean isWifiEnabled() {
        return NetworkConnectionManager.isWifiAvailable();
    }

}
