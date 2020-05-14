package com.hellogeek.permission.manufacturer.vivo.permissionlist;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
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

import static com.hellogeek.permission.util.Constant.PROVIDER_LOCKDISPLAY;

/**
 * 锁屏显示-vivo
 */
public class LockDisPlayPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;


    public LockDisPlayPermission(Context context) {
        super(context);
        mContext = context;
    }

    public void openLockDisPlay(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (nodeInfo == null) return;
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.LOCKDISPALY) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.LOCKDISPALY, true,0));
                PermissionProvider.save(mContext, PROVIDER_LOCKDISPLAY, true);
                back(service);
            }
            return;
        } else if (mIsOpen || result) {
            return;
        } else {
            if (NodeInfoUtil.pageContains(nodeInfo, "锁屏显示") && NodeInfoUtil.pageContains(nodeInfo, AppUtils.getAppName(mContext)) && !getList().contains(SIGN1)) {
                List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText("锁屏显示");
                if (nodes != null && nodes.size() > 0) {
                    boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "锁屏显示");
                    addSIGN(SIGN1);
                    if (isOpen) {
                        PermissionProvider.save(mContext, PROVIDER_LOCKDISPLAY, true);
                        mIsOpen = isOpen;
                        EventBus.getDefault().post(new PathEvent(Permission.LOCKDISPALY, true,1));
                        back(service);
                    }
                } else {
                    NodeInfoUtil.scrollableList(nodeInfo);
                }
            }
        }
    }


    public void openLockDisPlayV67(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (nodeInfo == null) return;
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.LOCKDISPALY) && !mIsOpen) {
            EventBus.getDefault().post(new PathEvent(Permission.LOCKDISPALY, true,0));
            PermissionProvider.save(mContext, PROVIDER_LOCKDISPLAY, true);
            back(service);
            return;
        } else if (mIsOpen) {
            return;
        } else {
            if (NodeInfoUtil.pageContains(nodeInfo, "软件管理") && !getList().contains(SIGN1)) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "软件管理");
                addSIGN(SIGN1);
            } else if (NodeInfoUtil.pageContains(nodeInfo, "权限管理") && getList().contains(SIGN1) && !getList().contains(SIGN2)) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "权限管理");
                addSIGN(SIGN2);
            } else if (NodeInfoUtil.pageContains(nodeInfo, "锁屏显示") && getList().contains(SIGN2) && !getList().contains(SIGN3)) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "锁屏显示");
                addSIGN(SIGN3);
            } else if (NodeInfoUtil.pageContains(nodeInfo, AppUtils.getAppName(mContext)) && !getList().contains(SIGN4)) {
                List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(AppUtils.getAppName(mContext));
                if (nodes != null && nodes.size() > 0) {
                    boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, AppUtils.getAppName(mContext));
                    addSIGN(SIGN4);
                    if (isOpen) {
                        mIsOpen = isOpen;
                        PermissionProvider.save(mContext, PROVIDER_LOCKDISPLAY, true);
                        EventBus.getDefault().post(new PathEvent(Permission.LOCKDISPALY, true,1));
                        back(service);
                    }
                } else {
                    NodeInfoUtil.scrollableList(nodeInfo);
                }
            }
        }
    }


    /**
     * 比较奇葩的手机，代码点击会出现二次允许框，点击允许 锁屏显示权限还是不开启，只能走手动引导点击
     * v4_4
     * y66
     * 系统:6.0.1	OS:3	I管家：5.2.2.6 待升级i管家后测
     */
    public void openLockDisPlayV4_4Y66(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (nodeInfo == null) return;
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.LOCKDISPALY) && !mIsOpen) {
            if (!result) {
                result=true;
                EventBus.getDefault().post(new PathEvent(Permission.LOCKDISPALY, true,0));
                PermissionProvider.save(mContext, PROVIDER_LOCKDISPLAY, true);

                back(service);
            }
            return;
        } else if (mIsOpen||result) {
            return;
        } else {
            if (NodeInfoUtil.pageContains(nodeInfo, "锁屏显示") && NodeInfoUtil.pageContains(nodeInfo, AppUtils.getAppName(mContext)) && !getList().contains(SIGN1)) {
                List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText("锁屏显示");
                if (nodes != null && nodes.size() > 0) {
                    addSIGN(SIGN1);
                    Rect rect = new Rect(0, 0, 0, 0);
                    nodes.get(0).getParent().getBoundsInScreen(rect);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            floaWindow(mContext, rect);
                        }
                    }, 500);
                } else {
                    NodeInfoUtil.scrollableList(nodeInfo);
                }
            }
        }
    }
}
