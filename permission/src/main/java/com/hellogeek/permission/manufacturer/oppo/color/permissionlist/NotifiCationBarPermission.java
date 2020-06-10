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

import static com.hellogeek.permission.util.Constant.PROVIDER_NOTIFICATIONBAR;

/**
 * 通知栏-oppo
 */
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
                result=true;
                EventBus.getDefault().post(new PathEvent(Permission.NOTIFICATIONBAR, true,0));
                PermissionProvider.save(mContext, PROVIDER_NOTIFICATIONBAR, true);

                back(service);
            }
            return;
        }else if (mIsOpen||result){
            return;
        } else if ((NodeInfoUtil.pageContains(info, "设置") || NodeInfoUtil.pageContains(info, "通知与状态栏") )&& !getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "通知与状态栏");
                addSIGN(SIGN1);
        } else if (NodeInfoUtil.pageContains(info, "通知管理") && getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "通知管理");
                addSIGN(SIGN2);
        } else if (NodeInfoUtil.pageContains(info, "通知管理") && getList().contains(SIGN2) && !getList().contains(SIGN3)) {
            if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext))) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, AppUtils.getAppName(mContext));
                addSIGN(SIGN3);
            } else if (!getList().contains(SIGN3)) {
                NodeInfoUtil.scrollableList(info);
            }
        } else if (NodeInfoUtil.pageContains(info, "允许通知") && getList().contains(SIGN3) && !getList().contains(SIGN4)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "允许通知");
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_NOTIFICATIONBAR, true);
                EventBus.getDefault().post(new PathEvent(Permission.NOTICEOFTAKEOVER, true,1));
                back(service);
            }
        } else {
            NodeInfoUtil.scrollableList(info);
        }
    }

}
