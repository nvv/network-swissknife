package com.nsak.android.core;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nsak.android.App;

/**
 * @author Vlad Namashko
 */

public class Settings {

    public static final String LAST_SELECTED_HOST = "last_selected_host";
    public static final String IP_CALCULATOR_IP = "ip_calculator_ip";
    public static final String IP_CALCULATOR_MASK = "ip_calculator_mask";

    public SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(App.sInstance);
    }

    public void setLastSelectedHost(String host) {
        getSharedPreferences().edit().putString(LAST_SELECTED_HOST, host).apply();
    }

    public String getLastSelectedHost() {
        return getSharedPreferences().getString(LAST_SELECTED_HOST, "");
    }

    public void setIpCalculatorIp(String ip) {
        getSharedPreferences().edit().putString(IP_CALCULATOR_IP, ip).apply();
    }

    public String getIpCalculatorIp() {
        return getSharedPreferences().getString(IP_CALCULATOR_IP, "");
    }

    public void setIpCalculatorMask(String mask) {
        getSharedPreferences().edit().putString(IP_CALCULATOR_MASK, mask).apply();
    }

    public String getIpCalculatorMask() {
        return getSharedPreferences().getString(IP_CALCULATOR_MASK, "");
    }
}
