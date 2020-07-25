package com.xiaoniu.cleanking.ui.newclean.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.xiaoniu.clean.deviceinfo.EasyNetworkMod
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.getStringExtraNotNull
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent
import com.xiaoniu.cleanking.base.BaseActivity
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector
import com.xiaoniu.cleanking.ui.localpush.PopPushActivity
import com.xiaoniu.cleanking.ui.newclean.presenter.ExternalScenePresenter
import com.xiaoniu.cleanking.utils.NumberUtils
import kotlinx.android.synthetic.main.activity_external_scene.*
import kotlinx.android.synthetic.main.view_scene_wifi.*

class ExternalSceneActivity : BaseActivity<ExternalScenePresenter>() {
    override fun inject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun getLayoutId() = R.layout.activity_external_scene


    companion object {
        const val SCENE = "scene"
        const val SCENE_WIFI = "scene_wifi"
        const val SCENE_PHONE_STATE = "scene_phone_state"
        const val SCENE_CHARGE = "scene_charge"
    }

    //当前弹窗场景
    private var mCurrentScene = ""

    override fun initView() {
        mCurrentScene = intent.getStringExtraNotNull(SCENE)
        when (mCurrentScene) {
            SCENE_WIFI -> {//wifi
                wifi_stub.inflate()
                showWifiScene()
            }
            SCENE_PHONE_STATE -> {//手机状态

            }
            SCENE_CHARGE -> {//充电

            }
        }
        scene_close.setOnClickListener { finish() }
    }

    private fun showWifiScene() {
        scene_title.text = "WIFI已经连接"
        scene_button.text = "网络加速"
        val easyNetworkMod = EasyNetworkMod(this)
        //wifi图标
        when (easyNetworkMod.checkWifiState()) {
            1 -> {
                wifi_image.setImageResource(R.mipmap.wifi_high)
                wifi_state.text = "强"
            }
            2 -> {
                wifi_image.setImageResource(R.mipmap.wifi_middle)
                wifi_state.text = "中"
            }
            3 -> {
                wifi_image.setImageResource(R.mipmap.wifi_low)
                wifi_state.text = "弱"
            }
        }
        //名称
        wifi_name.text = easyNetworkMod.wifiSSID
        //开放状态
        wifi_open_type.text = if (easyNetworkMod.checkIsCurrentWifiHasPassword()) "隐私" else "开放"
        //网络速度
        wifi_net_speed.text = easyNetworkMod.wifiLinkSpeed
        if (easyNetworkMod.wifiLinkSpeed.contains("Mbps")) {
            val speed = easyNetworkMod.wifiLinkSpeed.replace("Mbps", "").trim().toInt()
            //下载速度
            wifi_download_speed.text = NumberUtils.mathRandom(speed / 2, speed).toString().plus("Mbps")
        } else {
            wifi_download_speed.text = "unKnow"
        }
        //网络延迟
        wifi_delay.text = NumberUtils.mathRandom(10, 60).toString().plus("ms")

    }


    override fun netError() {
    }
}