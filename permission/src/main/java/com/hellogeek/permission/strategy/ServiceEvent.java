package com.hellogeek.permission.strategy;

import com.hellogeek.permission.server.AccessibilityServiceMonitor;

public class ServiceEvent {
    protected AccessibilityServiceMonitor serviceMonitor ;

    public ServiceEvent(AccessibilityServiceMonitor serviceMonitor ) {
        this.serviceMonitor = serviceMonitor;
    }
    public AccessibilityServiceMonitor getAccessibilityServiceMonitor(){
        return serviceMonitor;
    }

}
