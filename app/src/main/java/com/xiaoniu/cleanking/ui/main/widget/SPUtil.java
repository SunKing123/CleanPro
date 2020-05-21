package com.xiaoniu.cleanking.ui.main.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.xiaoniu.cleanking.app.AppApplication;
import com.xiaoniu.common.utils.AppUtils;
import com.xiaoniu.common.utils.ContextUtils;

import java.util.HashSet;
import java.util.Set;

public class SPUtil {
    private static SharedPreferences sp;
    private static SharedPreferences guideSp;
    private static String XML_NAME;
    //    public static String mytoken = "8a6aaa59e24ee07ec820170220151447";
    public static String myInformationId = "1";
    //    public static String mytoken = "38ef122c894e4cc2a820170223155249";
    public static String mytoken = "";
    public static String myBrName = "myBrName";
    public static String ONEKEY_ACCESS = "onekey_access";
    public static String myBrHomeName = "myBrHomeName";
    public static String ATTENTIONUP = "ATTENTIONUP";
    public static String ATTENTIONDOWN = "ATTENTIONDOWN";
    public static String REFRESHDATA = "REFRESHDATA";
    public static String REFRESHMESSAGEDATA = "REFRESHMESSAGEDATA";

    public static String VOUDATA = "VOUDATA";

    public static String PRAISEUP = "PRAISEUP";
    public static String PRAISEDOWN = "PRAISEDOWN";
    public static String CRASHPATH = "/sdcard/calftrader/";

    public static String REPAYMENT = "REPAYMENT";
    public static String EXTENSION = "EXTENSION";
    public static String EXTENSION_FIRST = "EXTENSION_FIRST";
    public static String ONREPAY = "ONREPAY";


    public static String TITLE_CALENDAR = "TITLE_CALENDAR";
    public static String FIRST_TIME_CALENDAR = "FIRST_TIME_CALENDAR";
    public static String SECOND_TIME_CALENDAR = "SECOND_TIME_CALENDAR";
    public static String THIRD_TIME_CALENDAR = "THIRD_TIME_CALENDAR";

    public static String Guide = "";

    public static final String KEY_CONSENT_AGREEMENT = "consentAgreement";
    public static final String KEY_IS_First = "isfirst";
    public static String IS_CLEAR = "is_clear";
    public static String TOTLE_CLEAR_CATH = "totle_clear_cath";
    private static final String KEY_ENABLE_CLEAN_NOTIFICATION = "key_enable_clean_notification";
    private static final String KEY_DISABLE_CLEAN_PACKAGE = "key_disable_clean_package";
    private static final String KEY_NOTIFY_CLEAN_COUNT = "key_notify_clean_count";
    private static final String KEY_POWER_CLEAN_TIME = "key_power_clean_time";

    public static void setMyToken(String mytokens) {
        mytoken = mytokens;
    }

    public static void init(Context context) {
        XML_NAME = AppUtils.getVersionName(context, context.getPackageName()) + "xiaoniu";
        Guide = AppUtils.getVersionName(context, context.getPackageName()) + "guide";
    }

    public static void setBoolean(Context ctx, String key, boolean value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(Context ctx, String key, boolean defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }

    public static void setString(Context ctx, String key, String value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }

    public static String getString(Context ctx, String key, String defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }

    public static void setInt(Context ctx, String key, int value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, value).commit();
    }

    public static int getInt(Context ctx, String key, int defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        }
        return sp.getInt(key, defValue);
    }

    public static void setFloat(Context ctx, String key, float value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putFloat(key, value).commit();
    }

    public static Float getFloat(Context ctx, String key, float defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        }
        return sp.getFloat(key, defValue);
    }

    public static void setLong(Context ctx, String key, long value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putLong(key, value).commit();
    }

    public static Long getLong(Context ctx, String key, long defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        }
        return sp.getLong(key, defValue);
    }

    public static void setList(Context ctx, String key, Set<String> value) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putStringSet(key, value).commit();
    }

    public static Set<String> getList(Context ctx, String key, Set<String> defValue) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        }
        return sp.getStringSet(key, defValue);
    }


    public static void remove(Context ctx, String key) {
        if (sp == null) {
            sp = ctx.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }

    public static void setGuideBoolean(Context ctx, String key, boolean value) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        guideSp.edit().putBoolean(key, value).commit();
    }

    public static boolean getGuideBoolean(Context ctx, String key, boolean defValue) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        return guideSp.getBoolean(key, defValue);
    }


    public static void setisInValid(Context ctx, String key, boolean value) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        guideSp.edit().putBoolean(key, value).commit();
    }

    public static void setIsFirstLoan(Context ctx, String key, boolean value) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        guideSp.edit().putBoolean(key, value).commit();
    }

    public static boolean getisInValid(Context ctx, String key, boolean defValue) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        return guideSp.getBoolean(key, defValue);
    }

    public static boolean getIsFirstLoan(Context ctx, String key, boolean defValue) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        return guideSp.getBoolean(key, defValue);
    }

    public static void setScaleBoolean(Context ctx, String key, boolean value) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        guideSp.edit().putBoolean(key, value).commit();
    }

    public static boolean getScaleBoolean(Context ctx, String key, boolean defValue) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        return guideSp.getBoolean(key, defValue);
    }

    public static void setCalendFirst(Context ctx, String key, boolean value) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        guideSp.edit().putBoolean(key, value).commit();
    }

    public static boolean getCalendFirst(Context ctx, String key, boolean defValue) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        return guideSp.getBoolean(key, defValue);
    }

//    public static void setEduFirst(Context ctx, String key, boolean value) {
//        if (guideSp == null) {
//            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
//        }
//        guideSp.edit().putBoolean(key, value).commit();
//    }
//
//    public static boolean getEduFirst(Context ctx, String key, boolean defValue) {
//        if (guideSp == null) {
//            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
//        }
//        return guideSp.getBoolean(key, defValue);
//    }

    public static boolean getFirstIn(Context ctx, String key, boolean defValue) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        return guideSp.getBoolean(key, defValue);
    }

    public static void setStartsNumber(Context ctx, String key, int value) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        guideSp.edit().putInt(key, value).commit();
    }

    public static int getStartsNumber(Context ctx, String key, int defValue) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        return guideSp.getInt(key, defValue);
    }


    public static void setUploadBoolean(Context ctx, String key, boolean value) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        guideSp.edit().putBoolean(key, value).commit();
    }

    public static void setFirstIn(Context ctx, String key, boolean value) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        guideSp.edit().putBoolean(key, value).commit();
    }

    public static boolean getUploadBoolean(Context ctx, String key, boolean defValue) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        return guideSp.getBoolean(key, defValue);
    }


    public static void setRepair(Context ctx, String key, boolean value) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        guideSp.edit().putBoolean(key, value).commit();
    }

    public static boolean getRepairBoolean(Context ctx, String key, boolean defValue) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        return guideSp.getBoolean(key, defValue);
    }


    public static void setGuideString(Context ctx, String key, String value) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        guideSp.edit().putString(key, value).commit();
    }

    public static String getGuideString(Context ctx, String key, String defValue) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        return guideSp.getString(key, defValue);
    }

    public static void setAsyncString(Context ctx, String key, String value) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        guideSp.edit().putString(key, value).commit();
    }

    public static String getAsyncString(Context ctx, String key, String defValue) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        return guideSp.getString(key, defValue);
    }

    public static void setGuideInt(Context ctx, String key, int value) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        guideSp.edit().putInt(key, value).commit();
    }

    public static int getGuideInt(Context ctx, String key, int defValue) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        return guideSp.getInt(key, defValue);
    }

    /**
     * 清空Sp中保存的信息
     *
     * @param context Context
     */
    public static void clear(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences(XML_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().clear().apply();
    }

    public static void setIsClear(Context ctx, String key, boolean value) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        guideSp.edit().putBoolean(key, value).commit();
    }

    public static void getIsClear(Context ctx, String key, boolean value) {
        if (guideSp == null) {
            guideSp = ctx.getSharedPreferences(Guide, Context.MODE_PRIVATE);
        }
        guideSp.edit().putBoolean(key, value).commit();
    }


    /**
     * 当前是否为审核状态，审核状态则隐藏相关页面
     *
     * @return
     */
    public static boolean isInAudit() {
        //  状态（0=隐藏，1=显示）
        String auditSwitch = SPUtil.getString(AppApplication.getInstance(), AppApplication.AuditSwitch, "1");
        return TextUtils.equals(auditSwitch, "0");
    }

    /**
     * 说明：设置是否开启通知栏清理功能
     * <br>作者：huyang
     * <br>添加时间：2019/5/19 13:25
     */
    public static void setCleanNotificationEnable(boolean status) {
        setBoolean(ContextUtils.getContext(), KEY_ENABLE_CLEAN_NOTIFICATION, status);
    }

    /**
     * 说明：获取通知栏清理设置页功能开启状态
     * <br>作者：huyang
     * <br>添加时间：2019/5/19 13:25
     */
    public static boolean isCleanNotificationEnable() {
        return getBoolean(ContextUtils.getContext(), KEY_ENABLE_CLEAN_NOTIFICATION, true);
    }

    /**
     * 说明：获取通知栏清理白名单
     * <br>作者：huyang
     * <br>添加时间：2019/5/23 20:27
     */
    public static Set<String> getActualWhitelist() {
        Set<String> userSetWhitelist = SPUtil.getDisableCleanNotificationPackages(null);
        if (userSetWhitelist == null) {
            userSetWhitelist = getDefaultWhitelist();
            SPUtil.addDisableCleanNotificationPackages(userSetWhitelist.toArray(new String[0]));
        }
        return userSetWhitelist;
    }

    private static Set<String> getDefaultWhitelist() {
        Set<String> whitelist = new HashSet<>();
        whitelist.add("com.tencent.mm");
        whitelist.add("com.tencent.mobileqq");
        whitelist.add("com.tencent.wework");
        whitelist.add("com.xiaoniu.cleanking");
        return whitelist;
    }

    /**
     * 说明：保存用户设置的可展示通知的应用白名单
     * <br>作者：huyang
     * <br>添加时间：2019/5/19 13:47
     */
    public static void addDisableCleanNotificationPackages(String... pkgs) {
        if (pkgs == null) {
            return;
        } else if (pkgs.length == 0) {
            ContextUtils.getContext().getSharedPreferences(XML_NAME, Context.MODE_PRIVATE).edit().putStringSet(KEY_DISABLE_CLEAN_PACKAGE, new HashSet<String>()).commit();
        }
        Set<String> raw = getDisableCleanNotificationPackages(new HashSet<String>());
        raw = new HashSet<>(raw);
        boolean modified = false;
        for (String pkg : pkgs) {
            if (!TextUtils.isEmpty(pkg) && raw.add(pkg)) {
                modified = true;
            }
        }

        if (modified) {
            ContextUtils.getContext().getSharedPreferences(XML_NAME, Context.MODE_PRIVATE).edit().putStringSet(KEY_DISABLE_CLEAN_PACKAGE, raw).commit();
        }
    }

    public static void removeDisableCleanNotificationPackage(String pkg) {
        if (TextUtils.isEmpty(pkg)) {
            return;
        }
        Set<String> raw = getDisableCleanNotificationPackages(new HashSet<String>());
        if (raw.contains(pkg)) {
            raw = new HashSet<>(raw);
            raw.remove(pkg);
            ContextUtils.getContext().getSharedPreferences(XML_NAME, Context.MODE_PRIVATE).edit().putStringSet(KEY_DISABLE_CLEAN_PACKAGE, raw).commit();
        }
    }

    /**
     * 说明：获取用户设置的可展示通知的应用白名单
     * <br>作者：huyang
     * <br>添加时间：2019/5/19 14:03
     */
    public static Set<String> getDisableCleanNotificationPackages(Set<String> defaultSet) {
        return ContextUtils.getContext().getSharedPreferences(XML_NAME, Context.MODE_PRIVATE).getStringSet(KEY_DISABLE_CLEAN_PACKAGE, defaultSet);
    }

    /**
     * 获取通知栏清理累计通知数
     */
    public static long getNotifyCleanCount() {
        return getLong(ContextUtils.getContext(), KEY_NOTIFY_CLEAN_COUNT, 0l);
    }

    public static void addNotifyCleanCount(long newCount) {
        if (newCount > 0) {
            long allCount = getNotifyCleanCount() + newCount;
            setLong(ContextUtils.getContext(), KEY_NOTIFY_CLEAN_COUNT, allCount);
        }
    }

    public static long getLastPowerCleanTime() {
        return getLong(ContextUtils.getContext(), KEY_POWER_CLEAN_TIME, 0l);
    }

    public static void setLastPowerCleanTime(long time) {
        if (time > 0) {
            setLong(ContextUtils.getContext(), KEY_POWER_CLEAN_TIME, time);
        }
    }

    public static String getLastNewsID(String newType) {
        return getString(ContextUtils.getContext(), newType, "");
    }

    /*保存最后一条资讯的key，防止加载更多时数据重复*/
    public static void setLastNewsID(String newType, String newsId) {
        setString(ContextUtils.getContext(), newType, newsId);
    }


}

