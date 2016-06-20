package com.nsak.android.network.data;

import com.nsak.android.network.utils.NetworkCalculator;
import com.nsak.android.utils.CommandLineCommandOutputData;

import java.net.InetAddress;

/**
 * @author Vlad Namashko
 */
public class TracerouteData implements CommandLineCommandOutputData {

    private int mLevel;
    private String mIpAddress;
    private String mHost;

    public TracerouteData(String outputLine) {
        String[] data = outputLine.trim().split("\\s+");

        try {
            mLevel = Integer.parseInt(data[0]);
        } catch (Exception e) {
            mLevel = -1;
            return;
        }
        mIpAddress = data[1];
        try {
            mHost = InetAddress.getByAddress(NetworkCalculator.ipStringToBytes(mIpAddress)).getHostName();
        } catch (Exception e) {
            mHost = mIpAddress;
        }
    }

    public int getLevel() {
        return mLevel;
    }

    public String getIpAddress() {
        return mIpAddress;
    }

    public String getHost() {
        return mHost;
    }
}
