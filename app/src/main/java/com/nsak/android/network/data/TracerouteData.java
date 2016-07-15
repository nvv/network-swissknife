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
    private String mTime;

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

        mTime = "";
        try {
            for (int i = 0; i < data.length; i++) {
                if (data[i].equals("ms")) {
                    mTime = data[i - 1];
                    break;
                }
            }
        } catch (Exception e) {
            mTime = "";
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

    public String getTime() {
        return mTime;
    }
}
