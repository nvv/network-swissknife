package com.nsak.android.network.data;

import com.nsak.android.utils.CommandLineCommandOutputData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vlad Namashko
 */
public class PingData implements CommandLineCommandOutputData {

    private static String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
    private static Pattern PATTERN = Pattern.compile(IPADDRESS_PATTERN);


    private int mSize;
    private String mTime;
    private String mFrom;
    private int mSeq;
    private String mTtl;
    private String mIp;

    public PingData(String outputLine) {
        String[] data = outputLine.trim().split("\\s+");
        for (int i = 0; i < data.length; i++) {
            String item = data[i];
            if (item.contains("time")) {
                mTime = item.split("=")[1];
            } else if (item.contains("bytes") && i > 0) {
                mSize = Integer.parseInt(data[i - 1]);
            } else if (item.contains("from") && i < data.length) {
                mFrom = data[i + 1];
            } else if (item.contains("icmp_seq")) {
                mSeq = Integer.parseInt(item.split("=")[1]);
            } else if (item.contains("ttl")) {
                mTtl = item.split("=")[1];
            }
        }

        mIp = extractIp(outputLine);
    }

    private String extractIp(String output) {
        Matcher matcher = PATTERN.matcher(output);
        if (matcher.find()) {
            return matcher.group();
        }
        else{
            return "0.0.0.0";
        }
    }

    public int getSize() {
        return mSize;
    }

    public String getTime() {
        return mTime;
    }

    public String getFrom() {
        return mFrom;
    }

    public int getSeq() {
        return mSeq;
    }

    public String getTtl() {
        return mTtl;
    }

    public String getIp() {
        return mIp;
    }
}
