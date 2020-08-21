package com.xiaoniu.cleanking.ui.deskpop.deviceinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.base.SimpleFragment
import com.xiaoniu.cleanking.ui.deskpop.base.StartActivityUtils
import com.xiaoniu.cleanking.ui.main.event.LifecycEvent
import com.xiaoniu.cleanking.ui.tool.notify.event.FunctionCompleteEvent
import com.xiaoniu.cleanking.utils.HomeDeviceInfoStore
import com.xiaoniu.cleanking.utils.LogUtils
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
class HomeDeviceInfoFragment : SimpleFragment() {

    companion object {
        fun getInstance(): HomeDeviceInfoFragment {
            val fragment = HomeDeviceInfoFragment()
            return fragment
        }
    }

    //内存和存储阈值
    private var low: Array<Int> = arrayOf(0, 49)

    //电量状态阈值
    private var bLow: Array<Int> = arrayOf(0, 20)
    private var bHigh: Array<Int> = arrayOf(20, 90)

    private var BATTERY_VPT = 37
    private var CPU_VPT = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), null)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_phone_memory_state_layout
    }

    fun initData(savedInstanceState: Bundle?) {

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

    fun refreshAllView() {
        initMemoryView()
        initStorageView()
        initCoolView()
        initBatteryView()
        LogUtils.e("=================HomeDeviceInfoFragment:refreshAllView()")
    }

    /**
     * 运行信息
     */
    private fun initMemoryView() {
        if (PreferenceUtil.getCleanTime()) {
            initTrueMemoryView()
        } else {
            initCleanedMemoryView()
        }
    }

    /**
     * 加载真是的内存信息
     */
    private fun initTrueMemoryView() {
        var total = HomeDeviceInfoStore.getInstance().getTotalMemory(mContext)
        var used = HomeDeviceInfoStore.getInstance().getUsedMemory(mContext)
        var percent = HomeDeviceInfoStore.getInstance().getUsedMemoryPercent()
        setMemoryViewData(total, used, percent)
    }

    /**
     * 加载假的内存信息
     */
    private fun initCleanedMemoryView() {
        //内存加速值，需要在真是的百分比上减去假的加速百分比，然后对一直用的内存进行相应计算显示，瞒天过海，骗过用户。
        var total = HomeDeviceInfoStore.getInstance().getTotalMemory(mContext)
        var used = HomeDeviceInfoStore.getInstance().getCleanedUsedMemory(mContext)
        var percent = HomeDeviceInfoStore.getInstance().getCleanedUsedMemoryPercent()
        setMemoryViewData(total, used, percent)
    }

    private fun setMemoryViewData(total: Float, used: Float, percent: Int) {
        tv_memory_title.setText("运行总内存：" + total + " GB")
        tv_memory_content.setText("已用运行内存：" + used + " GB")
        tv_memory_percent.setText(percent.toString() + "%")
        updateMemoryOrStorageImage(image_memory, percent)
        updateMemoryOrStorageBtnBackGround(btn_clean_memory, percent)
    }


    fun format(value: Double): String? {
        if (value <= 0) {
            return value.toString()
        }
        var bd = BigDecimal(value)
        bd = bd.setScale(0, RoundingMode.HALF_UP)
        return bd.toString()
    }

    /**
     * 内部存储信息
     */
    fun initStorageView() {
        if (PreferenceUtil.getNowCleanTime()) {
            initTrueStorageView()
        } else {
            initCleanedStorageView()
        }
    }

    private fun initTrueStorageView() {
        var total = HomeDeviceInfoStore.getInstance().getTotalStorage(mContext)
        var used = HomeDeviceInfoStore.getInstance().getUsedStorage(mContext)
        var percent = HomeDeviceInfoStore.getInstance().getUsedStoragePercent(mContext)

        tv_storage_title.setText("内部总存储：" + total + " GB")
        tv_storage_content.setText("已用内部存储：" + used + " GB")
        tv_storage_percent.setText(format(percent) + "%")
        updateMemoryOrStorageImage(image_storage, percent.toInt())
        updateMemoryOrStorageBtnBackGround(btn_clean_storage, percent.toInt())
    }

    /**
     * 内部存储信息清理后的
     */
    private fun initCleanedStorageView() {
        var total = HomeDeviceInfoStore.getInstance().getTotalStorage(mContext)
        var used = HomeDeviceInfoStore.getInstance().getCleanedUsedStorage(mContext)
        var percent = HomeDeviceInfoStore.getInstance().getCleanedUsedStoragePercent(mContext)

        tv_storage_title.setText("内部总存储：" + total + " GB")
        tv_storage_content.setText("已用内部存储：" + used + " GB")
        tv_storage_percent.setText(format(percent.toDouble()) + "%")
        updateMemoryOrStorageImage(image_storage, percent.toInt())
        updateMemoryOrStorageBtnBackGround(btn_clean_storage, percent.toInt())
    }

    /**
     * 温度信息
     */
    private fun initCoolView() {
        if (PreferenceUtil.getCoolingCleanTime()) {
            initTrueCoolView()
        } else {
            initCleanedCoolView()
        }
    }

    private fun initTrueCoolView() {
        var batteryT = HomeDeviceInfoStore.getInstance().getBatteryTemperature(mContext)
        var cpuT = HomeDeviceInfoStore.getInstance().getCPUTemperature(mContext)
        updateCoolImage(batteryT, cpuT)
        updateBtn(batteryT, cpuT)
        tv_temperature_title.setText("电池温度：" + batteryT + "°C")
        tv_temperature_content.setText("CPU温度：" + cpuT + "°C")
    }

    /**
     * 温度信息
     */
    private fun initCleanedCoolView() {
        var batteryT = HomeDeviceInfoStore.getInstance().getCleanedBatteryTemperature(mContext)
        var cpuT = HomeDeviceInfoStore.getInstance().getCleanedCPUTemperature(mContext)
        updateCoolImage(batteryT, cpuT)
        updateBtn(batteryT, cpuT)
        tv_temperature_title.setText("电池温度：" + batteryT + "°C")
        tv_temperature_content.setText("CPU温度：" + cpuT + "°C")
    }

    /**
     * 电量信息
     */
    private fun initBatteryView() {
        if (PreferenceUtil.getPowerCleanTime()) {
            initTrueBatteryView()
        } else {
            initCleanedBatteryView()
        }
    }

    private fun initTrueBatteryView() {
        tv_battery_title.setText("当前电量：" + HomeDeviceInfoStore.getInstance().getElectricNum(mContext) + "%")
        tv_battery_content.setText("待机时间" + HomeDeviceInfoStore.getInstance().getStandTime(mContext) + "小时")
        tv_battery_percent.setText(HomeDeviceInfoStore.getInstance().getElectricNum(mContext).toString() + "%")

        updateBatteryImage(HomeDeviceInfoStore.getInstance().getElectricNum(mContext))
        var percent = HomeDeviceInfoStore.getInstance().getElectricNum(mContext)

        if (inTheRange(percent, bLow)) {
            btn_clean_battery.setBackgroundResource(R.drawable.clear_btn_red_bg)
        } else {
            btn_clean_battery.setBackgroundResource(R.drawable.clear_btn_green_bg)
        }
    }

    /**
     * 电量信息
     */
    private fun initCleanedBatteryView() {
        tv_battery_title.setText("当前电量：" + HomeDeviceInfoStore.getInstance().getElectricNum(mContext) + "%")
        tv_battery_content.setText("待机时间" + HomeDeviceInfoStore.getInstance().getCleanedStandTime(mContext))
        tv_battery_percent.setText(HomeDeviceInfoStore.getInstance().getElectricNum(mContext).toString() + "%")

        updateBatteryImage(HomeDeviceInfoStore.getInstance().getElectricNum(mContext))
        var percent = HomeDeviceInfoStore.getInstance().getElectricNum(mContext)

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
        memoryClick()
        StartActivityUtils.goCleanMemory(activity!!)
    }

    /**
     * 垃圾清理
     */
    private fun goCleanStorage() {
        if (isDestroy()) return
        storageClick()
        StartActivityUtils.goCleanStorage(activity!!)
    }

    /**
     * 手机降温
     */
    private fun goCool() {
        if (isDestroy()) return
        batteryClick()
        StartActivityUtils.goPhoneCool(activity!!)

    }

    /**
     * 电池优化
     */
    private fun goCleanBattery() {
        if (isDestroy()) return
        powerClick()
        StartActivityUtils.goCleanBattery(activity!!)
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

    private fun updateCoolImage(batteryT: Float, cpuT: Float) {
        if (batteryT > BATTERY_VPT || cpuT > CPU_VPT) {
            image_temperature.setImageResource(R.drawable.icon_temperature_percent_high)
        } else {
            image_temperature.setImageResource(R.drawable.icon_temperature_percent_normal)
        }
    }

    private fun updateBtn(batteryT: Float, cpuT: Float) {
        if (batteryT > BATTERY_VPT || cpuT > CPU_VPT) {
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
        EventBus.getDefault().unregister(this)
    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************eventBus notify******************************************************************************
     *********************************************************************************************************************************************************
     */

    /**
     * 功能使用完毕通知
     */
    @Subscribe
    fun fromFunctionCompleteEvent(event: FunctionCompleteEvent?) {
        if (event == null || event.title == null || isDestroy()) {
            return
        }
        when (event.title) {
            "一键清理" -> initCleanedStorageView()
            "一键加速" -> initCleanedMemoryView()
            "手机降温" -> initCleanedCoolView()
            "超强省电" -> initCleanedBatteryView()

        }
    }

    /**
     * 热启动回调
     */
    @Subscribe
    fun changeLifeCycleEvent(lifecycEvent: LifecycEvent?) {
        refreshAllView()
    }

    fun memoryClick() {
        StatisticsUtils.trackClick(Points.HomeDeviceInfo.MEMORY_CLICK_EVENT_CODE, Points.HomeDeviceInfo.MEMORY_CLICK_EVENT_NAME, "", Points.HomeDeviceInfo.PAGE)
    }

    fun storageClick() {
        StatisticsUtils.trackClick(Points.HomeDeviceInfo.STORAGE_CLICK_EVENT_CODE, Points.HomeDeviceInfo.STORAGE_CLICK_EVENT_NAME, "", Points.HomeDeviceInfo.PAGE)
    }

    fun batteryClick() {
        StatisticsUtils.trackClick(Points.HomeDeviceInfo.BATTERY_CLICK_EVENT_CODE, Points.HomeDeviceInfo.BATTERY_CLICK_EVENT_NAME, "", Points.HomeDeviceInfo.PAGE)
    }

    fun powerClick() {
        StatisticsUtils.trackClick(Points.HomeDeviceInfo.POWER_CLICK_EVENT_CODE, Points.HomeDeviceInfo.POWER_CLICK_EVENT_NAME, "", Points.HomeDeviceInfo.PAGE)
    }

}
