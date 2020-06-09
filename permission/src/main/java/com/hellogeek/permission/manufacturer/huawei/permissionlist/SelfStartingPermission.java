package com.hellogeek.permission.manufacturer.huawei.permissionlist;

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

import static com.hellogeek.permission.util.Constant.PROVIDER_SELFSTARTING;

public class SelfStartingPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;


    public SelfStartingPermission(Context context) {
        super(context);
        mContext = context;
    }

    public void openSelfStarting(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SELFSTARTING) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true,0));
                PermissionProvider.save(mContext, PROVIDER_SELFSTARTING, true);

                back(service);
            }
            return;
        } else if (mIsOpen||result){
            return;
        }else if ((NodeInfoUtil.pageContains(info, "启动管理") ||
                NodeInfoUtil.pageContains(info, "开机自动启动") ||
                NodeInfoUtil.pageContains(info, "应用启动管理") ||
                NodeInfoUtil.pageContains(info, "自动启动")) && !getList().contains(SIGN1)
        ) {

            if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext))) {
                boolean isopen = NodeInfoUtil.clickNodeInfoAll(mContext, info, AppUtils.getAppName(mContext),true,false);
                if (isopen) {
                    addSIGN(SIGN1);
                }
            } else {
                NodeInfoUtil.scrollableList(info);
            }
        } else if (NodeInfoUtil.pageContains(info, "允许自启动") &&
                NodeInfoUtil.pageContains(info, "允许关联启动") &&
                NodeInfoUtil.pageContains(info, "允许后台活动") && getList().contains(SIGN1)) {
            if (!getList().contains(SIGN2)) {
                boolean isOpen1 = NodeInfoUtil.clickNodeInfoAll(mContext, info, "允许自启动");
                if (isOpen1) {
                    addSIGN(SIGN2);
                }
            }
            if (!getList().contains(SIGN3)) {
                boolean isOpen1 = NodeInfoUtil.clickNodeInfoAll(mContext, info, "允许关联启动", false);
                if (isOpen1) {
                    addSIGN(SIGN3);
                }
            }
            if (!getList().contains(SIGN4)) {
                boolean isOpen1 = NodeInfoUtil.clickNodeInfoAll(mContext, info, "允许后台活动", false);
                if (isOpen1) {
                    addSIGN(SIGN4);
                }
            }
            if (getList().contains(SIGN2) && getList().contains(SIGN3) && getList().contains(SIGN4)) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_SELFSTARTING, true);
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true,1));
                back(service);
            }
        }
    }


    public void openSelfStarting24(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SELFSTARTING) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true,0));
                PermissionProvider.save(mContext, PROVIDER_SELFSTARTING, true);


                back(service);
            }
            return;
        } else if (mIsOpen||result){
            return;
        } else if (NodeInfoUtil.pageContains(info, "自启管理") && !getList().contains(SIGN1)) {

            if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext))) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, AppUtils.getAppName(mContext));
                addSIGN(SIGN1);
            } else {
                NodeInfoUtil.scrollableList(info);
            }
        } else if (NodeInfoUtil.pageContains(info, "允许") &&
                NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext)) && getList().contains(SIGN1)) {

            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "允许");
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_SELFSTARTING, true);
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true,1));
                back(service);
            }
        }
    }

    public void openSelfStarting23(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SELFSTARTING) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true,0));
                PermissionProvider.save(mContext, PROVIDER_SELFSTARTING, true);

                back(service);
            }
            return;
        } else if (mIsOpen||result){
            return;
        } else if (NodeInfoUtil.pageContains(info, "自动启动管理") && !getList().contains(SIGN1)) {

            if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext))) {
                NodeInfoUtil.clickNodeInfoAll(mContext, info, AppUtils.getAppName(mContext));
                addSIGN(SIGN1);
            } else {
                NodeInfoUtil.scrollableList(info);
            }
        } else if (NodeInfoUtil.pageContains(info, "允许") &&
                NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext)) && getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "允许");
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_SELFSTARTING, true);
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true,1));
                back(service);
            }
        } else if (NodeInfoUtil.pageContains(info, "确定") &&
                NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext)) && getList().contains(SIGN1)) {

            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "确定");
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_SELFSTARTING, true);
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true,1));
                back(service);
            }
        }
    }

}
