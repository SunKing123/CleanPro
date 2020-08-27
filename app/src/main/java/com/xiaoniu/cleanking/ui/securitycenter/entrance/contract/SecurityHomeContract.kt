package com.xiaoniu.cleanking.ui.securitycenter.entrance.contract

import android.widget.FrameLayout

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

    }

}
