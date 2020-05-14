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

import static com.hellogeek.permission.util.Constant.PROVIDER_SELFSTARTING;

/**
 * 自启动-小米
 */
public class SelfStartingPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;


    public SelfStartingPermission(Context context) {
        super(context);
        mContext = context;
    }


    public void openSelfStarting(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SELFSTARTING)&&!mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true,0));
                PermissionProvider.save(mContext, PROVIDER_SELFSTARTING, true);

                back(service);
            }
            return;
        } else if (mIsOpen||result){
            return;
        } else if (NodeInfoUtil.pageContains(info, "关于自启动的说明") &&
                NodeInfoUtil.pageContains(info, "确定") && !getList().contains(SIGN1)
        ) {
            NodeInfoUtil.clickNodeInfoAll(mContext, info, "确定");
            addSIGN(SIGN1);
        } else if (NodeInfoUtil.pageContains(info, "自启动管理") &&
                !getList().contains(SIGN2)) {
            if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext))) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, AppUtils.getAppName(mContext));
                addSIGN(SIGN2);
            } else {
                NodeInfoUtil.scrollableList(info);
            }
        } else if ((NodeInfoUtil.pageContains(info, "允许系统唤醒") || NodeInfoUtil.pageContains(info, "允许被其他应用唤醒")) &&
                getList().contains(SIGN2)) {
            boolean isOpen=false;
            List<AccessibilityNodeInfo> nodes = info.findAccessibilityNodeInfosByText("允许系统唤醒");
            if (nodes != null && nodes.size() > 0) {
                 isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "允许系统唤醒");
            }
            List<AccessibilityNodeInfo> nodes1= info.findAccessibilityNodeInfosByText("允许被其他应用唤醒");
            if (nodes1 != null && nodes1.size() > 0) {
                if (!isOpen) {
                    isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "允许被其他应用唤醒");
                }
            }
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_SELFSTARTING, true);
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true,1));
                back(service);
            }
        }
    }

}
