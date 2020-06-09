package com.hellogeek.permission.manufacturer.vivo.permissionlist;

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

import static com.hellogeek.permission.util.Constant.PROVIDER_NOTIFICATIONBAR;

/**
 * 通知栏-vivo
 */
public class NotifiCationBarPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;

    public NotifiCationBarPermission(Context context) {
        super(context);
        this.mContext = context;
    }

    public void openNotifiCationBarV3_2(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.NOTIFICATIONBAR) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.NOTIFICATIONBAR, true, 0));
                PermissionProvider.save(mContext, PROVIDER_NOTIFICATIONBAR, true);

                back(service);
            }
            return;
        } else if (mIsOpen || result) {
            return;
        } else if (NodeInfoUtil.pageContains(info, "通知使用权") || NodeInfoUtil.pageContains(info, "通知读取权限")) {
            if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext)) && !getList().contains(SIGN1)) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, AppUtils.getAppName(mContext));

                addSIGN(SIGN1);

            } else if (!getList().contains(SIGN1)) {
                NodeInfoUtil.scrollableList(info);
            }

        } else if ((NodeInfoUtil.pageContains(info, "允许通知") || NodeInfoUtil.pageContains(info, "在锁屏显示")) && getList().contains(SIGN1) && !getList().contains(SIGN2)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "允许通知");
            boolean isOpen1 = NodeInfoUtil.clickNodeInfoAll(mContext, info, "在锁屏显示", false);
            addSIGN(SIGN2);
            if (isOpen || isOpen1) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_NOTIFICATIONBAR, true);
                EventBus.getDefault().post(new PathEvent(Permission.NOTIFICATIONBAR, true, 1));
                back(service);
            }

        }
    }

    private boolean isOpenLock;

    public void openNotifiCationBarOther(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.NOTIFICATIONBAR) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.NOTIFICATIONBAR, true, 0));
                PermissionProvider.save(mContext, PROVIDER_NOTIFICATIONBAR, true);

                back(service);
            }
            return;
        } else if (mIsOpen || result) {
            return;
        } else if (NodeInfoUtil.pageContains(info, "通知") && NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext)) && !getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "通知");
            if (isOpen) {
                addSIGN(SIGN1);
            }
        } else if ((NodeInfoUtil.pageContains(info, "允许通知")) && getList().contains(SIGN1) && !getList().contains(SIGN2)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "允许通知");
            addSIGN(SIGN2);
            if (isOpen) {
                isOpenLock = true;
            }
        } else if ((NodeInfoUtil.pageContains(info, "允许通知") || NodeInfoUtil.pageContains(info, "在锁屏显示")) && getList().contains(SIGN2) && !getList().contains(SIGN3)) {
            boolean isOpen1 = NodeInfoUtil.clickNodeInfoAll(mContext, info, "在锁屏显示");
            addSIGN(SIGN3);
            if (isOpen1) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_NOTIFICATIONBAR, true);
                EventBus.getDefault().post(new PathEvent(Permission.NOTIFICATIONBAR, true, 1));
                back(service);
            }
        } else if (getList().contains(SIGN1) && !getList().contains(SIGN2) && isOpenLock) {
            mIsOpen = true;
            PermissionProvider.save(mContext, PROVIDER_NOTIFICATIONBAR, true);
            EventBus.getDefault().post(new PathEvent(Permission.NOTIFICATIONBAR, true, 1));
            back(service);
        }
    }

}
