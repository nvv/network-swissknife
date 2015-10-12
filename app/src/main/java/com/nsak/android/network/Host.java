package com.nsak.android.network;

import com.nsak.android.network.utils.HardwareUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Vlad Namashko.
 */
public class Host {

    public static final int TYPE_GATEWAY = 0;
    public static final int TYPE_COMPUTER = 1;

    public int deviceType = TYPE_COMPUTER;
    public String ipAddress = null;
    public int ipAddressInt = 0;
    public String hostname = null;
    public String macAddress = HardwareUtils.NOMAC;
    public String nicVendor = "Unknown";
    public String os = "Unknown";
    public boolean isReacheble;

    public HashMap<Integer, String> services = null;
    public HashMap<Integer, String> banners = null;
    public ArrayList<Integer> portsOpen = null;
    public ArrayList<Integer> portsClosed = null;
}
