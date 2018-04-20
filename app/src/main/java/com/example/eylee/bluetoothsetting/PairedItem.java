package com.example.eylee.bluetoothsetting;

import android.graphics.drawable.Drawable;

public class PairedItem {
    private Drawable mIcon;
    private String deviceNm;
    private String deviceAddr;
    private boolean isConnected;
    private boolean isDiscovery;

    PairedItem(Drawable mIcon, String deviceNm, String deviceAddr, boolean isConnected, boolean isDiscovery){
        this.mIcon = mIcon;
        this.deviceNm = deviceNm;
        this.deviceAddr = deviceAddr;
        this.isConnected = isConnected;
        this.isDiscovery = isDiscovery;
    }

    public boolean isDiscovery() {
        return isDiscovery;
    }

    public void setDiscovery(boolean discovery) {
        isDiscovery = discovery;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public Drawable getmIcon() {
        return mIcon;
    }

    public void setmIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }

    public String getDeviceNm() {
        return deviceNm;
    }

    public void setDeviceNm(String deviceNm) {
        this.deviceNm = deviceNm;
    }

    public String getDeviceAddr() {
        return deviceAddr;
    }

    public void setDeviceAddr(String deviceAddr) {
        this.deviceAddr = deviceAddr;
    }
}
