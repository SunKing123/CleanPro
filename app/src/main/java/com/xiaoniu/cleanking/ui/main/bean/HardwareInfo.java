package com.xiaoniu.cleanking.ui.main.bean;

import java.io.Serializable;

public class HardwareInfo implements Serializable {

    private int size;

    private String batteryLevel;

    private boolean isCharge;

    private boolean isBluetoothOpen;

    private boolean isGPSOpen;

    private boolean isWiFiOpen;

    /**
     * CPU型号
     */
    private String CPUType;

    /**
     * Cpu核心数
     */
    private String CPUCore;

    /**
     * CPU负载
     */
    private String CPULoad;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public boolean isCharge() {
        return isCharge;
    }

    public void setCharge(boolean charge) {
        isCharge = charge;
    }

    public boolean isBluetoothOpen() {
        return isBluetoothOpen;
    }

    public void setBluetoothOpen(boolean bluetoothOpen) {
        isBluetoothOpen = bluetoothOpen;
    }

    public boolean isGPSOpen() {
        return isGPSOpen;
    }

    public void setGPSOpen(boolean GPSOpen) {
        isGPSOpen = GPSOpen;
    }

    public boolean isWiFiOpen() {
        return isWiFiOpen;
    }

    public void setWiFiOpen(boolean wiFiOpen) {
        isWiFiOpen = wiFiOpen;
    }

    public String getCPUType() {
        return CPUType;
    }

    public void setCPUType(String CPUType) {
        this.CPUType = CPUType;
    }

    public String getCPUCore() {
        return CPUCore;
    }

    public void setCPUCore(String CPUCore) {
        this.CPUCore = CPUCore;
    }

    public String getCPULoad() {
        return CPULoad;
    }

    public void setCPULoad(String CPULoad) {
        this.CPULoad = CPULoad;
    }
}
