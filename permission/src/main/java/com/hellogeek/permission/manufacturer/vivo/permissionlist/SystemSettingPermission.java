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
import com.hellogeek.permission.util.NodeInfoUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.hellogeek.permission.util.Constant.PROVIDER_SYSTEMSETTING;

/**
 * 修改系统设置-vivo
 */
public class SystemSettingPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;

    public SystemSettingPermission(Context context) {
        super(context);
        this.mContext = context;
    }

    public void openSystemSetting(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (nodeInfo == null) return;
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SYSTEMSETTING) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SYSTEMSETTING, true,0));
                PermissionProvider.save(mContext, PROVIDER_SYSTEMSETTING, true);

                back(service);
            }
            return;
        }else if (mIsOpen||result){
            return;
        } else {
            if (NodeInfoUtil.pageContains(nodeInfo, "修改系统设置") && !getList().contains(SIGN1)) {
                List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText("允许修改系统设置");
                if (nodes != null && nodes.size() > 0) {
                    mIsOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "允许修改系统设置");
                    addSIGN(SIGN1);
                    if (mIsOpen) {
                        PermissionProvider.save(mContext, PROVIDER_SYSTEMSETTING, true);
                        EventBus.getDefault().post(new PathEvent(Permission.SYSTEMSETTING, true,1));
                        back(service);
                    }
                } else {
                    NodeInfoUtil.scrollableList(nodeInfo);
                }
            }
        }
    }


    /**
     * 比较奇葩的手机，点击返回点击成功，但是 修改系统设置还是不开启，只能走手动引导点击
     * v4_4
     * y66
     * 系统:6.0.1	OS:3	I管家：5.2.2.6 待升级i管家后测
     */
    public void openSystemSettingV4_4Y66(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (nodeInfo == null) return;
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SYSTEMSETTING) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SYSTEMSETTING, true,0));
                PermissionProvider.save(mContext, PROVIDER_SYSTEMSETTING, true);
                back(service);
            }
            return;
        }else if (mIsOpen||result){
            return;
        } else {
            if (NodeInfoUtil.pageContains(nodeInfo, "修改系统设置") && !getList().contains(SIGN1)) {
                List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText("允许修改系统设置");
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
