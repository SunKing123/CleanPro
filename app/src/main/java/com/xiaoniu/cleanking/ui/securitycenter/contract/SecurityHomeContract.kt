package com.xiaoniu.cleanking.ui.securitycenter.contract

import android.widget.FrameLayout
import com.xiaoniu.cleanking.ui.securitycenter.model.RecommendModel

/**
 * Created by xinxiaolong on 2020/8/25.
 * email：xinxiaolong123@foxmail.com
 */
interface SecurityHomeContract {

    interface SecurityHomeView {

        fun getPosition1AdvContainer(): FrameLayout
        fun getPosition2AdvContainer(): FrameLayout
        fun getPosition3AdvContainer(): FrameLayout
    }

    interface SecurityHomePresenter {

        //填充推荐功能数据
        fun fillRecommendBarViewData(model:RecommendModel)
        //隐藏推荐功能布局
        fun inVisibleRecommendBarView()


    }

}
