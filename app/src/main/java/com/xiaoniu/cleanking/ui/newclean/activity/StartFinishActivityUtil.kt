package com.xiaoniu.cleanking.ui.newclean.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import com.comm.jksdk.GeekAdSdk
import com.comm.jksdk.ad.listener.AdManager
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.base.BaseActivity
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.main.presenter.ScreenFinishBeforPresenter
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager
import com.xiaoniu.cleanking.utils.ExtraConstant
import com.xiaoniu.cleanking.utils.FileQueryUtils
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.common.utils.NetworkUtils
import java.util.*

/**
 * @author XiLei
 * @date 2019/11/22.
 * description：完成页之前的全屏视频广告
 */
class ScreenFinishBeforActivity {


    var isOneOpen=false
    var isTwoOpen=false
    var isThreeOpen=false

    fun gotoFinish(context:Context){
        initOnOff();
        if(isOneOpen||isTwoOpen){
            val bundle = Bundle()
            bundle.putString("title", mTitle)
            bundle.putString("num", mNum)
            bundle.putString("unit", mUnit)
            bundle.putBoolean("main", getIntent().getBooleanExtra("main", false))
            startActivity(NewCleanFinishActivity::class.java, bundle)
        }else{
            val bundle = Bundle()
            bundle.putString("title", mTitle)
            bundle.putBoolean("main", getIntent().getBooleanExtra("main", false))
            startActivity(CleanFinishAdvertisementActivity::class.java, bundle)
        }
    }

    fun initOnOff(){
        if(AppHolder.getInstance().getSwitchInfoList()!=null&& AppHolder.getInstance().getSwitchInfoList().getData()!=null&& AppHolder.getInstance().switchInfoList.data.size > 0){
            for (switchInfoList in AppHolder.getInstance().switchInfoList.data) {
                if (PositionId.KEY_AD_PAGE_FINISH == switchInfoList.configKey && PositionId.DRAW_ONE_CODE == switchInfoList.advertPosition) {
                    isOneOpen = switchInfoList.isOpen
                }
                if (PositionId.KEY_AD_PAGE_FINISH == switchInfoList.configKey && PositionId.DRAW_TWO_CODE == switchInfoList.advertPosition) {
                    isTwoOpen = switchInfoList.isOpen
                }
                if (PositionId.KEY_AD_PAGE_FINISH == switchInfoList.configKey && PositionId.DRAW_THREE_CODE == switchInfoList.advertPosition) {
                    isThreeOpen = switchInfoList.isOpen
                }
            }
        }
    }


}
