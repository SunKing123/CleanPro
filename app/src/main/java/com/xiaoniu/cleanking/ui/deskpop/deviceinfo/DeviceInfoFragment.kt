package com.xiaoniu.cleanking.ui.deskpop.deviceinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import com.jess.arms.utils.FileUtils
import com.xiaoniu.clean.deviceinfo.EasyBatteryMod
import com.xiaoniu.clean.deviceinfo.EasyMemoryMod
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.base.SimpleFragment
import com.xiaoniu.cleanking.ui.deskpop.base.StartActivityUtils
import com.xiaoniu.cleanking.ui.tool.notify.event.FunctionCompleteEvent
import com.xiaoniu.cleanking.utils.MemoryInfoStore
import com.xiaoniu.cleanking.utils.NumberUtils
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.common.utils.Points
import com.xiaoniu.common.utils.StatisticsUtils
import kotlinx.android.synthetic.main.fragment_phone_memory_state_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.math.BigDecimal
import java.math.RoundingMode


/**
 * Created by xinxiaolong on 2020/7/24.
 * email：xinxiaolong123@foxmail.com
 */
class DeviceInfoFragment : SimpleFragment() {

    companion object {
        fun getInstance(): DeviceInfoFragment {
            val fragment = DeviceInfoFragment()
            return fragment
        }
    }
    //内存和存储阈值
    private var low: Array<Int> = arrayOf(0, 49)

    //电量状态阈值
    private var bLow: Array<Int> = arrayOf(0, 20)
    private var bHigh: Array<Int> = arrayOf(20, 90)

    private var TEMPERATURE_VPT = 37
    private lateinit var easyMemoryMod: EasyMemoryMod
    private lateinit var easyBatteryMod: EasyBatteryMod

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), null)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_phone_memory_state_layout
    }

    fun initData(savedInstanceState: Bundle?) {
        easyMemoryMod = EasyMemoryMod(mContext)
        easyBatteryMod = EasyBatteryMod(mContext)
    }

    override fun initView() {
        initMemoryView()
        initStorageView()
        initCoolView()
        initBatteryView()
        initEvent()
        StatisticsUtils.customTrackEvent(Points.ExternalDevice.MEET_CONDITION_CODE, Points.ExternalDevice.MEET_CONDITION_NAME, "", Points.ExternalDevice.PAGE)
        StatisticsUtils.customTrackEvent(Points.ExternalDevice.PAGE_EVENT_CODE, Points.ExternalDevice.PAGE_EVENT_NAME, "", Points.ExternalDevice.PAGE)
    }

    /**
     * 运行信息
     */
    private fun initMemoryView() {
        initTrueMemoryView()

    }

    /**
     * 加载真是的内存信息
     */
    private fun initTrueMemoryView(){
        var total = MemoryInfoStore.getInstance().getTotalMemory(mContext)
        var used =MemoryInfoStore.getInstance().getUsedMemory(mContext)
        var percent=MemoryInfoStore.getInstance().getUsedMemoryPercent(mContext)
        setMemoryViewData(total,used,percent)
    }

    private fun setMemoryViewData(total:Float,used:Float,percent:Double){
        tv_memory_title.setText("运行总内存：" + total + " GB")
        tv_memory_content.setText("已用运行内存：" + used + " GB")
        tv_memory_percent.setText(format(percent) + "%")
        updateMemoryOrStorageImage(image_memory, percent.toInt())
        updateMemoryOrStorageBtnBackGround(btn_clean_memory, percent.toInt())
    }


    fun format(value: Double): String? {
        var bd = BigDecimal(value)
        bd = bd.setScale(0, RoundingMode.HALF_UP)
        return bd.toString()
    }

    /**
     * 内部存储信息
     */
    private fun initStorageView() {
        var total = easyMemoryMod.getTotalInternalMemorySize().toFloat()
        var used = total - easyMemoryMod.getAvailableInternalMemorySize().toFloat()

        var percent = (used.toDouble() / total.toDouble()) * 100
        tv_storage_title.setText("内部总存储：" + FileUtils.getUnitGB(total) + " GB")
        tv_storage_content.setText("已用内部存储：" + FileUtils.getUnitGB(used) + " GB")
        tv_storage_percent.setText(format(percent) + "%")
        updateMemoryOrStorageImage(image_storage, percent.toInt())
        updateMemoryOrStorageBtnBackGround(btn_clean_storage, percent.toInt())
    }


    /**
     * 温度信息
     */
    private fun initCoolView() {
        updateCoolImage(easyBatteryMod.getBatteryTemperature())
        updateBtn(easyBatteryMod.getBatteryTemperature())
        tv_temperature_title.setText("电池温度：" + easyBatteryMod.getBatteryTemperature() + "°C")
        tv_temperature_content.setText("CPU温度：" + (easyBatteryMod.getBatteryTemperature() + NumberUtils.mathRandomInt(5, 10).toFloat()) + "°C")
    }

    /**
     * 电量信息
     */
    private fun initBatteryView() {
        tv_battery_title.setText("当前电量：" + easyBatteryMod.getBatteryPercentage() + "%")
        tv_battery_content.setText("待机时间" + NumberUtils.mathRandomInt(5, 10) + "小时")
        tv_battery_percent.setText(easyBatteryMod.getBatteryPercentage().toString() + "%")

        updateBatteryImage(easyBatteryMod.getBatteryPercentage())

        var percent = easyBatteryMod.getBatteryPercentage();

        if (inTheRange(percent, bLow)) {
            btn_clean_battery.setBackgroundResource(R.drawable.clear_btn_red_bg)
        } else {
            btn_clean_battery.setBackgroundResource(R.drawable.clear_btn_green_bg)
        }
    }

    private fun initEvent() {
        btn_clean_memory.setOnClickListener({ goCleanMemory() })
        btn_clean_storage.setOnClickListener({ goCleanStorage() })
        btn_clean_temperature.setOnClickListener({ goCool() })
        btn_clean_battery.setOnClickListener({ goCleanBattery() })
    }

    /**
     * 一键加速
     */
    private fun goCleanMemory() {
        if (isDestroy()) return
            StatisticsUtils.trackClick(Points.ExternalDevice.CLICK_MEMORY_BTN_CODE, Points.ExternalDevice.CLICK_MEMORY_BTN_NAME, "", Points.ExternalDevice.PAGE)
            StartActivityUtils.forceGoCleanMemory(activity!!)
            finish()
    }

    /**
     * 垃圾清理
     */
    private fun goCleanStorage() {
        if (isDestroy()) return
            StatisticsUtils.trackClick(Points.ExternalDevice.CLICK_STORAGE_BTN_CODE, Points.ExternalDevice.CLICK_STORAGE_BTN_NAME, "", Points.ExternalDevice.PAGE)
            StartActivityUtils.forceGoCleanStorage(activity!!)
            finish()
    }

    /**
     * 手机降温
     */
    private fun goCool() {
        if (isDestroy()) return
            StatisticsUtils.trackClick(Points.ExternalDevice.CLICK_BATTERY_TEMPERATURE_BTN_CODE, Points.ExternalDevice.CLICK_BATTERY_TEMPERATURE_BTN_NAME, "", Points.ExternalDevice.PAGE)
            StartActivityUtils.forceGoPhoneCool()
            finish()
    }

    /**
     * 电池优化
     */
    private fun goCleanBattery() {
        if (isDestroy()) return
            StatisticsUtils.trackClick(Points.ExternalDevice.CLICK_BATTERY_TEMPERATURE_BTN_CODE, Points.ExternalDevice.CLICK_BATTERY_TEMPERATURE_BTN_NAME, "", Points.ExternalDevice.PAGE)
            StartActivityUtils.forceGoCleanBattery(activity!!)
            finish()
    }

    /**
     * 更新按钮背景
     */
    private fun updateMemoryOrStorageBtnBackGround(textView: AppCompatTextView, percent: Int) {
        if (inTheRange(percent, low)) {
            textView.setBackgroundResource(R.drawable.clear_btn_green_bg)
        } else {
            textView.setBackgroundResource(R.drawable.clear_btn_red_bg)
        }
    }

    /**
     * 更新图标颜色
     */
    private fun updateMemoryOrStorageImage(image: ImageView, percent: Int) {
        if (inTheRange(percent, low)) {
            image.setImageResource(R.drawable.icon_memory_percent_low)
        } else {
            image.setImageResource(R.drawable.icon_memory_percent_high)
        }
    }

    private fun updateCoolImage(temperature: Float) {
        if (temperature > TEMPERATURE_VPT) {
            image_temperature.setImageResource(R.drawable.icon_temperature_percent_high)
        } else {
            image_temperature.setImageResource(R.drawable.icon_temperature_percent_normal)
        }
    }

    private fun updateBtn(temperature: Float) {
        if (temperature > TEMPERATURE_VPT) {
            btn_clean_temperature.setBackgroundResource(R.drawable.clear_btn_red_bg)
        } else {
            btn_clean_temperature.setBackgroundResource(R.drawable.clear_btn_green_bg)
        }
    }

    private fun updateBatteryImage(percent: Int) {
        if (inTheRange(percent, bLow)) {
            image_battery.setImageResource(R.drawable.icon_battery_percent_low)
        } else if (inTheRange(percent, bHigh)) {
            image_battery.setImageResource(R.drawable.icon_battery_percent_high)
        } else {
            image_battery.setImageResource(R.drawable.icon_battery_percent_max)
        }
    }

    private fun inTheRange(percent: Int, range: Array<Int>): Boolean {
        return percent >= range[0] && percent <= range[1]
    }

    fun finish() {
        activity?.finish()
    }

    fun isDestroy(): Boolean {
        return activity == null || isDetached || tv_memory_title == null
    }

    override fun onDetach() {
        super.onDetach()
    }
}
