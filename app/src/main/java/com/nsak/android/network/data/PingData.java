package com.nsak.android.network.data;

import com.nsak.android.utils.CommandLineCommandOutputData;

/**
 * @author Vlad Namashko
 */
public class PingData implements CommandLineCommandOutputData {

    private int mSize;
    private String mTime;

    public PingData(String outputLine) {
        String[] data = outputLine.trim().split("\\s+");
        mSize = Integer.parseInt(data[0]);
        mTime = data[6].split("=")[1];
    }

    public int getSize() {
        return mSize;
    }

    public String getTime() {
        return mTime;
    }
}
