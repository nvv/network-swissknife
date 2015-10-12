package com.nsak.android.network.wifi;

import android.net.DhcpInfo;

import com.nsak.android.network.utils.NetworkCalculator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.List;

/**
 * @author Vlad Namashko.
 */
public class WifiInfo {

    private int mIpAddress;
    private String mIpAddressString;

    private int mMaskCidr;
    private String mMaskString;

    private String mGatewayString;
    private String mDns;
    private String mSsid;
    private String mBssid;
    private int mSpeed;
    private int mFrequency;

    public WifiInfo(DhcpInfo dhcpInfo, android.net.wifi.WifiInfo connectionInfo) throws IOException {

        mIpAddress = dhcpInfo.ipAddress;
        mIpAddressString = NetworkCalculator.ipIntToStringRevert(mIpAddress);

        mMaskCidr = 24;

        List<InterfaceAddress> interfaceAddresses = NetworkInterface.getByInetAddress(InetAddress.getByAddress(NetworkCalculator.ipIntToBytesReverted(dhcpInfo.ipAddress))).getInterfaceAddresses();
        for (InterfaceAddress address : interfaceAddresses) {
            if (address.getAddress().getHostAddress().equals(mIpAddressString)) {
                mMaskCidr = address.getNetworkPrefixLength();
            }
        }

        mMaskString = NetworkCalculator.ipBytesToString(NetworkCalculator.cidrToQuad(mMaskCidr));

        mSsid = connectionInfo.getSSID().trim();
        if (mSsid.startsWith("") && mSsid.endsWith("")) {
            mSsid = mSsid.substring(1, mSsid.length() - 1);
        }

        mGatewayString = NetworkCalculator.ipIntToStringRevert(dhcpInfo.gateway);
        mDns = NetworkCalculator.ipIntToStringRevert(dhcpInfo.dns1);
        mBssid = connectionInfo.getBSSID();
        mSpeed = connectionInfo.getLinkSpeed();

        //mFrequency = connectionInfo.getFrequency();
    }

    public int getIpAddress() {
        return mIpAddress;
    }

    public String getIpAddressString() {
        return mIpAddressString;
    }

    public int getMaskCidr() {
        return mMaskCidr;
    }

    public String getMaskString() {
        return mMaskString;
    }

    public String getSsid() {
        return mSsid;
    }
}
