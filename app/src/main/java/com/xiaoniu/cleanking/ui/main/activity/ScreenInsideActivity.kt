package com.xiaoniu.cleanking.ui.main.activity

import android.util.Log
import com.xiaoniu.cleanking.BuildConfig
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent
import com.xiaoniu.cleanking.base.BaseActivity
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.main.presenter.MainPresenter
import com.xiaoniu.common.utils.StatisticsUtils
import com.xiaoniu.common.utils.StatusBarUtil
import com.xiaoniu.common.utils.ToastUtils

/**
 * @author XiLei
 * @date 2019/12/2.
 * description：内部插屏广告
 */
@Deprecated(message="插屏广告请求删除，界面容器暂时保存")
class ScreenInsideActivity : BaseActivity<MainPresenter>() {

    private val TAG = "GeekSdk"

    override fun getLayoutId(): Int {
        return R.layout.activity_hot_redpacket
    }

    override fun inject(activityComponent: ActivityComponent?) {
    }

    override fun initView() {
        StatusBarUtil.setTransparentForWindow(this)
        StatisticsUtils.customTrackEvent("ad_vue_custom", "内部插屏广告vue创建", "hot_splash_page", "inside_advertising_ad_page")
        finish()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isFinishing) {

        }
    }

    override fun onStop() {
        super.onStop()
        if (!isFinishing) {
            finish()
        }
    }

    override fun netError() {
    }


}
