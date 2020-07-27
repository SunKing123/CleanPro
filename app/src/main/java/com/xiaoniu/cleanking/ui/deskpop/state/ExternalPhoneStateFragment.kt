package com.xiaoniu.cleanking.ui.deskpop.state

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import com.alibaba.android.arouter.launcher.ARouter
import com.jess.arms.base.SimpleFragment
import com.jess.arms.di.component.AppComponent
import com.jess.arms.utils.FileUtils
import com.xiaoniu.clean.deviceinfo.EasyBatteryMod
import com.xiaoniu.clean.deviceinfo.EasyMemoryMod
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.constant.RouteConstants
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity
import com.xiaoniu.cleanking.utils.NumberUtils
import kotlinx.android.synthetic.main.fragment_phone_memory_state_layout.*


/**
 * Created by xinxiaolong on 2020/7/24.
 * email：xinxiaolong123@foxmail.com
 */
class ExternalPhoneStateFragment : SimpleFragment() {

    private var low: Array<Int> = arrayOf(0, 20)
    private var medium: Array<Int> = arrayOf(21, 60)
    private var high: Array<Int> = arrayOf(80, 99)

    private lateinit var easyMemoryMod: EasyMemoryMod
    private lateinit var easyBatteryMod: EasyBatteryMod

    override fun setupFragmentComponent(appComponent: AppComponent) {

    }


    override fun setData(data: Any?) {

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_phone_memory_state_layout
    }

    override fun initData(savedInstanceState: Bundle?) {
        easyMemoryMod = EasyMemoryMod(mContext)
        easyBatteryMod = EasyBatteryMod(mContext)
        initView()
        initEvent()
    }

    private fun initView() {
        initMemoryView()
        initStorageView()
        initCoolView()
        initBatteryView()
    }

    /**
     * 运行信息
     */
    private fun initMemoryView() {
        var total = easyMemoryMod.getTotalRAM()
        var available = easyMemoryMod.getAvailableRAM()
        var percent = (available.toDouble() / total.toDouble()) * 100
        percent = 100 - percent
        tv_memory_title.setText("运行总内存：" + FileUtils.getUnitGB(total.toFloat()))
        tv_memory_content.setText("可用运行内存：" + FileUtils.getUnitGB(available.toFloat()))
        tv_memory_percent.setText(percent.toInt().toString() + "%")
        updateMemoryOrStorageImage(image_memory, percent.toInt())
        updateBtnBackGround(btn_clean_memory, percent.toInt())
    }

    /**
     * 内部存储信息
     */
    private fun initStorageView() {
        var total = easyMemoryMod.getTotalInternalMemorySize().toFloat()
        var available = easyMemoryMod.getAvailableInternalMemorySize().toFloat()
        var percent = (available.toDouble() / total.toDouble()) * 100
        percent = 100 - percent

        tv_storage_title.setText("内部总存储：" + FileUtils.getUnitGB(total))
        tv_storage_content.setText("可用内部存储：" + FileUtils.getUnitGB(available))
        tv_storage_percent.setText(percent.toInt().toString() + "%")
        updateMemoryOrStorageImage(image_storage, percent.toInt())
        updateBtnBackGround(btn_clean_storage, percent.toInt())
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
        updateBtnBackGround(btn_clean_battery,easyBatteryMod.getBatteryPercentage())

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
        val bundle = Bundle()
        bundle.putString(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_one_key_speed))
        var intent = Intent(mContext, PhoneAccessActivity::class.java)
        intent.putExtras(bundle)
        mContext.startActivity(intent)
        mActivity.finish()
    }

    /**
     * 垃圾清理
     */
    private fun goCleanStorage() {
        startActivity(NowCleanActivity::class.java)
        mActivity.finish()

    }

    /**
     * 手机降温
     */
    private fun goCool() {
        ARouter.getInstance().build(RouteConstants.PHONE_COOLING_ACTIVITY).navigation()
        mActivity.finish()
    }

    /**
     * 电池优化
     */
    private fun goCleanBattery() {
        startActivity(PhoneSuperPowerActivity::class.java)
        mActivity.finish()
    }

    private fun startActivity(cls: Class<*>?) {
        var intent = Intent(mContext, cls)
        mContext.startActivity(intent)
    }


    /**
     * 更新按钮背景
     */
    private fun updateBtnBackGround(textView: AppCompatTextView, percent: Int) {
        if (inTheRange(percent, low)) {
            textView.setBackgroundResource(R.drawable.clear_btn_green_bg)
        } else if (inTheRange(percent, medium)) {
            textView.setBackgroundResource(R.drawable.clear_btn_yellow_bg)
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
        } else if (inTheRange(percent, medium)) {
            image.setImageResource(R.drawable.icon_memory_percent_medium)
        } else if (inTheRange(percent, high)) {
            image.setImageResource(R.drawable.icon_memory_percent_high)
        } else {
            image.setImageResource(R.drawable.icon_memory_percent_max)
        }
    }

    private fun updateCoolImage(temperature: Float) {
        if (temperature > 37) {
            image_temperature.setImageResource(R.drawable.icon_temperature_percent_high)
        } else {
            image_temperature.setImageResource(R.drawable.icon_temperature_percent_normal)
        }
    }

    private fun updateBtn(temperature: Float) {
        if (temperature > 37) {
            btn_clean_temperature.setBackgroundResource(R.drawable.clear_btn_red_bg)
        } else {
            btn_clean_temperature.setBackgroundResource(R.drawable.clear_btn_green_bg)
        }
    }


    private fun updateBatteryImage(percent: Int) {
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

    private fun inTheRange(percent: Int, range: Array<Int>): Boolean {
        return percent >= range[0] && percent <= range[1]
    }
}
