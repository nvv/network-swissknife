package com.easymobile.lan.scanner.main;

public class HostScan {
    static {
        System.loadLibrary("host_scan");
    }

    public native String gethostname(String paramString);
}