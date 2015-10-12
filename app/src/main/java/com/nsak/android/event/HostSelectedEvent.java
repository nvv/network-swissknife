package com.nsak.android.event;

import com.nsak.android.network.Host;

/**
 * @author Vlad Namashko.
 */
public class HostSelectedEvent {

    public Host host;

    public HostSelectedEvent(Host host) {
        this.host = host;
    }
}
