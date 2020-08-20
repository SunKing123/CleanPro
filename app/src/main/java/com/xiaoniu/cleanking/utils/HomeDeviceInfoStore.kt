package com.xiaoniu.cleanking.utils

import android.content.Context
import com.jess.arms.utils.FileUtils
import com.xiaoniu.clean.deviceinfo.EasyBatteryMod
import com.xiaoniu.clean.deviceinfo.EasyMemoryMod
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig
import com.xiaoniu.cleanking.utils.update.MmkvUtil
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Created by xinxiaolong on 2020/8/18.
 * email：xinxiaolong123@foxmail.com
 *
 * 需求文档：https://shimo.im/docs/BJJCJkNZaecnp1O9/read
 */
class HomeDeviceInfoStore {

    private var memoryPercent: Int = 0
    private var cleanedMemoryPercent: Int = 0
    private var cpuTemperature: Float = 0F
    private var standTime: Int = 0

    companion object {
        @Volatile
        private var instance: HomeDeviceInfoStore? = null
        fun getInstance() =
                instance ?: synchronized(this) {
                    instance
                            ?: HomeDeviceInfoStore().also { instance = it }
                }
    }


    private constructor() {

    }


    /*
     *************************************************************************************************************************************************
     **********************************************************memory info****************************************************************************
     *************************************************************************************************************************************************
     */

    /**
     * 获取总内存大小
     */
    fun getTotalMemory(context: Context): Float {
        return MemoryInfoStore.getInstance().getTotalMemory(context)
    }

    /**
     * 获取已使用内存大小
     */
    fun getUsedMemory(context: Context): Float {
        var total = getTotalMemory(context)
        var percent = getUsedMemoryPercent().toDouble() / 100
        return format(total * percent)
    }

    /**
     * 获取清理后已使用内存大小
     */
    fun getCleanedUsedMemory(context: Context): Float {
        var total = getTotalMemory(context)
        return format(total * (getCleanedUsedMemoryPercent().toDouble() / 100))
    }

    /**
     * 获取内存使用百分比
     */
    fun getUsedMemoryPercent(): Int {
        if (memoryPercent == 0) {
            memoryPercent = NumberUtils.mathRandom(70, 85).toInt()
        }
        return memoryPercent
    }

    /**
     * 获取清理后内存使用百分比
     */
    fun getCleanedUsedMemoryPercent(): Int {
        if (cleanedMemoryPercent == 0) {
            cleanedMemoryPercent = getUsedMemoryPercent() - PreferenceUtil.getOneKeySpeedNum().toInt()
        }
        return cleanedMemoryPercent
    }


    /*
     *************************************************************************************************************************************************
     *****************************************************storage info********************************************************************************
     *************************************************************************************************************************************************
     */

    /**
     * 获取总硬盘大小
     */
    fun getTotalStorage(context: Context): Float {
        var easyMemoryMod = EasyMemoryMod(context)
        var total = easyMemoryMod.getTotalInternalMemorySize().toFloat()
        return FileUtils.getUnitGB(total).toFloat()
    }

    /**
     * 获取已使用总硬盘大小
     */
    fun getUsedStorage(context: Context): Float {
        var easyMemoryMod = EasyMemoryMod(context)
        var total = easyMemoryMod.getTotalInternalMemorySize().toFloat()
        var used = total - easyMemoryMod.getAvailableInternalMemorySize().toFloat()
        return FileUtils.getUnitGB(used).toFloat()
    }

    /**
     * 获取已使用硬盘占比
     */
    fun getUsedStoragePercent(context: Context): Double {
        var easyMemoryMod = EasyMemoryMod(context)
        var total = easyMemoryMod.getTotalInternalMemorySize().toDouble()
        var used = total - easyMemoryMod.getAvailableInternalMemorySize().toDouble()
        log("getUsedStoragePercent() total=" + total+"   used="+used+"    percent="+(used / total) * 100)
        return (used / total) * 100
    }

    /**
     * 获取已使用总硬盘大小
     */
    fun getCleanedUsedStorage(context: Context): Float {
        var diff = MmkvUtil.getLong(SpCacheConfig.MKV_KEY_HOME_CLEANED_DATA_B, 0)

        log("getCleanedUsedStorage() diff=" + diff)
        var cleaned = FileUtils.getUnitGB(diff.toFloat())
        var used=getUsedStorage(context)
        log("getCleanedUsedStorage() cleaned=" + cleaned)
        log("getCleanedUsedStorage() used=" + used)
        used=used-cleaned.toFloat()
        log("getCleanedUsedStorage() used after=" + used)

        return format(used.toDouble())
    }

    /**
     * 获取已使用硬盘占比
     */
    fun getCleanedUsedStoragePercent(context: Context): Float {
        var used = getCleanedUsedStorage(context)
        var total = getTotalStorage(context)
        var percent = (used / total) * 100
        log("getCleanedUsedStoragePercent() percent=" + percent + "  used=" + used + "   total=" + total)
        return percent
    }

    /*
     *************************************************************************************************************************************************
     *****************************************************temperature info****************************************************************************
     *************************************************************************************************************************************************
     */

    /**
     * 获取真实的电池温度
     */
    fun getBatteryTemperature(context: Context): Float {
        var easyBatteryMod = EasyBatteryMod(context)
        return easyBatteryMod.batteryTemperature
    }


    /**
     * 需求：部分机型获取不到cpu温度情况下，取随机值【40，60】
     * 实现：部分毛！全都随机!!!
     */
    fun getCPUTemperature(context: Context): Float {
        cpuTemperature = NumberUtils.mathRandomInt(40, 60).toFloat()
        log("getCPUTemperature() cpuTemperature=" + cpuTemperature)
        return cpuTemperature
    }

    /**
     * 获取清理后的电池温度
     * 需求：若用户在核心功能区域使用完手机降温功能，降温完成页降温数值3°，同时手机状态监控电池温度和cpu温度降低3°。
     */
    fun getCleanedBatteryTemperature(context: Context): Float {
        log("getCleanedBatteryTemperature() getCleanCoolNum=" + PreferenceUtil.getCleanCoolNum())
        log("getCleanedBatteryTemperature() getBatteryTemperature=" + getBatteryTemperature(context))
        return getBatteryTemperature(context) - PreferenceUtil.getCleanCoolNum()
    }

    /**
     * 获取清理后的cpu温度
     * 需求：若用户在核心功能区域使用完手机降温功能，降温完成页降温数值3°，同时手机状态监控电池温度和cpu温度降低3°。
     */
    fun getCleanedCPUTemperature(context: Context): Float {
        if(cpuTemperature<=0){
            cpuTemperature=getCPUTemperature(context)
        }
        log("getCleanedCPUTemperature() cpuTemperature=" + cpuTemperature+"   PreferenceUtil.getCleanCoolNum()="+PreferenceUtil.getCleanCoolNum())
        return cpuTemperature - PreferenceUtil.getCleanCoolNum()
    }


    /*
     *************************************************************************************************************************************************
     *****************************************************temperature info****************************************************************************
     *************************************************************************************************************************************************
     */

    fun getElectricNum(context: Context): Int {
        var easyBatteryMod = EasyBatteryMod(context)
        return easyBatteryMod.getBatteryPercentage()
    }

    /**
     * 待机时间
     */
    fun getStandTime(context: Context): Int {
        standTime = NumberUtils.mathRandomInt(5, 10)
        return standTime
    }

    /**
     * 获取清理后的待机时长
     */
    fun getCleanedStandTime(context: Context):String{
        if(standTime==0){
            standTime= getStandTime(context)
        }
        log("getCleanedStandTime() standTime=" + standTime+"   getCleanedBatteryMinutes="+PreferenceUtil.getCleanedBatteryMinutes())
        return standTime.toString()+"小时"+PreferenceUtil.getCleanedBatteryMinutes()+"分钟"
    }

    /**
     * 保存电量优化后的待机时机增加值
     *
     * 需求：
     * 当手机电量【1%~10%），延长时间显示【5，15】分钟随机数
     * 当手机电量【10%~20%），延长时间显示【10，30】分钟随机数
     * 当手机电池电量【20%~50%）时，延长时间显示【10，45】分钟随机数
     * 当手机电池电量【50%，70%）时，延长待机时间显示【20，50】分钟随机数
     * 当手机电池电量【70%，100%】时，延长待机时间显示【30，60】分钟随机数
     */
    fun saveRandomOptimizeElectricNum(context: Context) {
        val electric = getBatteryTemperature(context)
        var num: String? = ""
        num = if (electric >= 70) {
            NumberUtils.mathRandom(30, 59)
        } else if (electric >= 50) {
            NumberUtils.mathRandom(20, 50)
        } else if (electric >= 20) {
            NumberUtils.mathRandom(10, 45)
        } else if (electric >= 10) {
            NumberUtils.mathRandom(10, 30)
        } else {
            NumberUtils.mathRandom(5, 15)
        }
        PreferenceUtil.saveCleanedBatteryMinutes(num.toInt())
        log("saveRandomOptimizeElectricNum() num=" + num)
    }


    fun format(value: Double): Float {
        var bd = BigDecimal(value)
        bd = bd.setScale(1, RoundingMode.HALF_UP)
        return bd.toFloat()
    }

    fun log(text: String) {
        LogUtils.e("HomeDeviceInfoStore:======" + text)
    }
}
