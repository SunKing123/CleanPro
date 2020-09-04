package com.xiaoniu.cleanking.ui.securitycenter.contract

import android.widget.FrameLayout
import com.xiaoniu.cleanking.ui.securitycenter.model.RecommendModel

/**
 * Created by xinxiaolong on 2020/8/25.
 * email：xinxiaolong123@foxmail.com
 */
interface SecurityHomeContract {

    interface ISecurityHomeView {

        //获取广告位1的容器
        fun getPosition1AdvContainer(): FrameLayout
        //获取广告位2的容器
        fun getPosition2AdvContainer(): FrameLayout
        //获取广告位3的容器
        fun getPosition3AdvContainer(): FrameLayout
        //填充推荐功能数据
        fun setRecommendBarViewData(model: RecommendModel)
        //隐藏推荐功能布局
        fun inVisibleRecommendBarView()

    }

    interface ISecurityHomePresenter {

        fun onCreate()
        fun onResume()
        fun onDestroy()

        fun onRecommendBarClick()

    }

}
