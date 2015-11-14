package com.nsak.android.network;

import com.nsak.android.App;
import com.nsak.android.core.ThreadPoolRunnable;
import com.nsak.android.event.NetworkInfoDiscoveredEvent;
import com.nsak.android.fragments.NetworkScanFragment;
import com.nsak.android.db.VendorDbAdapter;
import com.nsak.android.network.exceptions.NetworkScanException;
import com.nsak.android.network.utils.HardwareUtils;
import com.nsak.android.network.utils.NetworkCalculator;
import com.nsak.android.network.utils.NetworkUtils;
import com.nsak.android.network.wifi.WifiInfo;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import jcifs.netbios.NbtAddress;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * @author Vlad Namashko.
 */
public class NetworkScanner {

    private String TAG = "Network Scanner";
    private final Map<String, Host> mReachableHosts = new ConcurrentHashMap<>();
    private boolean mIsScanning;
    private final static int[] DPORTS = {22, 135, 139, 445, 80};
    private WifiInfo mWifiInfo;

    private void stopScanning() {
        mIsScanning = false;
    }

    public void destroy() {
        stopScanning();
        App.sInstance.getThreadPool().stopNetworkScanTasks();
    }

    public Observable<Host> scanNetwork(final WifiInfo wifiInfo, final Handler handler) {
        mWifiInfo = wifiInfo;
        return Observable.create(new Observable.OnSubscribe<Host>() {
            private HashMap<String, String> mAddresses = new HashMap<>();
            private int mLastAvailableIpsCount;
            private String mGatewayIp;

            @Override
            public void call(final Subscriber<? super Host> subscriber) {
                mReachableHosts.clear();
                mIsScanning = true;

                android.net.wifi.WifiInfo info = App.sInstance.getWifiManager().getConnectionInfo();
                mAddresses.put(NetworkCalculator.ipIntToStringRevert(info.getIpAddress()), info.getMacAddress());
                mGatewayIp = NetworkCalculator.ipIntToStringRevert(App.sInstance.getWifiManager().getDhcpInfo().gateway);

                String stringIp = wifiInfo.getIpAddressString();
                String stringMask = wifiInfo.getMaskString();
                String[] ips;
                try {
                    ips = NetworkCalculator.allStringAddressesInSubnet(stringIp, stringMask);
                    handler.sendMessage(Message.obtain(null, NetworkScanFragment.MSG_NETWORK_DISCOVERED,
                            new NetworkInfoDiscoveredEvent(ips.length)));
                } catch (Exception e) {
                    subscriber.onError(e);
                    return;
                }

                if (ips.length == 0) {
                    subscriber.onError(new NetworkScanException());
                    return;
                }

                Log.d(TAG, "::::::::::::::::::::::::: ");
                Log.d(TAG, " Start network scan : " + stringIp + "/" + stringMask);
                Log.d(TAG, "::::::::::::::::::::::::: ");

                final AtomicInteger latch = new AtomicInteger(ips.length);
                for (final String ip : ips) {
                    App.sInstance.getThreadPool().executeNetworkTask(new ScanHostTask(ip) {
                        @Override
                        void onHostScanned(Host host) {
                            if (mReachableHosts.size() > 0 && mLastAvailableIpsCount != mReachableHosts.size()) {
                                updateMacAddresses();
                                mLastAvailableIpsCount = mReachableHosts.size();
                            }
                            synchronized (subscriber) {
                                subscriber.onNext(host);
                            }
                            if (latch.decrementAndGet() == 0) {
                                subscriber.onCompleted();
                            }
                        }
                    });
                }
            }

            private synchronized void updateMacAddresses() {

                HardwareUtils.getHardwareAddresses(mAddresses);

                Observable.from(mAddresses.keySet()).filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String ip) {
                        String mac = mAddresses.get(ip);
                        Host host = mReachableHosts.get(ip);
                        return host != null && !mac.equals(host.macAddress);
                    }
                }).map(new Func1<String, Host>() {
                    @Override
                    public Host call(String ip) {
                        return mReachableHosts.get(ip);
                    }
                }).subscribe(new Action1<Host>() {
                    @Override
                    public void call(Host host) {
                        String mac = mAddresses.get(host.ipAddress);
                        host.macAddress = mac;
                        host.deviceType = mGatewayIp.equals(host.ipAddress) ? 0 : 1;
                        host.nicVendor = VendorDbAdapter.getVendor(Integer.parseInt(mac.substring(0, 8).replace(":", ""), 16));
                        Log.d(TAG, ":::: Discovered : " + host.ipAddress + " : " +
                                host.macAddress + "( " + host.nicVendor + ") " + host.deviceType + " / " + host.hostname);
                    }
                });
            }
        });
    }

    public abstract class ScanHostTask implements ThreadPoolRunnable {

        private static final String TAG = "ScanHostTask";

        private String mIp;
        private InetAddress mInetAddress;

        public ScanHostTask(String ip) {
            mIp = ip;
        }

        @Override
        public void run() {

            Host host = mWifiInfo.getIpAddressString().equals(mIp) ? new Gateway(mWifiInfo) : new Host();
            host.ipAddress = mIp;

            boolean isReachable = false;
            try {

                //Log.d(TAG, "Trying " + mIp);

                mInetAddress = InetAddress.getByName(mIp);

                if (mIsScanning && (mInetAddress.isReachable(25) || NetworkUtils.ping(mIp))) {
                    addHost(host);
                    isReachable = true;
                } else if (mIsScanning) {
                    Socket s = new Socket();
                    for (int port : DPORTS) {
                        try {
                            s.bind(null);
                            s.connect(new InetSocketAddress(mIp, port), 25);
                            addHost(host);
                            isReachable = true;
                            break;
                        } catch (Exception ignored) {
                        } finally {
                            try {
                                s.close();
                            } catch (Exception ignored) {
                            }
                        }
                    }

                    // last resort
                    if (mIsScanning && !isReachable) {
                        String mac = HardwareUtils.getHardwareAddress(mIp);
                        if (!HardwareUtils.NOMAC.equals(mac)) {
                            addHost(host);
                            isReachable = true;
                        }
                    }
                }
            } catch (Exception ignore) {
                //e.printStackTrace();
            }

            Log.d(TAG, "Done " + mIp + " : " + isReachable);

            if (mIsScanning) {
                if (isReachable) {
                    host.discoveredTime = System.currentTimeMillis();
                }
                onHostScanned(host);
            }
        }

        private void addHost(Host host) {
            host.ipAddressInt = NetworkCalculator.ipStringToInt(mIp);
            host.isReachable = true;
            try {
                String hostname = InetAddress.getByAddress(NetworkCalculator.ipIntToByteArray(host.ipAddressInt)).getHostName();
                if (!host.ipAddress.equals(hostname)) {
                    host.hostname = hostname;
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            host.netBiosName = getNetBiosName(host.ipAddress);
            mReachableHosts.put(mIp, host);

            Log.d(TAG, "Discovered host : " + mIp);
        }

        private String getNetBiosName(String ip) {
            try {
                NbtAddress[] nbtAddresses = NbtAddress.getAllByAddress(ip);
                return nbtAddresses[0].getHostName();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public int getId() {
            return NetworkCalculator.ipStringToInt(mIp);
        }

        abstract void onHostScanned(Host host);
    }


}
