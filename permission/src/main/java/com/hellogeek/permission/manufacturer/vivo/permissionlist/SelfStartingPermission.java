package com.hellogeek.permission.manufacturer.vivo.permissionlist;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.ManfacturerBase;
import com.hellogeek.permission.manufacturer.vivo.VivoModel;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.strategy.PathEvent;
import com.hellogeek.permission.util.AccessibilitUtil;
import com.hellogeek.permission.util.AppUtils;
import com.hellogeek.permission.util.NodeInfoUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.hellogeek.permission.util.Constant.PROVIDER_NOTIFICATIONBAR;
import static com.hellogeek.permission.util.Constant.PROVIDER_SELFSTARTING;

/**
 * 自启动-vivo
 */
public class SelfStartingPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;


    public SelfStartingPermission(Context context) {
        super(context);
        mContext = context;
    }
   private  boolean isEX=false;
    public void openSelfStarting(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {

        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SELFSTARTING)) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true,0));
                PermissionProvider.save(mContext, PROVIDER_SELFSTARTING, true);

                back(service);
            }
            return;
        }else if (result) {
            return;
        }  else if (NodeInfoUtil.pageContains(info, "权限") && !getList().contains(SIGN1)) {
            List<AccessibilityNodeInfo> list = info.findAccessibilityNodeInfosByText("权限");
            boolean isOpen = false;
            if (list != null && list.size() > 1) {
                isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, list.get(1), "权限");
            } else {
                isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "权限");
            }
            addSIGN(SIGN1);
        } else if (NodeInfoUtil.pageContains(info, "自启动") &&
                !getList().contains(SIGN2)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "自启动");
            addSIGN(SIGN2);
        } else if (NodeInfoUtil.pageContains(info, "自启动")&&getList().contains(SIGN2) && !getList().contains(SIGN3)) {
            if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext))) {
                List<AccessibilityNodeInfo> nodes = info.findAccessibilityNodeInfosByText(AppUtils.getAppName(mContext));
                if (nodes != null && nodes.size() > 0) {
                    addSIGN(SIGN3);
                    Rect rect = new Rect(0, 0, 0, 0);
                    nodes.get(0).getParent().getBoundsInScreen(rect);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            floaWindow(mContext, rect);
                        }
                    }, 500);
                }
            } else {
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                NodeInfoUtil.scrollableList(info);
            }
        }
    }


    public void openSelfStartingY67(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SELFSTARTING)) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true,0));
                PermissionProvider.save(mContext, PROVIDER_SELFSTARTING, true);

                back(service);
            }
            return;
        } else if (NodeInfoUtil.pageContains(info, "软件管理") && !getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "软件管理");
            addSIGN(SIGN1);
        } else if (NodeInfoUtil.pageContains(info, "权限管理") &&
                !getList().contains(SIGN2) && getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "权限管理");
            addSIGN(SIGN2);
        } else if (NodeInfoUtil.pageContains(info, "自启动管理") &&
                !getList().contains(SIGN3) && getList().contains(SIGN2)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "自启动管理");
            addSIGN(SIGN3);
        } else if (getList().contains(SIGN3) && !getList().contains(SIGN4)) {
            if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext))) {
                List<AccessibilityNodeInfo> nodes = info.findAccessibilityNodeInfosByText(AppUtils.getAppName(mContext));
                if (nodes != null && nodes.size() > 0) {
                    addSIGN(SIGN4);
                    Rect rect = new Rect(0, 0, 0, 0);
                    nodes.get(0).getParent().getBoundsInScreen(rect);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            floaWindow(mContext, rect);
                        }
                    }, 500);
                }
            } else {
                NodeInfoUtil.scrollableList(info);
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void openSelfStartingV2V3(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SELFSTARTING)) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true,0));
                PermissionProvider.save(mContext, PROVIDER_SELFSTARTING, true);

                back(service);
            }
            return;
        } else if (NodeInfoUtil.pageContains(info, "软件管理") && !getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "软件管理");
            addSIGN(SIGN1);
        } else if (NodeInfoUtil.pageContains(info, "自启动管理") &&
                !getList().contains(SIGN2) && getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "自启动管理");
            addSIGN(SIGN2);
        } else if (getList().contains(SIGN2) && !getList().contains(SIGN3)) {
            if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext))) {
                List<AccessibilityNodeInfo> nodes = info.findAccessibilityNodeInfosByText(AppUtils.getAppName(mContext));
                if (nodes != null && nodes.size() > 0) {
                    addSIGN(SIGN3);
                    Rect rect = new Rect(0, 0, 0, 0);
                    nodes.get(0).getParent().getBoundsInScreen(rect);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            floaWindow(mContext, rect);
                        }
                    }, 500);

                }
            } else {
                NodeInfoUtil.scrollableList(info);
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
