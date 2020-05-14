package com.hellogeek.permission.manufacturer.miui.permissionlist;

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

import static com.hellogeek.permission.util.Constant.PROVIDER_LOCKDISPLAY;
/**
 * 锁屏显示-小米
 * */
public class LockDisPlayPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;


    public LockDisPlayPermission(Context context) {
        super(context);
        mContext = context;
    }
    public void openLockDisPlay(Context mContext, AccessibilityNodeInfo info, AccessibilityService service){
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.LOCKDISPALY)&&!mIsOpen) {
            if (!result) {
                result=true;
                EventBus.getDefault().post(new PathEvent(Permission.LOCKDISPALY, true,0));
                PermissionProvider.save(mContext, PROVIDER_LOCKDISPLAY, true);
                back(service);
            }
            return;
        } else if (mIsOpen||result){
            return;
        }else if (NodeInfoUtil.pageContains(info, "锁屏显示") && !getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext,info, "锁屏显示");
            addSIGN(SIGN1);
        } else if (NodeInfoUtil.pageContains(info, "允许") &&
                !getList().contains(SIGN2) && getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext,info, "允许");
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_LOCKDISPLAY, true);
                EventBus.getDefault().post(new PathEvent(Permission.LOCKDISPALY, true,1));
                back(service);
            }
        }else{
            NodeInfoUtil.scrollableList(info);
        }
    }
}
