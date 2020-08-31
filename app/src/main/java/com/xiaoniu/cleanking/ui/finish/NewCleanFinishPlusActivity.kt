package com.xiaoniu.cleanking.ui.finish

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.view.KeyEvent
import android.view.View
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.base.BaseActivity
import com.xiaoniu.cleanking.constant.RouteConstants
import com.xiaoniu.cleanking.ui.finish.base.CleanFinishLogger
import com.xiaoniu.cleanking.ui.finish.contract.NewCleanFinishPlusContract
import com.xiaoniu.cleanking.ui.finish.model.CleanFinishPointer
import com.xiaoniu.cleanking.ui.finish.model.RecmedItemDataStore
import com.xiaoniu.cleanking.ui.finish.model.RecmedItemModel
import com.xiaoniu.cleanking.ui.finish.presenter.CleanFinishPlusPresenter
import com.xiaoniu.cleanking.ui.main.activity.MainActivity
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity
import com.xiaoniu.cleanking.ui.main.bean.BubbleCollected
import com.xiaoniu.cleanking.ui.main.bean.InsertAdSwitchInfoList
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity
import com.xiaoniu.cleanking.ui.newclean.bean.GoldCoinDialogParameter
import com.xiaoniu.cleanking.ui.newclean.dialog.GoldCoinDialog
import com.xiaoniu.cleanking.ui.tool.notify.event.FromHomeCleanFinishEvent
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity
import com.xiaoniu.cleanking.ui.viruskill.VirusKillActivity
import com.xiaoniu.cleanking.utils.AndroidUtil
import com.xiaoniu.cleanking.utils.AppLifecycleUtil
import com.xiaoniu.cleanking.utils.ExtraConstant
import com.xiaoniu.cleanking.utils.LogUtils
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.cleanking.ui.finish.view.FinishCardView
import com.xiaoniu.common.utils.DisplayUtils
import com.xiaoniu.common.utils.StatusBarUtil
import com.xiaoniu.common.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_new_clean_finish_plus_layout.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by xinxiaolong on 2020/8/4.
 * email：xinxiaolong123@foxmail.com
 */
class NewCleanFinishPlusActivity : BaseActivity<CleanFinishPlusPresenter>(), NewCleanFinishPlusContract.CleanFinishView {

    //这里先允许为空，因为intent里获取有时会为null,导致程序崩溃。
    var titleName: String? = ""
    lateinit var pointer: CleanFinishPointer
    lateinit var newIntent: Intent
    var isFirst = true
    var isDailyTask = false

    companion object {
        fun start(context: Context,title:String,used:Boolean) {
            var intent = Intent(context, NewCleanFinishPlusActivity::class.java);
            intent.putExtra(ExtraConstant.TITLE, title)
            intent.putExtra(ExtraConstant.USED, used)
            context.startActivity(intent)
        }

        fun start(context: Context,intent:Intent,title:String,used:Boolean) {
            intent.putExtra(ExtraConstant.TITLE, title)
            intent.putExtra(ExtraConstant.USED, used)
            intent.setClass(context, NewCleanFinishPlusActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_new_clean_finish_plus_layout
    }

    override fun inject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        newIntent = intent!!
        initView()
    }

    override fun onViewCreated() {
        super.onViewCreated()
        mPresenter.onCreate()
        StatusBarUtil.setTransparentForWindow(this)
        newIntent = intent
    }

    override fun initView() {
        titleName = newIntent.getStringExtra(ExtraConstant.TITLE)
        //有时候intent传进的title为空了，费解！
        //bug描述：https://mobile.umeng.com/platform/5dcb9de5570df3121b000fbe/error_analysis/list/detail/3328618714190
        if (titleName == null) {
            titleName = "一键加速"
        }
        pointer = CleanFinishPointer(titleName!!)
        restView()
        initTitle()
        initHeadView()
        initEvent()
        // 先装载推荐卡片布局，再加载广告。
        // 因为推荐卡片布局数量影响广告加载逻辑
        mPresenter.loadRecommendData()
        loadAdv()

        pointer.exposurePoint()

    }

    fun restView() {
        card_1.visibility = View.GONE
        card_2.visibility = View.GONE
        isFirst = true
    }

    private fun initEvent() {
        left_title.setOnClickListener {
            pointer.returnPoint()
            pointer.insertAdvRequest5()
            onBackPressed()
        }
        finish_card.setOnClickListener({ startScratch() })
    }

    private fun loadAdv() {
        mPresenter.loadOneAdv(findViewById(R.id.ad_container_1))
        mPresenter.loadTwoAdv(findViewById(R.id.ad_container_2))
    }

    private fun initTitle() {
        left_title.text = titleName
    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************init head view data**************************************************************************
     *********************************************************************************************************************************************************
    */
    private fun initHeadView() {
        finish_headView.initViewData(titleName!!)
    }

    override fun netError() {

    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************init recommend card view*********************************************************************
     *********************************************************************************************************************************************************
    */

    /**
     * 显示第一个推荐功能视图
     */
    override fun visibleRecommendViewFirst(item: RecmedItemModel) {
        setRecommendViewData(card_1, item)
    }

    /**
     * 显示第二个推荐功能视图
     */
    override fun visibleRecommendViewSecond(item: RecmedItemModel) {
        setRecommendViewData(card_2, item)
    }

    fun setRecommendViewData(view: FinishCardView, item: RecmedItemModel) {
        view.initViewData(item)
        view.setOnClickListener({ onRecommendViewClick(item.title) })
    }

    fun onRecommendViewClick(title: String) {
        pointer.recommendClickPoint(title)
        when (title) {
            "垃圾文件过多" -> startClean()
            "手机加速" -> startAcc()
            "病毒查杀" -> startVirus()
            "超强省电" -> startPower()
            "微信清理" -> startWxClean()
            "手机降温" -> startCool()
            "通知栏清理" -> startNotify()
        }
        finish()
    }

    /**
     * 显示刮刮卡引导视图
     */
    override fun visibleScratchCardView() {
        finish_card.visibility = View.VISIBLE
    }

    /**
     * 显示刮刮卡引导视图
     */
    override fun goneScratchCardView() {
        finish_card.visibility = View.GONE
    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************gold coin dialog*****************************************************************************
     *********************************************************************************************************************************************************
    */
    /**
     * 金币弹框
     */
    override fun showGoldCoinDialog(bubbleCollected: BubbleCollected, isTask:Boolean) {
        isDailyTask = isTask
        val bean = GoldCoinDialogParameter()
        bean.dialogType = 3
        bean.obtainCoinCount = bubbleCollected.data.goldCount
        bean.totalCoinCount = bubbleCollected.data.totalGoldCount.toDouble()
        if (AppHolder.getInstance().checkAdSwitch(PositionId.KEY_FINISH_GET_GOLD_COIN)) {
            bean.adId = AppHolder.getInstance().getMidasAdId(PositionId.KEY_FINISH_GET_GOLD_COIN, PositionId.DRAW_ONE_CODE)
        }
        bean.context = this
        bean.isRewardOpen = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_GOLD_DIALOG_SHOW_VIDEO)
        bean.closeClickListener = View.OnClickListener { view: View? -> pointer.goldCoinClose() }
        bean.onDoubleClickListener = View.OnClickListener { v: View? ->
            if (AndroidUtil.isFastDoubleBtnClick(1000)) {
                return@OnClickListener
            }
            pointer.goldCoinDoubleClick()
            mPresenter.loadVideoAdv(bubbleCollected,isTask)
        }
        pointer.goldCoinDialogExposure()
        pointer.goldCoinRequestAdv1()
        GoldCoinDialog.showGoldCoinDialog(bean)
    }

    override fun dismissGoldCoinDialog() {
        GoldCoinDialog.dismiss()
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun getFunctionTitle(): String {
        if (titleName == null) {
            titleName = "一键加速"
        }
        return this.titleName!!
    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************recommend card start activity****************************************************************
     *********************************************************************************************************************************************************
    */
    /**
     * 一键清理
     */
    fun startClean() {
        RecmedItemDataStore.getInstance().click_clean = true
        var intent = Intent(this, NowCleanActivity::class.java)
        intent.putExtra("fromRecommend", true)
        startActivity(intent)
    }

    /**
     * 病毒查杀
     */
    fun startVirus() {
        RecmedItemDataStore.getInstance().click_virus = true
        startActivity(VirusKillActivity::class.java)
    }

    /**
     * 一键加速
     */
    fun startAcc() {
        RecmedItemDataStore.getInstance().click_acc = true
        val bundle = Bundle()
        bundle.putString(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_one_key_speed))
        startActivity(PhoneAccessActivity::class.java, bundle)
    }

    /**
     * 超强省电
     */
    fun startPower() {
        RecmedItemDataStore.getInstance().click_power = true
        startActivity(PhoneSuperPowerActivity::class.java)
    }

    /**
     * 微信清理
     */
    fun startWxClean() {
        if (!AndroidUtil.isInstallWeiXin(this)) {
            ToastUtils.showShort(R.string.tool_no_install_chat)
            return
        }
        RecmedItemDataStore.getInstance().click_wx = true
        startActivity(WechatCleanHomeActivity::class.java)
    }

    /**
     * 手机降温
     */
    fun startCool() {
        RecmedItemDataStore.getInstance().click_cool = true
        startActivity(RouteConstants.PHONE_COOLING_ACTIVITY)
    }

    /**
     * 通知栏清理
     */
    fun startNotify() {
        RecmedItemDataStore.getInstance().click_notify = true
        NotifyCleanManager.startNotificationCleanActivity(getActivity(), 0)
    }

    /**
     * 刮刮卡列表
     */
    fun startScratch() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("type", "huodong")
        startActivity(intent)
    }


    fun jumpMainPage() {
        EventBus.getDefault().post(FromHomeCleanFinishEvent(titleName))
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("back_from_finish", true)
        startActivity(intent)
        finish()
    }

    /*
     *********************************************************************************************************************************************************
     ************************************************************activity lifecycle***************************************************************************
     *********************************************************************************************************************************************************
    */

    override fun onBackPressed() {
        super.onBackPressed()
        if(!isDailyTask){
            jumpMainPage()
        }

    }

    override fun onPostResume() {
        super.onPostResume()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && isFirst) {
            isFirst = false
            loadPopView()
        }
    }

    private fun loadPopView() {
        val used = newIntent.getBooleanExtra(ExtraConstant.USED, false)
        val config: InsertAdSwitchInfoList.DataBean? = AppHolder.getInstance().getInsertAdInfo(PositionId.KEY_FINISH_INSIDE_SCREEN)
        config?.let {
            CleanFinishLogger.log("isOpen=" + it.isOpen)
            if (it.isOpen) {
                mPresenter.loadInsideScreenDialog()
            } else if (used) {
                mPresenter.loadGoldCoinDialog()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mPresenter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            pointer.systemReturnPoint()
            pointer.insertAdvRequest5()
        }
        return super.onKeyDown(keyCode, event)
    }
}
