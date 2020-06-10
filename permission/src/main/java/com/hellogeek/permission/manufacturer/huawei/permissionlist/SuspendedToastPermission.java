package com.hellogeek.permission.manufacturer.huawei.permissionlist;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.ManfacturerBase;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.strategy.PathEvent;
import com.hellogeek.permission.util.AccessibilitUtil;
import com.hellogeek.permission.util.AppUtils;
import com.hellogeek.permission.util.NodeInfoUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.hellogeek.permission.util.Constant.PROVIDER_SUSPENDEDTOAST;

public class SuspendedToastPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;

    public SuspendedToastPermission(Context context) {
        super(context);
        mContext = context;
    }

    public void openSuspendedToast23(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext.getApplicationContext(), Permission.SUSPENDEDTOAST) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true, 0));
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);
                back(service);
            }
            return;
        } else if (mIsOpen) {
            return;
        } else if (NodeInfoUtil.pageContains(info, "在其他应用的上层显示")) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "在其他应用上层显示", 1);
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true, 1));
                back(service);
            }
        }
    }

    public void openSuspendedToast26(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SUSPENDEDTOAST) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true, 0));
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);

                back(service);
            }
            return;
        } else if (mIsOpen || result) {
            return;
        } else if ((NodeInfoUtil.pageContains(info, "在其他应用的上层显示") || NodeInfoUtil.pageContains(info, "在其他应用的上层")) && !getList().contains(SIGN1)) {
            if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext))) {
                NodeInfoUtil.clickNodeInfoAll(mContext, info, AppUtils.getAppName(mContext), 1);
                addSIGN(SIGN1);
            } else if (!getList().contains(SIGN1)) {
                NodeInfoUtil.scrollableList(info);
            }

        } else if (NodeInfoUtil.pageContains(info, "在其他应用上层显示") && NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext)) && getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "在其他应用上层显示", 1);
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true, 1));
                back(service);
            }
        }
    }

    public void openSuspendedToastOther(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SUSPENDEDTOAST) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true, 0));
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);

                back(service);
            }
            return;
        } else if (mIsOpen || result) {
            return;
        } else if (TextUtils.equals("H60-L01", Build.MODEL)) {
            if (NodeInfoUtil.pageContains(info, "悬浮窗管理")) {
                List<AccessibilityNodeInfo> nodeInfos = info.findAccessibilityNodeInfosByText("悬浮窗管理");
                if (nodeInfos != null && nodeInfos.size() > 0) {
                    NodeInfoUtil.clickNodeInfoAll(mContext, info, "悬浮窗管理");
                }
                List<AccessibilityNodeInfo> packInfo = info.findAccessibilityNodeInfosByText(AppUtils.getAppName(mContext));
                if (packInfo != null && packInfo.size() > 0) {
                    mIsOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, AppUtils.getAppName(mContext), 1);
                } else {
                    NodeInfoUtil.scrollableList(info);
                }
            }
        } else {
            mIsOpen = openSuspendedToast(mContext, info);
            if (mIsOpen) {
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true, 1));
                back(service);
            }
        }
    }


    /**
     * @param mContext
     * @param nodeInfo
     */
    public boolean openSuspendedToast(Context mContext, AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) return true;
        if (NodeInfoUtil.pageContains(nodeInfo, "悬浮窗")) {
            List<AccessibilityNodeInfo> nodeInfos = nodeInfo.findAccessibilityNodeInfosByText(AppUtils.getAppName(mContext));
            if (nodeInfos != null && nodeInfos.size() > 0) {
                return NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, AppUtils.getAppName(mContext), 1);
            } else {
                NodeInfoUtil.scrollableList(nodeInfo);
            }
        }
        return false;
    }

}
