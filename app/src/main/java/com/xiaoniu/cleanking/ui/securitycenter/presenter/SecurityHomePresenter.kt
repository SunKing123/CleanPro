package com.xiaoniu.cleanking.ui.securitycenter.presenter

import com.xiaoniu.cleanking.ui.securitycenter.contract.SecurityHomeContract
import com.xiaoniu.cleanking.ui.securitycenter.model.RecmedBarDataStore

/**
 * Created by xinxiaolong on 2020/9/4.
 * email：xinxiaolong123@foxmail.com
 */
class SecurityHomePresenter:SecurityHomeContract.ISecurityHomePresenter {

    var view: SecurityHomeContract.ISecurityHomeView
    var recmedData:RecmedBarDataStore;
    constructor(view: SecurityHomeContract.ISecurityHomeView){
        this.view=view
        recmedData=RecmedBarDataStore.getInstance()
    }

    override fun onCreate() {

    }

    override fun onResume() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }

    override fun onRecommendBarClick() {
        var model=recmedData.pop()
        if(model!=null){
            view.setRecommendBarViewData(model)
        }else{
            view.inVisibleRecommendBarView()
        }
    }
}
