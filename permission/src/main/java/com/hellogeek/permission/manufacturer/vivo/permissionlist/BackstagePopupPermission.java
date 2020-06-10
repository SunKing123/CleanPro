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

import java.util.List;

import static com.hellogeek.permission.util.Constant.PROVIDER_BACKSTAGEPOPUP;

/**
 * 后台弹出-vivo
 */
public class BackstagePopupPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;


    public BackstagePopupPermission(Context context) {
        super(context);
        mContext = context;
    }

    public void openBackstagePopup(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (nodeInfo == null) return;
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.BACKSTAGEPOPUP) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.BACKSTAGEPOPUP, true,0));
                PermissionProvider.save(mContext, PROVIDER_BACKSTAGEPOPUP, true);
                back(service);
            }
            return;
        }else if (mIsOpen||result){
            return;
        } else {
            if (NodeInfoUtil.pageContains(nodeInfo, "后台弹出界面") && NodeInfoUtil.pageContains(nodeInfo, AppUtils.getAppName(mContext)) && !getList().contains(SIGN1)) {
                List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText("后台弹出界面");
                if (nodes != null && nodes.size() > 0) {
                    boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "后台弹出界面");
                    addSIGN(SIGN1);
                    if (isOpen) {
                        PermissionProvider.save(mContext, PROVIDER_BACKSTAGEPOPUP, true);
                        mIsOpen = isOpen;
                        EventBus.getDefault().post(new PathEvent(Permission.BACKSTAGEPOPUP, true,1));
                        back(service);
                    }
                } else {
                    NodeInfoUtil.scrollableList(nodeInfo);
                }
            }
        }
    }

}
