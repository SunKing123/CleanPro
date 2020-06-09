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
import com.hellogeek.permission.util.AppUtils;
import com.hellogeek.permission.util.NodeInfoUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.hellogeek.permission.util.Constant.PACKAGE_USAGE_STATS;
import static com.hellogeek.permission.util.Constant.PROVIDER_SELFSTARTING;
import static com.hellogeek.permission.util.Constant.PROVIDER_SYSTEMSETTING;

/**
 * @ProjectName: clean
 * @Package: com.hellogeek.permission.manufacturer.vivo.permissionlist
 * @ClassName: PakageUsageStatsPermission
 * @Description: 应用使用情况
 * @Author: LiDing
 * @CreateDate: 2020/5/11 21:34
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/11 21:34
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class PakageUsageStatsPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;

    public PakageUsageStatsPermission(Context context) {
        super(context);
        mContext = context;
    }

    public void openPakageUsageStats(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {

        if (info == null) return;
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.PACKAGEUSAGESTATS) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.PACKAGEUSAGESTATS, true, 0));
                PermissionProvider.save(mContext, PACKAGE_USAGE_STATS, true);
                back(service);
            }
            return;
        } else if (mIsOpen || result) {
            return;
        } else {
            if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext)) && !getList().contains(SIGN1)) {
                List<AccessibilityNodeInfo> nodes = info.findAccessibilityNodeInfosByText(AppUtils.getAppName(mContext));
                if (nodes != null && nodes.size() > 0) {
                    boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, AppUtils.getAppName(mContext));
                    if (isOpen) addSIGN(SIGN1);

                } else {
                    NodeInfoUtil.scrollableList(info);
                }
            } else if (NodeInfoUtil.pageContains(info, "允许访问使用记录") && getList().contains(SIGN1)) {
                mIsOpen = NodeInfoUtil.clickNodeInfoAll(mContext, info, "允许访问使用记录");
                if (mIsOpen) {
                    PermissionProvider.save(mContext, PACKAGE_USAGE_STATS, true);
                    EventBus.getDefault().post(new PathEvent(Permission.PACKAGEUSAGESTATS, true, 1));
                    back(service);
                }
            }
        }

    }


}
