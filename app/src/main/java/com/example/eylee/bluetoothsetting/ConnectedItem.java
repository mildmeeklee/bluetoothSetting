package com.example.eylee.bluetoothsetting;

import android.graphics.drawable.Drawable;

public class ConnectedItem {
    private Drawable mIcon;
    private String deviceNm;
    private String deviceAddr;

    public ConnectedItem(Drawable mIcon, String deviceNm, String deviceAddr){
        this.mIcon = mIcon;
        this.deviceNm = deviceNm;
        this.deviceAddr = deviceAddr;
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
