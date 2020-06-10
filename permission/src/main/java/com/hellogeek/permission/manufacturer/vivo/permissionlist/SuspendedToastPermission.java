package com.hellogeek.permission.manufacturer.vivo.permissionlist;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.Rect;
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
 * 悬浮窗-vivo
 */
public class SuspendedToastPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;

    public SuspendedToastPermission(Context context) {
        super(context);
        mContext = context;
    }

    public void openSuspendedToastY67(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (nodeInfo == null) return;
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SUSPENDEDTOAST) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true,0));
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);

                back(service);
            }
            return;
        } else if (mIsOpen || result) {
            return;
        } else {
            if (NodeInfoUtil.pageContains(nodeInfo, "软件管理") && !getList().contains(SIGN1)) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "软件管理");
                addSIGN(SIGN1);
            } else if (NodeInfoUtil.pageContains(nodeInfo, "权限管理") && getList().contains(SIGN1) && !getList().contains(SIGN2)) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "权限管理");
                addSIGN(SIGN2);
            } else if (NodeInfoUtil.pageContains(nodeInfo, "悬浮窗管理") && getList().contains(SIGN2) && !getList().contains(SIGN3)) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "悬浮窗管理");
                addSIGN(SIGN3);
            } else if (NodeInfoUtil.pageContains(nodeInfo, AppUtils.getAppName(mContext))&&NodeInfoUtil.pageContains(nodeInfo, "悬浮窗管理") && !getList().contains(SIGN4) && getList().contains(SIGN3)) {
                List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(AppUtils.getAppName(mContext));
                if (nodes != null && nodes.size() > 0) {
                    boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, AppUtils.getAppName(mContext), 1);
                    addSIGN(SIGN4);
                    if (isOpen) {
                        mIsOpen = isOpen;
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


    public void openSuspendedToastV3_2(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (nodeInfo == null) return;
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SUSPENDEDTOAST) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true,0));
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);

                back(service);
            }
            return;
        } else if (mIsOpen || result) {
            return;
        } else {
            if (NodeInfoUtil.pageContains(nodeInfo, "软件管理") && !getList().contains(SIGN1)) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "软件管理");
                if (isOpen) {
                    addSIGN(SIGN1);
                }
            } else if (NodeInfoUtil.pageContains(nodeInfo, "悬浮窗管理") && getList().contains(SIGN1) && !getList().contains(SIGN2)) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "悬浮窗管理");
                if (isOpen) {
                    addSIGN(SIGN2);
                }
            } else if (NodeInfoUtil.pageContains(nodeInfo, AppUtils.getAppName(mContext))&&NodeInfoUtil.pageContains(nodeInfo, "悬浮窗管理") && !getList().contains(SIGN3) && getList().contains(SIGN2)) {
                List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(AppUtils.getAppName(mContext));
                if (nodes != null && nodes.size() > 0) {
                    boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, AppUtils.getAppName(mContext), 1);
                    addSIGN(SIGN3);
                    if (isOpen) {
                        mIsOpen = isOpen;
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


    public void openSuspendedToastVOther(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (nodeInfo == null) return;
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SUSPENDEDTOAST) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true,0));
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);

                back(service);
            }
            return;
        } else if (result || mIsOpen) {
            return;
        } else {
            if (NodeInfoUtil.pageContains(nodeInfo, "悬浮窗") && NodeInfoUtil.pageContains(nodeInfo, AppUtils.getAppName(mContext)) && !getList().contains(SIGN1)) {
                List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText("悬浮窗");
                if (nodes != null && nodes.size() > 0) {
                    boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "悬浮窗");
                    addSIGN(SIGN1);
                    if (isOpen) {
                        mIsOpen = isOpen;
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

   /**比较奇葩的手机，代码点击会出现二次允许框，点击允许 悬浮窗权限还是不开启，只能走手动引导点击
    * v4_4
    * y66
    * 系统:6.0.1	OS:3	I管家：5.2.2.6 待升级i管家后测
    * */
    public void openSuspendedToastV4_4Y66(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (nodeInfo == null) return;
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SUSPENDEDTOAST) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true,0));
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);

                back(service);
            }
            return;
        } else if (result || mIsOpen) {
            return;
        } else {
            if (NodeInfoUtil.pageContains(nodeInfo, "悬浮窗") && NodeInfoUtil.pageContains(nodeInfo, AppUtils.getAppName(mContext)) && !getList().contains(SIGN1)) {
                List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText("悬浮窗");
                if (nodes != null && nodes.size() > 0) {
                    addSIGN(SIGN1);
                    Rect rect = new Rect(0, 0, 0, 0);
                    nodes.get(0).getParent().getBoundsInScreen(rect);
                    floaWindowToast(mContext, rect);
                } else {
                    NodeInfoUtil.scrollableList(nodeInfo);
                }
            }
        }
    }
}
