package com.nsak.android.event;

import android.view.View;

import com.nsak.android.network.Host;

/**
 * @author Vlad Namashko.
 */
public class HostSelectedEvent {

    public Host host;
    public View sharedView;

    public HostSelectedEvent(Host host, View sharedView) {
        this.host = host;
        this.sharedView = sharedView;
    }
}
