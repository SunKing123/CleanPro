package com.xiaoniu.cleanking.ui.finish.contract

import android.app.Activity
import android.widget.LinearLayout
import com.jess.arms.mvp.IModel
import com.xiaoniu.cleanking.base.BaseModel
import com.xiaoniu.cleanking.base.BaseView
import com.xiaoniu.cleanking.ui.finish.model.RecmedItemModel
import com.xiaoniu.cleanking.ui.main.bean.BubbleCollected

/**
 * Created by xinxiaolong on 2020/8/4.
 * email：xinxiaolong123@foxmail.com
 */
public interface NewCleanFinishPlusContract {

    interface CleanFinishView : BaseView {
        fun visibleRecommendViewFirst(item: RecmedItemModel)
        fun visibleRecommendViewSecond(item: RecmedItemModel)
        fun visibleScratchCardView()
        fun showGoldCoinDialog(bubbleCollected: BubbleCollected)
        fun dismissGoldCoinDialog()
        fun getActivity():Activity
        fun getFunctionTitle():String
    }

    interface CleanFinishPresenter<T : BaseView, V : BaseModel> : com.xiaoniu.cleanking.base.BasePresenter<T,V> {
        fun onCreate()
        fun loadOneAdv(advContainer: LinearLayout)
        fun loadTwoAdv(advContainer: LinearLayout)
        fun addDoubleGoldCoin(bubbleCollected: BubbleCollected)
        fun onPostResume()
        fun onPause()
    }

    interface LieModel : IModel


}
