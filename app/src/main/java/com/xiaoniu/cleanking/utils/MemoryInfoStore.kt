package com.xiaoniu.cleanking.utils

import android.content.Context
import com.jess.arms.utils.FileUtils
import com.xiaoniu.clean.deviceinfo.EasyMemoryMod
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Created by xinxiaolong on 2020/8/16.
 * email：xinxiaolong123@foxmail.com
 */
public class MemoryInfoStore {

    private var usedMemoryPercent:Double = 0.0

    companion object {
        @Volatile
        private var instance: MemoryInfoStore? = null
        fun getInstance() =
                instance ?: synchronized(this) {
                    instance
                            ?: MemoryInfoStore().also { instance = it }
                }
    }
    private constructor() {

    }

    fun getTotalMemory(context:Context):Float{
        var easyMemoryMod=EasyMemoryMod(context)
        var total = easyMemoryMod.getTotalRAM().toFloat()
        return FileUtils.getUnitGB(total).toFloat()
    }

    fun getUsedMemoryPercent(context:Context):Double{
        var easyMemoryMod=EasyMemoryMod(context)
        var total = easyMemoryMod.getTotalRAM().toFloat()
        var used = total - easyMemoryMod.getAvailableRAM().toFloat()
        usedMemoryPercent = (used.toDouble() / total.toDouble()) * 100
        return usedMemoryPercent
    }

    fun getUsedMemory(context:Context):Float{
        var easyMemoryMod=EasyMemoryMod(context)
        var total = easyMemoryMod.getTotalRAM().toFloat()
        var used = total - easyMemoryMod.getAvailableRAM().toFloat()
        used = FileUtils.getUnitGB(used).toFloat()
        return used
    }


    /**
     * 加载假的内存信息
     */
    fun getFalseUsedPercent(context:Context):Double{
        var easyMemoryMod=EasyMemoryMod(context)
        //内存加速值，需要在真是的百分比上减去假的加速百分比，然后对一直用的内存进行相应计算显示，瞒天过海，骗过用户。
        var num = PreferenceUtil.getOneKeySpeedNum()
        var falsePercent:Double
        if(usedMemoryPercent<=0){
            var total = easyMemoryMod.getTotalRAM().toFloat()
            var used = total - easyMemoryMod.getAvailableRAM().toFloat()
            usedMemoryPercent = (used.toDouble() / total.toDouble()) * 100
        }
        falsePercent=usedMemoryPercent
        falsePercent=format(falsePercent)!!.toDouble()
        falsePercent=falsePercent-num.toDouble()
        return falsePercent
    }

    fun getFalseUsedMemory(context:Context):Float{
        var easyMemoryMod=EasyMemoryMod(context)
        var falsePercent=getFalseUsedPercent(context)
        var total = easyMemoryMod.getTotalRAM().toFloat()
        var used = (total*(falsePercent/100)).toFloat()
        return FileUtils.getUnitGB(used).toFloat()
    }

    fun format(value: Double): String? {
        var bd = BigDecimal(value)
        bd = bd.setScale(0, RoundingMode.HALF_UP)
        return bd.toString()
    }

}
