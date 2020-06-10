package com.hellogeek.permission.manufacturer.other.permissionlist;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
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

/**
 * 悬浮窗-other
 */
public class SuspendedToastPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;

    public SuspendedToastPermission(Context context) {
        super(context);
        mContext = context;
    }

    public void openSuspendedToast(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SUSPENDEDTOAST)&&!mIsOpen) {
            if (!result) {
                result=true;
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true,0));
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);

                back(service);
            }
            return;
        }else if (mIsOpen||result){
            return;
        } else {
            mIsOpen = openSuspendedToast1(mContext, info, service);
        }
    }

    public boolean openSuspendedToast1(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (nodeInfo == null) return true;
        if (NodeInfoUtil.pageContains(nodeInfo, "显示悬浮窗") && !getList().contains(SIGN1)) {
            List<AccessibilityNodeInfo> nodeInfos = nodeInfo.findAccessibilityNodeInfosByText(AppUtils.getAppName(mContext));
            if (nodeInfos != null && nodeInfos.size() > 0) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext,nodeInfo, "显示悬浮窗");
                addSIGN(SIGN1);
            }
        }else  if (NodeInfoUtil.pageContains(nodeInfo, "在其他应用的上层显示") && !getList().contains(SIGN1)) {
            List<AccessibilityNodeInfo> nodeInfos = nodeInfo.findAccessibilityNodeInfosByText(AppUtils.getAppName(mContext));
            if (nodeInfos != null && nodeInfos.size() > 0) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext,nodeInfo, "在其他应用的上层显示",1);
                addSIGN(SIGN1);
        }
        }else  if (NodeInfoUtil.pageContains(nodeInfo, "允许显示在其他应用的上层") && !getList().contains(SIGN1)) {
            List<AccessibilityNodeInfo> nodeInfos = nodeInfo.findAccessibilityNodeInfosByText(AppUtils.getAppName(mContext));
            if (nodeInfos != null && nodeInfos.size() > 0) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext,nodeInfo, "允许显示在其他应用的上层",1);
                addSIGN(SIGN1);
            }
        }else if (NodeInfoUtil.pageContains(nodeInfo, "允许") && getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfo(nodeInfo, "允许");
            if (!isOpen) {
                isOpen = NodeInfoUtil.clickNodeIsFor(nodeInfo, "允许");
            }
            if (!isOpen) {
                isOpen = NodeInfoUtil.performSwitch(nodeInfo, "允许");
            }
            if (isOpen) {
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true,1));
                back(service);
            }
            return isOpen;
        } else {
            NodeInfoUtil.scrollableList(nodeInfo);
        }
        return false;
    }

}
