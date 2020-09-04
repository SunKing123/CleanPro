package com.xiaoniu.cleanking.ui.securitycenter

import android.view.View
import android.widget.FrameLayout
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.base.SimpleFragment
import com.xiaoniu.cleanking.ui.securitycenter.contract.SecurityHomeContract
import com.xiaoniu.cleanking.ui.securitycenter.model.FunctionBarDataStore
import com.xiaoniu.cleanking.ui.securitycenter.model.RecommendModel
import com.xiaoniu.cleanking.ui.securitycenter.presenter.SecurityHomePresenter
import com.xiaoniu.cleanking.ui.securitycenter.view.FunctionGridView.Companion.ITEM_ACCOUNT
import com.xiaoniu.cleanking.ui.securitycenter.view.FunctionGridView.Companion.ITEM_AUTO_KILL
import com.xiaoniu.cleanking.ui.securitycenter.view.FunctionGridView.Companion.ITEM_PAY
import com.xiaoniu.cleanking.ui.securitycenter.view.FunctionGridView.Companion.ITEM_SOFT
import com.xiaoniu.cleanking.ui.securitycenter.view.FunctionGridView.Companion.ITEM_VIRUS_UPDATE
import com.xiaoniu.cleanking.ui.securitycenter.view.FunctionGridView.Companion.ITEM_WIFI
import com.xiaoniu.cleanking.ui.securitycenter.view.FunctionGridView.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_security_home_tab_layout.*

/**
 * Created by xinxiaolong on 2020/8/25.
 * email：xinxiaolong123@foxmail.com
 */
class SecurityHomeFragment : SimpleFragment(), SecurityHomeContract.ISecurityHomeView {

    lateinit var presenter: SecurityHomeContract.ISecurityHomePresenter
    lateinit var functionBarData: FunctionBarDataStore
    lateinit var adOne: FrameLayout
    lateinit var adTwo: FrameLayout
    lateinit var adThree: FrameLayout

    companion object {
        //非单例
        fun getInstance(): SecurityHomeFragment {
            return SecurityHomeFragment()
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_security_home_tab_layout
    }

    override fun initView() {
        initData()
        findAdView()
        initBarListView()
        initEvent()
    }

    fun findAdView() {
        adOne = mView.findViewById(R.id.ad_one)
        adTwo = mView.findViewById(R.id.ad_two)
        adThree = mView.findViewById(R.id.ad_three)
    }

    fun initData() {
        presenter = SecurityHomePresenter(this)
        functionBarData = FunctionBarDataStore()
    }

    fun initBarListView() {
        bar_camera.setViewData(functionBarData.getCameraModel())
        bar_battery.setViewData(functionBarData.getBatteryModel())
        bar_red_packet.setViewData(functionBarData.getRedPacketModel())
    }

    fun initEvent() {
        recommend_bar.setOnClickListener({ recommendBarClick() })
        function_gridView.onItemClickListener = object : OnItemClickListener {
            override fun onClick(code: String) {
                functionGridViewClick(code)
            }
        }
    }

    fun recommendBarClick() {
        presenter.onRecommendBarClick()
    }

    fun functionGridViewClick(code: String) {
        when (code) {
            ITEM_ACCOUNT -> {

            }
            ITEM_PAY -> {

            }
            ITEM_AUTO_KILL -> {

            }
            ITEM_SOFT -> {

            }
            ITEM_WIFI -> {

            }
            ITEM_VIRUS_UPDATE -> {

            }
        }
    }

    override fun getPosition1AdvContainer(): FrameLayout {
       return adOne
    }

    override fun getPosition2AdvContainer(): FrameLayout {
        return adTwo
    }

    override fun getPosition3AdvContainer(): FrameLayout {
        return adThree
    }

    override fun setRecommendBarViewData(model: RecommendModel) {
        recommend_bar.initViewData(model)
    }

    override fun inVisibleRecommendBarView() {
        recommend_bar.visibility = View.INVISIBLE
    }


}
