package com.xiaoniu.cleanking.ui.deskpop.base

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.constant.RouteConstants
import com.xiaoniu.cleanking.ui.accwidget.AccDesktopAnimationActivity
import com.xiaoniu.cleanking.ui.accwidget.ShortcutPinReceiver
import com.xiaoniu.cleanking.ui.finish.NewCleanFinishPlusActivity
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity
import com.xiaoniu.cleanking.utils.ExtraConstant
import com.xiaoniu.cleanking.utils.LogUtils
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import java.lang.reflect.InvocationTargetException


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
            NewCleanFinishPlusActivity.start(context,title,true)
        }

        /**
         * 创建加速快捷方式
         */
        fun createAccShortcut(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var shortcutManager = context.getSystemService(ShortcutManager::class.java);
                if (shortcutManager.isRequestPinShortcutSupported()) {
                    var intent = Intent(context, AccDesktopAnimationActivity::class.java)
                    intent.action = Intent.ACTION_VIEW
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    var pinShortcutInfo = ShortcutInfo.Builder(context, "acc_shortcut")
                            .setShortLabel("一键加速")
                            .setLongLabel("一键加速")
                            .setIcon(Icon.createWithResource(context, R.drawable.acc_shortcut_log))
                            .setIntent(intent)
                            .build()

                    //当添加快捷方式的确认弹框弹出来时，将被回调CallBackReceiver里面的onReceive方法
                    val shortcutCallbackIntent = PendingIntent.getBroadcast(context, 0, Intent(context, ShortcutPinReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
                    //当固定快捷方式成功后，会执行该Intent指定的操作，包括启动Activity、发送广播等，如果固定快捷方式成功后不需要做额外处理的话该参数传null就可以。
                    var create = shortcutManager.requestPinShortcut(pinShortcutInfo, shortcutCallbackIntent.intentSender);

                    LogUtils.e("==========================createAccShortcut() create=" + create)
                }
            }
        }

        /**
         * 创建加速快捷方式
         */
        @SuppressLint("RestrictedApi")
        fun createAccShortcut2(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var shortcutManager = context.getSystemService(ShortcutManager::class.java);
                if (shortcutManager.isRequestPinShortcutSupported()) {
                    var intent = Intent(context, AccDesktopAnimationActivity::class.java)
                    intent.action = Intent.ACTION_VIEW
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                    val info: ShortcutInfoCompat = ShortcutInfoCompat.Builder(context, "The only id")
                            .setIcon(IconCompat.createFromIcon(Icon.createWithResource(context, R.drawable.acc_shortcut_log)))
                            .setShortLabel("Short Label")
                            .setIntent(intent)
                            .build()

                    //当添加快捷方式的确认弹框弹出来时，将被回调CallBackReceiver里面的onReceive方法
                    val shortcutCallbackIntent = PendingIntent.getBroadcast(context, 0, Intent(context, ShortcutPinReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
                    //当固定快捷方式成功后，会执行该Intent指定的操作，包括启动Activity、发送广播等，如果固定快捷方式成功后不需要做额外处理的话该参数传null就可以。
                    var create = ShortcutManagerCompat.requestPinShortcut(context,info, shortcutCallbackIntent.intentSender);

                    LogUtils.e("==========================createAccShortcut() create=" + create)
                }
            }
        }

        fun isCreatedShortcut(context: Context?): Boolean {
            if(context==null){
                return false
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                var shortcutManager = context.getSystemService(ShortcutManager::class.java);
                if (shortcutManager.isRequestPinShortcutSupported && !shortcutManager.pinnedShortcuts.isEmpty()) {
                    return true
                }
            }
            return false
        }

        @RequiresApi(Build.VERSION_CODES.M)
         fun createAccShortcut3(context: Context, str: String, icon: Icon, str2: String, intent: Intent) {
            if (!TextUtils.isEmpty(intent.action)) {
                val systemService = context.getSystemService("shortcut")
                try {
                    if ((systemService.javaClass.getMethod("isRequestPinShortcutSupported", *arrayOfNulls(0)).invoke(systemService, *arrayOfNulls(0)) as Boolean)) {
                        val newInstance = Class.forName("android.content.pm.ShortcutInfo\$Builder").getConstructor(*arrayOf(Context::class.java, String::class.java)).newInstance(*arrayOf(context, str))
                        newInstance.javaClass.getDeclaredMethod("setIcon", *arrayOf<Class<*>>(Icon::class.java)).invoke(newInstance, *arrayOf<Any>(icon))
                        newInstance.javaClass.getMethod("setShortLabel", *arrayOf<Class<*>>(CharSequence::class.java)).invoke(newInstance, *arrayOf<Any>(str2))
                        newInstance.javaClass.getMethod("setIntent", *arrayOf<Class<*>>(Intent::class.java)).invoke(newInstance, *arrayOf<Any>(intent))
                        val invoke = newInstance.javaClass.getMethod("build", *arrayOfNulls(0)).invoke(newInstance, *arrayOfNulls(0))
                        systemService.javaClass.getMethod("requestPinShortcut", *arrayOf<Class<*>>(invoke.javaClass, IntentSender::class.java)).invoke(systemService, *arrayOf(invoke, null))
                    }
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e2: IllegalAccessException) {
                    e2.printStackTrace()
                } catch (e3: InvocationTargetException) {
                    e3.printStackTrace()
                } catch (e4: ClassNotFoundException) {
                    e4.printStackTrace()
                } catch (e5: InstantiationException) {
                    e5.printStackTrace()
                }
            }
        }

    }

}
