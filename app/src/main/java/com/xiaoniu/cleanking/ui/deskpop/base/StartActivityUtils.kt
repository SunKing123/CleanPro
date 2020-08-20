package com.xiaoniu.cleanking.ui.deskpop.base

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.alibaba.android.arouter.launcher.ARouter
import com.meishu.sdk.core.utils.LogUtil
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.constant.RouteConstants
import com.xiaoniu.cleanking.ui.accwidget.AccWidgetAnimationActivity
import com.xiaoniu.cleanking.ui.accwidget.AccWidgetCleanFinishActivity
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity
import com.xiaoniu.cleanking.ui.newclean.util.StartFinishActivityUtil.Companion.gotoFinish
import com.xiaoniu.cleanking.utils.ExtraConstant
import com.xiaoniu.cleanking.utils.LogUtils
import com.xiaoniu.cleanking.utils.update.PreferenceUtil

/**
 * Created by xinxiaolong on 2020/8/16.
 * email：xinxiaolong123@foxmail.com
 */
class StartActivityUtils {

    companion object {

        /*
         *********************************************************************************************************************************************************
         ************************************************************start oneKey acc************************************************************************
         *********************************************************************************************************************************************************
        */
        fun goCleanMemory(context: Context) {
            if (PreferenceUtil.getCleanTime()) {
                forceGoCleanMemory(context)
            } else {
                goFinishActivity(context, context.getString(R.string.tool_one_key_speed))
            }
        }

        fun forceGoCleanMemory(context: Context) {
            val bundle = Bundle()
            bundle.putString(SpCacheConfig.ITEM_TITLE_NAME, context.getString(R.string.tool_one_key_speed))
            var intent = Intent(context, PhoneAccessActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        /*
          *********************************************************************************************************************************************************
          ************************************************************start oneKey clean************************************************************************
          *********************************************************************************************************************************************************
         */

        /**
         * 首页跳转一键清理
         */
        fun goCleanStorage(context: Context) {
            if (PreferenceUtil.getNowCleanTime()) {
                forceGoCleanStorage(context)
            } else {
                goFinishActivity(context, context.getString(R.string.tool_suggest_clean))
            }
        }

        fun forceGoCleanStorage(context: Context) {
            startActivity(context, NowCleanActivity::class.java)
        }

        /*
       *********************************************************************************************************************************************************
       ************************************************************start phone cool***********************************************************************
       *********************************************************************************************************************************************************
       */
        fun goPhoneCool(context: Context) {
            if (PreferenceUtil.getCoolingCleanTime()) {
                forceGoPhoneCool()
            } else {
                goFinishActivity(context, context.getString(R.string.tool_phone_temperature_low))
            }
        }

        /**
         * 手机降温
         */
        fun forceGoPhoneCool() {
            ARouter.getInstance().build(RouteConstants.PHONE_COOLING_ACTIVITY).navigation()
        }


        /*
       *********************************************************************************************************************************************************
       ************************************************************start clean battery**************************************************************************
       *********************************************************************************************************************************************************
       */
        fun goCleanBattery(context: Context) {
            if (PreferenceUtil.getPowerCleanTime()) {
                forceGoCleanBattery(context)
            } else {
                goFinishActivity(context, context.getString(R.string.tool_super_power_saving))
            }
        }

        /**
         * 电池优化
         */
        fun forceGoCleanBattery(context: Context) {
            startActivity(context, PhoneSuperPowerActivity::class.java)
        }

        /*
        *********************************************************************************************************************************************************
        ************************************************************start activity tool fun**********************************************************************
        *********************************************************************************************************************************************************
        */

        private fun startActivity(context: Context, cls: Class<*>?) {
            var intent = Intent(context, cls)
            context?.startActivity(intent)
        }

        private fun goFinishActivity(context: Context, title: String) {
            val bundle = Bundle()
            bundle.putString(ExtraConstant.TITLE, title)
            gotoFinish(context, bundle)
        }
        
        /**
         * 创建加速快捷方式
         */
        fun createAccShortcut(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                var shortcutManager = context.getSystemService(ShortcutManager::class.java);
                if (shortcutManager.isRequestPinShortcutSupported()) {
                    var intent = Intent(context, AccWidgetAnimationActivity::class.java)
                    intent.action="action_create_acc_shortcut"
                    intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK

                    var pinShortcutInfo = ShortcutInfo.Builder(context, "acc_shortcut")
                            .setShortLabel("一键加速")
                            .setLongLabel("一键加速")
                            .setIcon(Icon.createWithResource(context, R.drawable.acc_shortcut_log))
                            .setIntent(intent)
                            .build()

                    //当固定快捷方式成功后，会执行该Intent指定的操作，包括启动Activity、发送广播等，如果固定快捷方式成功后不需要做额外处理的话该参数传null就可以。
                   var create=shortcutManager.requestPinShortcut(pinShortcutInfo,null);
                }
            }
        }

        fun createdShortcut(context: Context):Boolean{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                var shortcutManager = context.getSystemService(ShortcutManager::class.java);
                 if(shortcutManager.isRequestPinShortcutSupported&&!shortcutManager.pinnedShortcuts.isEmpty()){
                     return true
                 }
            }
            return false
        }
    }

}
