package com.hellogeek.permission.server.interfaces;

import android.view.accessibility.AccessibilityEvent;

public interface IAccessibilityServiceMonitor {
    void onEvent(AccessibilityEvent event);
}
