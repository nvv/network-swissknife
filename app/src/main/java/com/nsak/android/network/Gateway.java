package com.nsak.android.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.nsak.android.network.wifi.WifiInfo;

/**
 * @author Vlad Namashko
 */
public class Gateway extends Host {

    public String ssid = null;
    public String bssid = null;
    public String gateway = null;
    public String dns = null;

    public Gateway() {
        deviceType = TYPE_GATEWAY;
    }

    public Gateway(WifiInfo wifiInfo) {
        this();
        ssid = wifiInfo.getSsid();
        bssid = wifiInfo.getBssid();
        gateway = wifiInfo.getGatewayString();
        dns = wifiInfo.getDns();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(ssid);
        dest.writeString(bssid);
        dest.writeString(gateway);
        dest.writeString(dns);
    }

    public static final Parcelable.Creator<Gateway> CREATOR = new Parcelable.Creator<Gateway>() {

        public Gateway createFromParcel(Parcel in) {
            Gateway host = new Gateway();
            host.deviceType = in.readInt();
            host.ipAddress = in.readString();
            host.ipAddressInt = in.readInt();
            host.hostname = in.readString();
            host.netBiosName = in.readString();
            host.macAddress = in.readString();
            host.nicVendor = in.readString();
            host.os = in.readString();
            host.isCurrentDevice = in.readInt() == 1;
            host.isReachable = in.readInt() == 1;
            host.firstDiscovered = in.readLong();
            host.lastSeen = in.readLong();
            host.ssid = in.readString();
            host.bssid = in.readString();
            host.gateway = in.readString();
            host.dns = in.readString();

            return host;
        }

        public Gateway[] newArray(int size) {
            return new Gateway[size];
        }
    };
}
