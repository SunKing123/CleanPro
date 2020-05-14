package com.hellogeek.permission.strategy;

import android.content.Context;

import com.hellogeek.permission.manufacturer.huawei.HuaweiPermissionExecute;
import com.hellogeek.permission.manufacturer.miui.MiuiPermissionExecute;
import com.hellogeek.permission.manufacturer.oppo.colors.OppoPermissionExecute;
import com.hellogeek.permission.manufacturer.other.OtherPermissionExecute;
import com.hellogeek.permission.manufacturer.vivo.VivoPermissionExecute;
import com.hellogeek.permission.util.PhoneRomUtils;

import static com.hellogeek.permission.manufacturer.PermissionSystemPath.OPPO_COLOROS_PACKAGE_NAME;
import static com.hellogeek.permission.manufacturer.PermissionSystemPath.OPPO_COLORO_PACKAGE_NAME;
import static com.hellogeek.permission.manufacturer.PermissionSystemPath.OPPO_SAFE;


public class IGetManfactureExample {

    public static AutoFixAction getManfactureExample(Context context) {
        if (PhoneRomUtils.isHuawei()) {
            return new HuaweiPermissionExecute(context);
        } else if (PhoneRomUtils.isXiaoMi()) {
            return new MiuiPermissionExecute(context);
        } else if (PhoneRomUtils.isVivo()) {
            return new VivoPermissionExecute(context);
        } else if (PhoneRomUtils.isOppo()) {
            if (PhoneRomUtils.isPackName(context, OPPO_COLOROS_PACKAGE_NAME)) {
                return new OppoPermissionExecute(context);
            } else if (PhoneRomUtils.isPackName(context, OPPO_COLORO_PACKAGE_NAME)) {
                return new com.hellogeek.permission.manufacturer.oppo.color.OppoPermissionExecute(context);
            } else if (PhoneRomUtils.isPackName(context, OPPO_SAFE)) {
                return new com.hellogeek.permission.manufacturer.oppo.safe.OppoPermissionExecute(context);
            }

        }else{
            return new OtherPermissionExecute(context);
        }
        return null;
    }

}
