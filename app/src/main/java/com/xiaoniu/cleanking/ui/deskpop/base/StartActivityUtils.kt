package com.xiaoniu.cleanking.ui.deskpop.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.launcher.ARouter
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.constant.RouteConstants
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity
import com.xiaoniu.cleanking.ui.newclean.util.StartFinishActivityUtil.Companion.gotoFinish
import com.xiaoniu.cleanking.utils.ExtraConstant
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.common.utils.Points
import com.xiaoniu.common.utils.StatisticsUtils

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
        /**
         * 首页跳转一键加速
         */
        fun homeGoCleanMemory(context: Context) {
            if (PreferenceUtil.getCleanTime()) {
                goCleanMemory(context)
            } else {
                goFinishActivity(context, context.getString(R.string.tool_one_key_speed))
            }
        }

        /**
         * 外部插屏跳转一键加速
         */
        fun externalGoCleanMemory(context: Context) {
            goCleanMemory(context)
        }


        private fun goCleanMemory(context: Context) {
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
        fun homeGoCleanStorage(context: Context) {
            if (PreferenceUtil.getNowCleanTime()) {
                goCleanStorage(context)
            } else {
                goFinishActivity(context, context.getString(R.string.tool_suggest_clean))
            }
        }

        /**
         * 外部插屏跳转一键清理
         */
        fun externalGoCleanStorage(context: Context) {
            goCleanMemory(context)
        }

        private fun goCleanStorage(context: Context) {
            startActivity(context, NowCleanActivity::class.java)
        }

        /*
       *********************************************************************************************************************************************************
       ************************************************************start phone cool***********************************************************************
       *********************************************************************************************************************************************************
       */
        /**
         * 首页跳转手机降温
         */
        fun homeGoPhoneCool(context: Context) {
            if (PreferenceUtil.getCoolingCleanTime()) {
                goPhoneCool()
            } else {
                goFinishActivity(context, context.getString(R.string.tool_phone_temperature_low))
            }
        }

        /**
         * 外部插屏跳转手机降温
         */
        fun externalGoPhoneCool(context: Context) {
            goCleanMemory(context)
        }


        /**
         * 手机降温
         */
        private fun goPhoneCool() {
            ARouter.getInstance().build(RouteConstants.PHONE_COOLING_ACTIVITY).navigation()
        }


        /*
       *********************************************************************************************************************************************************
       ************************************************************start clean battery**************************************************************************
       *********************************************************************************************************************************************************
       */
        /**
         * 首页跳转电池优化
         */
        fun homeGoCleanBattery(context: Context) {
            if (PreferenceUtil.getPowerCleanTime()) {
                goCleanBattery(context)
            } else {
                goFinishActivity(context, context.getString(R.string.tool_super_power_saving))
            }
        }

        /**
         * 外部插屏跳转电池优化
         */
        fun externalGoCleanBattery(context: Context) {
            goCleanBattery(context)
        }

        /**
         * 电池优化
         */
        private fun goCleanBattery(context: Context) {
            startActivity(context, PhoneSuperPowerActivity::class.java)
        }


        /*
        *********************************************************************************************************************************************************
       ************************************************************start tool fun********************************************************************************
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
    }

}
