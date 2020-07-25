package com.xiaoniu.cleanking.ui.newclean.presenter

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.xiaoniu.cleanking.base.RxPresenter
import com.xiaoniu.cleanking.ui.main.model.MainModel
import com.xiaoniu.cleanking.ui.newclean.activity.SpeedUpResultActivity
import javax.inject.Inject

class ExternalScenePresenter @Inject constructor(activity: RxAppCompatActivity) : RxPresenter<SpeedUpResultActivity, MainModel>() {
    var mActivity: RxAppCompatActivity = activity
}