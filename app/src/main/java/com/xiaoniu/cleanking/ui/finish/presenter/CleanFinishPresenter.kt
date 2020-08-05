package com.xiaoniu.cleanking.ui.finish.presenter

import android.widget.LinearLayout
import com.xiaoniu.cleanking.ui.finish.contract.NewCleanFinishPlusContract
import com.xiaoniu.cleanking.ui.finish.contract.NewCleanFinishPlusContract.CleanFinishView;
import com.xiaoniu.cleanking.ui.finish.model.RecrmdItemDataStore
import com.xiaoniu.cleanking.ui.finish.model.RecrmdItemModel

/**
 * Created by xinxiaolong on 2020/8/5.
 * email：xinxiaolong123@foxmail.com
 */
public class CleanFinishPresenter : NewCleanFinishPlusContract.CleanFinishPresenter {

    private var view: CleanFinishView
    private var itemDataStore:RecrmdItemDataStore

    constructor(view: CleanFinishView) {
        this.view=view
        this.itemDataStore= RecrmdItemDataStore.getInstance()
    }

    override fun loadOneAdv(advContainer: LinearLayout) {

    }

    override fun loadTwoAdv(advContainer: LinearLayout) {

    }

    override fun onStart() {

        var firstModel= itemDataStore.popModel()

        if(firstModel!=null){
            view.visibleRecommendViewFirst(firstModel)
        }

        var secondModel= itemDataStore.popModel()
        if(secondModel!=null){
            view.visibleRecommendViewSecond(secondModel)
        }

        if(secondModel==null){
            view.visibleScratchCardView()
        }
    }

    override fun onDestroy() {

    }
}
