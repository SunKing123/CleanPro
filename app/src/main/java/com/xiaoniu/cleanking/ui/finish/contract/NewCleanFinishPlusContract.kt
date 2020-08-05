package com.xiaoniu.cleanking.ui.finish.contract

import android.widget.LinearLayout
import com.jess.arms.mvp.IModel
import com.jess.arms.mvp.IPresenter
import com.jess.arms.mvp.IView
import com.xiaoniu.cleanking.ui.finish.model.RecrmdItemModel

/**
 * Created by xinxiaolong on 2020/8/4.
 * email：xinxiaolong123@foxmail.com
 */
public interface NewCleanFinishPlusContract {

    interface CleanFinishView : IView {
        fun visibleRecommendViewOne(item: RecrmdItemModel)
        fun visibleRecommendViewTwo(item: RecrmdItemModel)
        fun visibleScratchCardView()
    }

    interface CleanFinishPresenter : IPresenter {
        fun loadOneAdv(advContainer: LinearLayout)
        fun loadTwoAdv(advContainer: LinearLayout)
    }

    interface LieModel : IModel


}
