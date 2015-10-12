package com.nsak.android.event;

import com.nsak.android.network.Host;

/**
 * @author Vlad Namashko.
 */
public class NetworkHostScannedEvent {

    public int totalHosts;
    public int hostScanned;
    public Host host;

    public NetworkHostScannedEvent(Host host, int totalHosts, int hostScanned) {
        this.host = host;
        this.totalHosts = totalHosts;
        this.hostScanned = hostScanned;
    }

}
