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
        for (int i = 0; i < data.length; i++) {
            String item = data[i];
            if (item.contains("time")) {
                mTime = item.split("=")[1];
            } else if (item.contains("bytes") && i > 0) {
                mSize = Integer.parseInt(data[i - 1]);
            }
        }
    }

    public int getSize() {
        return mSize;
    }

    public String getTime() {
        return mTime;
    }
}
