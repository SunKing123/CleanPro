package com.hellogeek.permission.manufacturer.oppo.color.permissionlist;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.ManfacturerBase;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.strategy.PathEvent;
import com.hellogeek.permission.util.AccessibilitUtil;
import com.hellogeek.permission.util.NodeInfoUtil;

import org.greenrobot.eventbus.EventBus;

import static com.hellogeek.permission.util.Constant.PROVIDER_SYSTEMSETTING;

/**
 * 修改系统设置-oppo
 */
public class SystemSettingPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;

    public SystemSettingPermission(Context context) {
        super(context);
        this.mContext = context;
    }

    public void openSystemSetting(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SYSTEMSETTING)&&!mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SYSTEMSETTING, true,0));
                PermissionProvider.save(mContext, PROVIDER_SYSTEMSETTING, true);

                back(service);
            }
            return;
        } else if (mIsOpen||result){
            return;
        } else if ((NodeInfoUtil.pageContains(info, "可修改系统设置") || NodeInfoUtil.pageContains(info, "修改系统设置")) && !getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "允许修改系统设置");
            addSIGN(SIGN1);
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_SYSTEMSETTING, true);
                EventBus.getDefault().post(new PathEvent(Permission.SYSTEMSETTING, true,1));
                back(service);
            }
        } else {
            NodeInfoUtil.scrollableList(info);
        }
    }

}
