package com.xiaoniu.cleanking.ui.main.activity

import com.geek.webpage.web.model.WebDialogManager
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.base.BaseActivity
import com.xiaoniu.cleanking.ui.main.presenter.MainPresenter
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.common.utils.StatusBarUtil
import java.util.*

/**
 * @author XiLei
 * @date 2019/11/19.
 * description：热启动红包
 */
class RedPacketHotActivity : BaseActivity<MainPresenter>(), WebDialogManager.FinishInterface {
    override fun getLayoutId(): Int {
        return R.layout.activity_hot_redpacket
    }

    override fun inject(activityComponent: ActivityComponent?) {
    }

    override fun initView() {
        StatusBarUtil.setTransparentForWindow(this)
        if (null == AppHolder.getInstance() || null == AppHolder.getInstance().redPacketEntityList
                || null == AppHolder.getInstance().redPacketEntityList.data
                || AppHolder.getInstance().redPacketEntityList.data.size <= 0
                || null == AppHolder.getInstance().redPacketEntityList.data[0].imgUrls
                || AppHolder.getInstance().redPacketEntityList.data[0].imgUrls.size <= 0)
            return
        val count: Int
        if (AppHolder.getInstance().redPacketEntityList.data[0].showType == 1) { //循环
            if (PreferenceUtil.getRedPacketShowTrigger() != AppHolder.getInstance().redPacketEntityList.data[0].trigger) {
                PreferenceUtil.saveRedPacketForCount(0)
            }
            PreferenceUtil.saveRedPacketShowTrigger(AppHolder.getInstance().redPacketEntityList.data[0].trigger)
            count = PreferenceUtil.getRedPacketForCount()
            if (count >= AppHolder.getInstance().redPacketEntityList.data[0].imgUrls.size - 1) {
                PreferenceUtil.saveRedPacketForCount(0)
            } else {
                PreferenceUtil.saveRedPacketForCount(PreferenceUtil.getRedPacketForCount() + 1)
            }
        } else { //随机
            if (AppHolder.getInstance().redPacketEntityList.data[0].imgUrls.size == 1) {
                count = 0
            } else {
                count = Random().nextInt(AppHolder.getInstance().redPacketEntityList.data[0].imgUrls.size - 1)
            }
        }
        if (!isFinishing()) {
            WebDialogManager.getInstance().showWebDialog(this, AppHolder.getInstance().redPacketEntityList.data[0].htmlUrl + AppHolder.getInstance().redPacketEntityList.data[0].imgUrls[count])
            WebDialogManager.getInstance().setFinishInterface(this)
        }
    }

    override fun netError() {
    }

    override fun finishActivity() {
        finish()
    }
}