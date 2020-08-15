package com.xiaoniu.cleanking.ui.deskpop.wifi

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.wifi.WifiInfo
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import com.xiaoniu.clean.deviceinfo.EasyNetworkMod
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.base.BaseActivity
import com.xiaoniu.cleanking.midas.MidasRequesCenter
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack
import com.xiaoniu.cleanking.ui.main.activity.NetWorkActivity
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.newclean.presenter.ExternalScenePresenter
import com.xiaoniu.cleanking.ui.newclean.util.StartFinishActivityUtil
import com.xiaoniu.cleanking.ui.viruskill.VirusKillActivity
import com.xiaoniu.cleanking.utils.ExtraConstant
import com.xiaoniu.cleanking.utils.NumberUtils
import com.xiaoniu.cleanking.utils.update.MmkvUtil
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat
import com.xiaoniu.common.utils.StatisticsUtils
import com.xiaoniu.unitionadbase.model.AdInfoModel
import kotlinx.android.synthetic.main.activity_external_scene.*
import kotlinx.android.synthetic.main.layout_desk_ad_contenter.*
import kotlinx.android.synthetic.main.view_scene_wifi.*

class ExternalSceneActivity : BaseActivity<ExternalScenePresenter>() {
    override fun inject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, resources.getColor(android.R.color.transparent, theme), true)
        } else {
            StatusBarCompat.setStatusBarColor(this, resources.getColor(android.R.color.transparent), false)
        }
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.addFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                //  or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val reset = MmkvUtil.getBool("isResetWiFi", false)
        PreferenceUtil.updatePopupWifi(reset)

    }

    override fun getLayoutId() = R.layout.activity_external_scene


    override fun initView() {
        StatisticsUtils.customTrackEvent("wifi_plug_screen_custom", "wifi插屏曝光", "wifi_plug_screen", "wifi_plug_screen")
        wifi_stub.inflate()
        showWifiScene()
        initAd()
        scene_close.setOnClickListener {
            StatisticsUtils.trackClick("close_click", "wifi插屏关闭按钮点击", "wifi_plug_screen", "wifi_plug_screen")
            finish()
        }
    }

    private fun startNetSpeed() {
        if (PreferenceUtil.getSpeedNetWorkTime()) {
            startActivity(NetWorkActivity::class.java)
        } else {
            val intent = Intent()
            val num = PreferenceUtil.getSpeedNetworkValue()
            intent.putExtra(ExtraConstant.TITLE, "网络加速")
            intent.putExtra("unused", true)
            StartFinishActivityUtil.gotoFinish(this,intent)
        }
    }

    private fun startKillVirusActivity() {
        if (PreferenceUtil.getVirusKillTime()) {
            startActivity(VirusKillActivity::class.java)
        } else {
            val intent = Intent()
            intent.putExtra(ExtraConstant.TITLE, "病毒查杀")
            intent.putExtra("unused", true)
            StartFinishActivityUtil.gotoFinish(this,intent)
        }
    }

    private fun showWifiScene() {
        scene_title.text = "WIFI已经连接"
        val easyNetworkMod = EasyNetworkMod(this)
        scene_button.text = "网络安全检测"
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
                scene_button.text = "网络加速"
            }
        }
        scene_button.setOnClickListener {
            StatisticsUtils.trackClick("wifi_plug_screen_button_click", "wifi插屏按钮点击", "wifi_plug_screen", "wifi_plug_screen")
            if (wifi_state.text.contains("弱")) {
                startNetSpeed()
            } else {
                startKillVirusActivity()
            }
            finish()
        }
        //名称
        wifi_name.text = easyNetworkMod.wifiSSID
        //开放状态
        wifi_open_type.text = if (easyNetworkMod.checkIsCurrentWifiHasPassword()) "隐私" else "开放"
        //网络速度
        wifi_net_speed.text = easyNetworkMod.wifiLinkSpeed
        if (easyNetworkMod.wifiLinkSpeed.contains(WifiInfo.LINK_SPEED_UNITS)) {
            val speed = easyNetworkMod.wifiLinkSpeed.replace("Mbps", "").trim().toInt()
            //下载速度
            wifi_download_speed.text = NumberUtils.mathRandom(speed / 2, speed).toString().plus("Mbps")
        } else {
            wifi_download_speed.text = "unKnow"
        }
        //网络延迟
        wifi_delay.text = NumberUtils.mathRandom(10, 60).toString().plus("ms")

    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun netError() {
    }


    /**
     * 广告展示
     */
    fun initAd() {
        if (isFinishing || !AppHolder.getInstance().checkAdSwitch(PositionId.KEY_PAGE_DESK_WIFI_AD))
            return
//            StatisticsUtils.customADRequest("ad_request", "广告请求", "1", " ", " ", "all_ad_request", "acceleration_page", "acceleration_page");
        MidasRequesCenter.requestAndShowAd(this, AppHolder.getInstance().getMidasAdId(PositionId.KEY_PAGE_DESK_WIFI_AD, PositionId.DRAW_ONE_CODE), object : SimpleViewCallBack(ad_container) {
            override fun onAdClick(adInfoModel: AdInfoModel) {
                super.onAdClick(adInfoModel)
                this@ExternalSceneActivity.finish()
            }
        })
    }
}
