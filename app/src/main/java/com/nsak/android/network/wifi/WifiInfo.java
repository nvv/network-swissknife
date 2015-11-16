package com.nsak.android.network.wifi;

import android.net.DhcpInfo;
import android.support.annotation.NonNull;

import com.nsak.android.network.utils.NetworkCalculator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
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
    private String mMacAddress;
    private int mSpeed;
    private int mFrequency;

    public WifiInfo(DhcpInfo dhcpInfo, android.net.wifi.WifiInfo connectionInfo) throws IOException {

        mIpAddress = dhcpInfo.ipAddress;
        mIpAddressString = NetworkCalculator.ipIntToStringRevert(mIpAddress);

        mMaskCidr = 24;

        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(
                InetAddress.getByAddress(NetworkCalculator.ipIntToBytesReverted(dhcpInfo.ipAddress)));
        List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
        for (InterfaceAddress address : interfaceAddresses) {
            if (address.getAddress().getHostAddress().equals(mIpAddressString)) {
                mMaskCidr = address.getNetworkPrefixLength();
                break;
            }
        }

        mMacAddress = extractMacAddress(networkInterface).toString();

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

    @NonNull
    protected StringBuilder extractMacAddress(NetworkInterface networkInterface) throws SocketException {
        final StringBuilder buf = new StringBuilder();
        for (int idx = 0; idx < networkInterface.getHardwareAddress().length; idx++) {
            buf.append(String.format("%02X:", networkInterface.getHardwareAddress()[idx]));
        }
        if (buf.length() >= 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf;
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

    public String getBssid() {
        return mBssid;
    }

    public String getDns() {
        return mDns;
    }

    public String getGatewayString() {
        return mGatewayString;
    }

    public String getMacAddress() {
        return mMacAddress;
    }
}
