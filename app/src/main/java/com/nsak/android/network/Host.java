package com.nsak.android.network;

import android.os.Parcel;
import android.os.Parcelable;

import com.nsak.android.App;
import com.nsak.android.R;
import com.nsak.android.network.utils.HardwareUtils;
import com.nsak.android.utils.TextUtils;

/**
 * @author Vlad Namashko.
 */
public class Host implements Parcelable {

    public static final int TYPE_GATEWAY = 0;
    public static final int TYPE_DEVICE = 1;

    public int deviceType = TYPE_DEVICE;
    public String ipAddress = null;
    public int ipAddressInt = 0;
    public String hostname = null;
    public String netBiosName = null;
    public String macAddress = HardwareUtils.NOMAC;
    public String nicVendor = "Unknown";
    public String os = "Unknown";
    public boolean isCurrentDevice;
    public boolean isReachable;

    public long discoveredTime;
    public long firstDiscovered;
    public long lastSeen;
/*
    public HashMap<Integer, String> services = null;
    public HashMap<Integer, String> banners = null;
    public ArrayList<Integer> portsOpen = null;
    public ArrayList<Integer> portsClosed = null;
*/

    public String getName() {
        String name = TextUtils.isNullOrEmpty(hostname) || ipAddress.equals(hostname) ? netBiosName : hostname;
        return isCurrentDevice && TextUtils.isNullOrEmpty(name) ? App.sInstance.getString(R.string.my_device) : name;
    }

    public void updateState(Host host) {
        deviceType = host.deviceType;
        hostname = host.hostname;
        netBiosName = host.netBiosName;
        isReachable = true;
        lastSeen = host.discoveredTime;
        isCurrentDevice = host.isCurrentDevice;
    }

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
        dest.writeString(netBiosName);
        dest.writeString(macAddress);
        dest.writeString(nicVendor);
        dest.writeString(os);
        dest.writeInt(isCurrentDevice ? 1 : 0);
        dest.writeInt(isReachable ? 1 : 0);
        dest.writeLong(firstDiscovered);
        dest.writeLong(lastSeen);
    }

    public static final Parcelable.Creator<Host> CREATOR = new Parcelable.Creator<Host>() {

        public Host createFromParcel(Parcel in) {
            Host host = new Host();
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

            return host;
        }

        public Host[] newArray(int size) {
            return new Host[size];
        }
    };
}
