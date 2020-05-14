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

import static com.hellogeek.permission.util.Constant.PROVIDER_SELFSTARTING;

/**
 * 自启动-oppo
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
            if (!result){
                result=true;
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true,0));
                PermissionProvider.save(mContext, PROVIDER_SELFSTARTING, true);
                back(service);
            }
            return;
        } else if (mIsOpen||result){
            return;
        }else if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext)) &&
                !getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, AppUtils.getAppName(mContext));
            addSIGN(SIGN1);
            if (isOpen) {
                mIsOpen = isOpen;
                PermissionProvider.save(mContext, PROVIDER_SELFSTARTING, true);
                EventBus.getDefault().post(new PathEvent(Permission.SELFSTARTING, true,1));
                back(service);
            }
        } else {
            if (!NodeInfoUtil.pageContains(info, "自启动")){
                return;
            }
            NodeInfoUtil.scrollableList(info);
        }
    }

}
