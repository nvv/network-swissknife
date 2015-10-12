package com.nsak.android.event;

/**
 * @author Vlad Namashko.
 */
public class NetworkInfoDiscoveredEvent {

    public int hostsCount;

    public NetworkInfoDiscoveredEvent(int hostsCount) {
        this.hostsCount = hostsCount;
    }
}
