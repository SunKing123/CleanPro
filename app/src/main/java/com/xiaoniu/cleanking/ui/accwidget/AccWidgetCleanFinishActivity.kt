package com.xiaoniu.cleanking.ui.accwidget

import android.os.Bundle
import android.view.View
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.base.BaseActivity
import com.xiaoniu.cleanking.base.BasePresenter
import com.xiaoniu.cleanking.midas.MidasRequesCenter
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.utils.NumberUtils
import kotlinx.android.synthetic.main.activity_acc_widget_clean_finish_layout.*

/**
 * Created by xinxiaolong on 2020/8/17.
 * email：xinxiaolong123@foxmail.com
 */
class AccWidgetCleanFinishActivity : BaseActivity<BasePresenter<*, *>>() {

    override fun inject(activityComponent: ActivityComponent?) {
        TODO("Not yet implemented")
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_acc_widget_clean_finish_layout
    }

    override fun initView() {
        var memoryLower = NumberUtils.mathRandomInt(10, 30)
        tv_cleaned_memory.text = "释放内存" + memoryLower + "%"

        var storageGarbage = NumberUtils.mathRandomInt(300, 800)
        tv_storage_garbage.text = storageGarbage.toString() + "MB"

        initEvent()
        loadAdv()
    }

    fun initEvent() {
        tv_goCleanStorage.setOnClickListener({ goToCleanStorage() })
    }

    fun goToCleanStorage() {

    }

    fun loadAdv() {
        MidasRequesCenter.requestAndShowAd(this, AppHolder.getInstance().getMidasAdId(PositionId.KEY_AD_PAGE_WIDGET_ACC_FINISH, PositionId.DRAW_ONE_CODE), object : SimpleViewCallBack(findViewById(R.id.ad_container)) {

        })
    }

    override fun netError() {

    }

}
