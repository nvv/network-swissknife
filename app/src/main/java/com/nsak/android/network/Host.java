package com.nsak.android.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.nsak.android.network.utils.HardwareUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Vlad Namashko.
 */
public class Host implements Parcelable {

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
/*
    public HashMap<Integer, String> services = null;
    public HashMap<Integer, String> banners = null;
    public ArrayList<Integer> portsOpen = null;
    public ArrayList<Integer> portsClosed = null;
*/
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(deviceType);
        dest.writeString(ipAddress);
        dest.writeInt(ipAddressInt);
        dest.writeString(hostname);
        dest.writeString(macAddress);
        dest.writeString(nicVendor);
        dest.writeString(os);
        dest.writeInt(isReacheble ? 1 : 0);
    }

    public static final Parcelable.Creator<Host> CREATOR = new Parcelable.Creator<Host>() {

        public Host createFromParcel(Parcel in) {
            Host host = new Host();
            host.deviceType = in.readInt();
            host.ipAddress = in.readString();
            host.ipAddressInt = in.readInt();
            host.hostname = in.readString();
            host.macAddress = in.readString();
            host.nicVendor = in.readString();
            host.os = in.readString();
            host.isReacheble = in.readInt() == 1;

            return host;
        }

        public Host[] newArray(int size) {
            return new Host[size];
        }
    };
}
