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

import static com.hellogeek.permission.util.Constant.PROVIDER_REPLACEACLLPAGE;

/**
 * 替换来电页面（拨号）
 */
public class RepLaceAcllPagePermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;

    public RepLaceAcllPagePermission(Context context) {
        super(context);
        this.mContext = context;
    }

    private int frequency = 0;

    public void openRepLaceAcllPage(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.REPLACEACLLPAGE)&&!mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.REPLACEACLLPAGE, true,0));
                PermissionProvider.save(mContext, PROVIDER_REPLACEACLLPAGE, true);

                back(service);
            }
            return;
        } else if (mIsOpen||result){
            return;
        } else if (NodeInfoUtil.pageContains(info, "高级") && NodeInfoUtil.pageContains(info, "应用管理") && !getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "高级");
            addSIGN(SIGN1);
        } else if (NodeInfoUtil.pageContains(info, "设置") && !getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "设置");
            addSIGN(SIGN1);
        } else if (NodeInfoUtil.pageContains(info, "默认应用设置") &&
                NodeInfoUtil.pageContains(info, "应用权限") && getList().contains(SIGN1)&&!getList().contains(SIGN2)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "默认应用设置");
            addSIGN(SIGN2);
        } else if (NodeInfoUtil.pageContains(info, "拨号") && getList().contains(SIGN2)&&!getList().contains(SIGN3)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "拨号");
            addSIGN(SIGN3);
        } else if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext)) && getList().contains(SIGN3)&&!getList().contains(SIGN4)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, AppUtils.getAppName(mContext));
            addSIGN(SIGN4);
        } else if (NodeInfoUtil.pageContains(info, "更换") && getList().contains(SIGN4)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "更换");
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_REPLACEACLLPAGE, true);
                EventBus.getDefault().post(new PathEvent(Permission.REPLACEACLLPAGE, true,1));
                back(service);
            }
        }
    }


    public void openRepLaceAcllPage26(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.REPLACEACLLPAGE)&&!mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.REPLACEACLLPAGE, true,0));
                PermissionProvider.save(mContext, PROVIDER_REPLACEACLLPAGE, true);

                back(service);
            }
            return;
        }else if (mIsOpen||result){
            return;
        }else if (NodeInfoUtil.pageContains(info, "默认应用") && !getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "默认应用");
            addSIGN(SIGN1);
        } else if (NodeInfoUtil.pageContains(info, "拨号") && getList().contains(SIGN1) && !getList().contains(SIGN2)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "拨号");
            addSIGN(SIGN2);
        } else if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext)) && getList().contains(SIGN2) && !getList().contains(SIGN3)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, AppUtils.getAppName(mContext));
            addSIGN(SIGN3);
        } else if (NodeInfoUtil.pageContains(info, "更换") && getList().contains(SIGN3)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "更换");
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_REPLACEACLLPAGE, true);
                EventBus.getDefault().post(new PathEvent(Permission.REPLACEACLLPAGE, true,1));
                back(service);
            }
        }
    }

    public void openRepLaceAcllPage29(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.REPLACEACLLPAGE)&&!mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.REPLACEACLLPAGE, true,0));
                PermissionProvider.save(mContext, PROVIDER_REPLACEACLLPAGE, true);

                back(service);
            }
            return;
        } else if (mIsOpen||result){
            return;
        } else if (NodeInfoUtil.pageContains(info, "默认应用") && !getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "默认应用");
            addSIGN(SIGN1);
        } else if (NodeInfoUtil.pageContains(info, "电话") && getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "电话");
            addSIGN(SIGN2);
        } else if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext)) && getList().contains(SIGN2)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, AppUtils.getAppName(mContext));
            addSIGN(SIGN3);
        } else if (NodeInfoUtil.pageContains(info, "确定") && getList().contains(SIGN3)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "确定");
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_REPLACEACLLPAGE, true);
                EventBus.getDefault().post(new PathEvent(Permission.REPLACEACLLPAGE, true,1));
                back(service);
            }
        }
    }
}
