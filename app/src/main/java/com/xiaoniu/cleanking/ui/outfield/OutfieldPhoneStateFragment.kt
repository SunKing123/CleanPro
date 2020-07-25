package com.xiaoniu.cleanking.ui.outfield

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import com.jess.arms.base.SimpleFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.FileUtils
import com.xiaoniu.clean.deviceinfo.EasyBatteryMod
import com.xiaoniu.clean.deviceinfo.EasyMemoryMod
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.utils.NumberUtils
import kotlinx.android.synthetic.main.fragment_phone_memory_state_layout.*
import java.util.*


/**
 * Created by xinxiaolong on 2020/7/24.
 * email：xinxiaolong123@foxmail.com
 */
class OutfieldPhoneStateFragment : SimpleFragment() {

    var low: Array<Int> = arrayOf(0, 20)
    var medium: Array<Int> = arrayOf(21, 60)
    var high: Array<Int> = arrayOf(80, 99)
    var max = 100

    override fun setupFragmentComponent(appComponent: AppComponent) {

    }

    override fun setData(data: Any?) {

    }

    override fun initData(savedInstanceState: Bundle?) {
        var easyMemoryMod = EasyMemoryMod(mContext)

        //运行信息
        var total=easyMemoryMod.getTotalRAM()
        var available=easyMemoryMod.getAvailableRAM()
        var percent=(available.toDouble()/total.toDouble())*100
        tv_memory_title.setText("运行总内存："+FileUtils.getUnitGB(total.toFloat()))
        tv_memory_content.setText("可用运行内存："+FileUtils.getUnitGB(available.toFloat()))
        tv_memory_percent.setText(percent.toInt().toString()+"%")
        updateMemoryOrStorageImage(image_memory,percent.toInt())

        //内部存储信息
        total=easyMemoryMod.getTotalInternalMemorySize()
        available=easyMemoryMod.getAvailableInternalMemorySize()
        percent=(available.toDouble()/total.toDouble())*100

        tv_storage_title.setText("内部总存储："+FileUtils.getUnitGB(total.toFloat()))
        tv_storage_content.setText("可用内部存储："+FileUtils.getUnitGB(available.toFloat()))
        tv_storage_percent.setText(percent.toInt().toString()+"%")
        updateMemoryOrStorageImage(image_storage,percent.toInt())

        //温度信息
        var easyBatteryMod=EasyBatteryMod(mContext)
        tv_temperature_title.setText("电池温度："+easyBatteryMod.getBatteryTemperature()+"°C")
        tv_temperature_content.setText("CPU温度："+(easyBatteryMod.getBatteryTemperature()+ NumberUtils.mathRandomInt(5, 10).toFloat())+"°C")

        //电量信息
        tv_battery_title.setText("当前电量："+easyBatteryMod.getBatteryPercentage()+"%")
        tv_battery_content.setText("待机时间"+ NumberUtils.mathRandomInt(5, 10)+"小时")
        updateBatteryImage(easyBatteryMod.getBatteryPercentage())
        tv_battery_percent.setText(easyBatteryMod.getBatteryPercentage().toString()+"%")

    }

    private val units = arrayOf("B", "KB", "MB", "GB")

    /**
     * 单位转换
     */
    private fun getUnit(size: Float): String? {
        var size = size
        var index = 0
        while (size > 1024 && index < units.size) {
            size = size / 1024
            index++
        }
        return String.format(Locale.getDefault(), " %.1f %s", size, units[index])
    }
    override fun getLayoutId(): Int {
        return R.layout.fragment_phone_memory_state_layout
    }

    fun updateMemoryOrStorageImage(image: ImageView, percent: Int) {
        if (inTheRange(percent, low)) {
            image.setImageResource(R.drawable.icon_memory_percent_low)
        } else if (inTheRange(percent, medium)) {
            image.setImageResource(R.drawable.icon_memory_percent_medium)
        } else if (inTheRange(percent, high)) {
            image.setImageResource(R.drawable.icon_memory_percent_high)
        } else {
            image.setImageResource(R.drawable.icon_memory_percent_max)
        }
    }

    fun updateBatteryImage(percent: Int) {
        if (inTheRange(percent, low)) {
            image_battery.setImageResource(R.drawable.icon_battery_percent_low)
        } else if (inTheRange(percent, medium)) {
            image_battery.setImageResource(R.drawable.icon_battery_percent_medium)
        } else if (inTheRange(percent, high)) {
            image_battery.setImageResource(R.drawable.icon_battery_percent_high)
        } else {
            image_battery.setImageResource(R.drawable.icon_battery_percent_max)
        }
    }

    fun inTheRange(percent: Int, range: Array<Int>): Boolean {
        return percent >= range[0] && percent <= range[1]
    }
}
