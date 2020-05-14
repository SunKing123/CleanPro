package com.hellogeek.permission.strategy;

import android.app.Activity;
import android.content.Context;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.util.AccessibilitUtil;
import com.hellogeek.permission.util.PhoneRomUtils;

import java.util.List;

import static com.hellogeek.permission.util.Constant.PROVIDER_NECESSARY_PERMISSIONALLOPEN;
import static com.hellogeek.permission.util.Constant.PROVIDER_PERMISSIONALLOPEN;

/**
 * 对外可掉接口类
 */
public class ExternalInterface {
    private static ExternalInterface instance;
    private Context context;

    // 单例模式
    public static ExternalInterface getInstance(Context context) {
        if (null == instance) {
            synchronized (AutoFixAction.class) {
                instance = new ExternalInterface(context);
            }
        }
        return instance;
    }

    public ExternalInterface(Context context) {
        this.context = context;
    }

    /**
     * 获取权限是否全部开启
     */
    public boolean isOpenAllPermission(Activity activity) {
        AutoFixAction autoFixAction = IGetManfactureExample.getManfactureExample(activity);
        if (autoFixAction != null) {

            List<Permission> list = autoFixAction.getPermissionList();
            if (list != null && list.size() > 0) {
                boolean isOpenAll = true;
                for (Permission permission : list) {
                    boolean isOpen = AccessibilitUtil.isOpenPermission(context, permission);
                    isOpenAll = isOpenAll && isOpen;
                }
                if (!isOpenAll) {
                    PermissionProvider.save(context, PROVIDER_PERMISSIONALLOPEN, false);
                }
                return isOpenAll;
            }
        } else {
            boolean isallOpen = PermissionProvider.getBoolen(context, PROVIDER_PERMISSIONALLOPEN, false);
            return isallOpen;
        }
        return false;
    }


    /**
     * 获取必要权限是否全部开启
     */
    public boolean isOpenNecessaryPermission(Activity activity) {
        AutoFixAction autoFixAction = IGetManfactureExample.getManfactureExample(activity);
        if (autoFixAction != null) {
            List<Permission> list = autoFixAction.getPermissionList();
            if (list != null && list.size() > 0) {
                boolean isOpenAll = true;
                for (Permission permission : list) {
                    if (isNecessary(permission)) {
                        boolean isOpen = AccessibilitUtil.isOpenPermission(context, permission);
                        isOpenAll = isOpenAll && isOpen;
                    }
                }
                if (!isOpenAll) {
                    PermissionProvider.save(context, PROVIDER_NECESSARY_PERMISSIONALLOPEN, false);
                }
                return isOpenAll;
            }else {
                boolean isallOpen = PermissionProvider.getBoolen(context, PROVIDER_NECESSARY_PERMISSIONALLOPEN, false);
                return isallOpen;
            }
        } else {
            boolean isallOpen = PermissionProvider.getBoolen(context, PROVIDER_NECESSARY_PERMISSIONALLOPEN, false);
            return isallOpen;
        }
    }


    private boolean isNecessary(Permission permission) {
        switch (permission) {
            case NOTIFICATIONBAR:
                return false;
            case REPLACEACLLPAGE:
                return PhoneRomUtils.isHuawei() ? true : false;
            case BACKSTAGEPOPUP:
                return (PhoneRomUtils.isVivo() || PhoneRomUtils.isXiaoMi()) ? true : false;
            case SYSTEMSETTING:
                return true;

            case SELFSTARTING:
                return (PhoneRomUtils.isOppo()) ? true : false;

            case LOCKDISPALY:
                return (PhoneRomUtils.isVivo() || PhoneRomUtils.isXiaoMi()) ? true : false;
            case SUSPENDEDTOAST:
                return true;
            case NOTICEOFTAKEOVER:
                return false;
        }
        return false;
    }
}
