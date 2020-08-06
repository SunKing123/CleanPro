package com.xiaoniu.cleanking.ui.finish

import android.os.Bundle
import com.jess.arms.base.BaseActivity
import com.jess.arms.di.component.AppComponent
import com.xiaoniu.cleanking.ui.finish.contract.NewCleanFinishPlusContract
import com.xiaoniu.cleanking.ui.finish.model.RecrmdItemModel

/**
 * Created by xinxiaolong on 2020/8/4.
 * email：xinxiaolong123@foxmail.com
 */
public class NewCleanFinishPlusActivity :BaseActivity<NewCleanFinishPlusContract.CleanFinishPresenter>(),NewCleanFinishPlusContract.CleanFinishView {

    override fun setupActivityComponent(appComponent: AppComponent) {

    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initView(savedInstanceState: Bundle?): Int {
        TODO("Not yet implemented")
    }

    /**
     * 显示第一个推荐功能视图
     */
    override fun visibleRecommendViewFirst(item: RecrmdItemModel) {
        TODO("Not yet implemented")
    }

    /**
     * 显示第二个推荐功能视图
     */
    override fun visibleRecommendViewSecond(item: RecrmdItemModel) {
        TODO("Not yet implemented")
    }

    /**
     * 显示刮刮卡引导视图
     */
    override fun visibleScratchCardView() {
        TODO("Not yet implemented")
    }

    override fun showMessage(message: String) {
        TODO("Not yet implemented")
    }

}
