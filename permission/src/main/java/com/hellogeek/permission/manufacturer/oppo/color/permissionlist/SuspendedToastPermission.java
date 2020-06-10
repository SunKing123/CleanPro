package com.hellogeek.permission.manufacturer.oppo.color.permissionlist;

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

import static com.hellogeek.permission.util.Constant.PROVIDER_SUSPENDEDTOAST;

/**
 * 悬浮窗-oppo
 */
public class SuspendedToastPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;

    public SuspendedToastPermission(Context context) {
        super(context);
        mContext = context;
    }


    public void openSuspendedToast(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SUSPENDEDTOAST)&&!mIsOpen) {
            if (!result) {
                result=true;
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true,0));
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);

                back(service);
            }
            return;
        } else if (mIsOpen||result){
            return;
        } else {
            if (NodeInfoUtil.pageContains(nodeInfo, "按权限管理") && !getList().contains(SIGN1)) {
                boolean mIsOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "按权限管理");
                    addSIGN(SIGN1);
            } else if (NodeInfoUtil.pageContains(nodeInfo, "悬浮窗管理") && !getList().contains(SIGN2) && getList().contains(SIGN1)) {
                boolean mIsOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "悬浮窗管理");
                    addSIGN(SIGN2);
            } else if (NodeInfoUtil.pageContains(nodeInfo, "悬浮窗管理") && NodeInfoUtil.pageContains(nodeInfo, AppUtils.getAppName(mContext)) && getList().contains(SIGN2) && !getList().contains(SIGN3)) {
                mIsOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, AppUtils.getAppName(mContext));
                addSIGN(SIGN3);
                if (mIsOpen) {
                    PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);
                    EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true,1));
                    back(service);
                }
            } else {
                NodeInfoUtil.scrollableList(nodeInfo);
            }
        }
    }


    public void openSuspendedToastOther(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SUSPENDEDTOAST)&&!mIsOpen) {
            if (!result) {
                result=true;
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true,0));
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);

                back(service);
            }
            return;
        } else if (mIsOpen) {
            return;
        } else {
            if (NodeInfoUtil.pageContains(nodeInfo, "权限隐私") && !getList().contains(SIGN1)) {
                boolean mIsOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "权限隐私");
                addSIGN(SIGN1);
            } else if (NodeInfoUtil.pageContains(nodeInfo, "悬浮窗管理") && !getList().contains(SIGN2) && getList().contains(SIGN1)) {
                boolean mIsOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "悬浮窗管理");
                addSIGN(SIGN2);
            } else if (NodeInfoUtil.pageContains(nodeInfo, "悬浮窗管理") && NodeInfoUtil.pageContains(nodeInfo, AppUtils.getAppName(mContext)) && getList().contains(SIGN2) && !getList().contains(SIGN3)) {
                mIsOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, AppUtils.getAppName(mContext));
                addSIGN(SIGN3);
                if (mIsOpen) {
                    PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);
                    EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true,1));
                    back(service);
                }
            } else {
                NodeInfoUtil.scrollableList(nodeInfo);
            }
        }
    }


}
