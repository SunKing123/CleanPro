package com.hellogeek.permission.manufacturer.huawei.permissionlist;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hellogeek.permission.manufacturer.ManfacturerBase;

public class NotifiCationBarPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;

    public NotifiCationBarPermission(Context context) {
        super(context);
        this.mContext = context;
    }

    public void openNotifiCationBar(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {

    }

}
