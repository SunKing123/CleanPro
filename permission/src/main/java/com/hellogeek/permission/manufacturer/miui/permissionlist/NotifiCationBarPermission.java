package com.hellogeek.permission.manufacturer.miui.permissionlist;

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

import static com.hellogeek.permission.util.Constant.PROVIDER_NOTIFICATIONBAR;

public class NotifiCationBarPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;

    public NotifiCationBarPermission(Context context) {
        super(context);
        this.mContext = context;
    }

    public void openNotifiCationBar(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.NOTIFICATIONBAR)&&!mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.NOTIFICATIONBAR, mIsOpen,0));
                PermissionProvider.save(mContext, PROVIDER_NOTIFICATIONBAR, true);
                back(service);
            }
            return;
        }else if (mIsOpen||result){
            return;
        } else if ((NodeInfoUtil.pageContains(info, "通知使用权") || NodeInfoUtil.pageContains(info, "通知读取权限")) && !getList().contains(SIGN1)) {
            if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext))) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, AppUtils.getAppName(mContext));
                addSIGN(SIGN1);
            } else if (!getList().contains(SIGN1)) {
                NodeInfoUtil.scrollableList(info);
            }

        } else if (NodeInfoUtil.pageContains(info, "是否启用") || NodeInfoUtil.pageContains(info, "要启用") && getList().contains(SIGN1) && !getList().contains(SIGN2)) {
            boolean isOpen = false;
            List<AccessibilityNodeInfo> nodes = info.findAccessibilityNodeInfosByText("确定");
            if (nodes != null && nodes.size() > 0) {
                isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "确定");
            }
            List<AccessibilityNodeInfo> nodes1 = info.findAccessibilityNodeInfosByText("允许");
            if (nodes1 != null && nodes1.size() > 0) {
                if (!isOpen) {
                    isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "允许");
                }
            }
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_NOTIFICATIONBAR, true);
                EventBus.getDefault().post(new PathEvent(Permission.NOTIFICATIONBAR, mIsOpen,1));
                back(service);
            }
        } else if (NodeInfoUtil.pageContains(info, "停用") || NodeInfoUtil.pageContains(info, "取消") && getList().contains(SIGN1) && !getList().contains(SIGN2)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "取消");
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_NOTIFICATIONBAR, true);
                EventBus.getDefault().post(new PathEvent(Permission.NOTIFICATIONBAR, mIsOpen,1));
                back(service);
            }
        }
    }

}
