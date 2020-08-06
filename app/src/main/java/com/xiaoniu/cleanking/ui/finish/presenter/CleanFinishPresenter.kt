package com.xiaoniu.cleanking.ui.finish.presenter

import android.widget.LinearLayout
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.ui.finish.contract.NewCleanFinishPlusContract
import com.xiaoniu.cleanking.ui.finish.contract.NewCleanFinishPlusContract.CleanFinishView
import com.xiaoniu.cleanking.ui.finish.model.RecrmdItemDataStore
import com.xiaoniu.cleanking.ui.main.config.PositionId

/**
 * Created by xinxiaolong on 2020/8/5.
 * email：xinxiaolong123@foxmail.com
 */
public class CleanFinishPresenter : NewCleanFinishPlusContract.CleanFinishPresenter {

    private var view: CleanFinishView
    private var itemDataStore: RecrmdItemDataStore
    private var isOpenOne = false
    private var isOpenTwo = false

    constructor(view: CleanFinishView) {
        this.view = view
        this.itemDataStore = RecrmdItemDataStore.getInstance()
    }

    override fun onStart() {
        initAdSwitch()

        var firstModel = itemDataStore.popModel()

        if (firstModel != null) {
            view.visibleRecommendViewFirst(firstModel)
        }

        var secondModel = itemDataStore.popModel()
        if (secondModel != null) {
            view.visibleRecommendViewSecond(secondModel)
        }

        if (secondModel == null) {
            view.visibleScratchCardView()
        }
    }

    private fun initAdSwitch() {
        isOpenOne = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_FINISH, PositionId.DRAW_ONE_CODE)
        isOpenTwo = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_FINISH, PositionId.DRAW_TWO_CODE)
    }

    override fun loadOneAdv(advContainer: LinearLayout) {
        if (!isOpenOne) return

    }

    override fun loadTwoAdv(advContainer: LinearLayout) {
        if (!isOpenTwo) return
    }

    override fun onDestroy() {

    }
}
