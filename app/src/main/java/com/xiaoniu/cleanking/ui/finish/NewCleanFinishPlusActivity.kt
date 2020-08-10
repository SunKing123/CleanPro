package com.xiaoniu.cleanking.ui.finish

import android.app.Activity
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
import com.xiaoniu.cleanking.midas.MidasConstants
import com.xiaoniu.cleanking.ui.finish.contract.NewCleanFinishPlusContract
import com.xiaoniu.cleanking.ui.finish.model.CleanFinishPointer
import com.xiaoniu.cleanking.ui.finish.model.RecmedItemDataStore
import com.xiaoniu.cleanking.ui.finish.model.RecmedItemModel
import com.xiaoniu.cleanking.ui.finish.presenter.CleanFinishPlusPresenter
import com.xiaoniu.cleanking.ui.main.activity.MainActivity
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity
import com.xiaoniu.cleanking.ui.main.bean.BubbleCollected
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
import com.xiaoniu.cleanking.utils.ExtraConstant
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.cleanking.widget.FinishCardView
import com.xiaoniu.common.utils.DisplayUtils
import com.xiaoniu.common.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_new_clean_finish_plus_layout.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by xinxiaolong on 2020/8/4.
 * email：xinxiaolong123@foxmail.com
 */
class NewCleanFinishPlusActivity : BaseActivity<CleanFinishPlusPresenter>(), NewCleanFinishPlusContract.CleanFinishView {

    var titleName: String = ""
    lateinit var pointer: CleanFinishPointer
    lateinit var newIntent: Intent
    var isFirst=true

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
        titleName = newIntent.getStringExtra("title")
        pointer = CleanFinishPointer(titleName)
        restView()
        initTitle()
        initHeadView()
        initEvent()
        mPresenter.loadRecommendData()
        loadAdv()
    }

    fun restView() {
        card_1.visibility = View.GONE
        card_2.visibility = View.GONE
        isFirst=true
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

    private fun initHeadView() {
        var num = intent.getStringExtra(ExtraConstant.NUM)
        when (titleName) {
            "建议清理", "立即清理", "一键清理" -> showSuggestClearView()
            "一键加速" -> showOneKeySpeedUp()
            "病毒查杀" -> showKillVirusView()
            "超强省电" -> showPowerSaving()
            "微信专清" -> showWeiXinClear()
            "手机降温" -> showPhoneCold()
            "通知栏清理" -> showNotificationClear()
            "网络加速" -> showNetSpeedUp()
            "手机清理" -> showPhoneClear()
        }
    }

    //建议清理
    private fun showSuggestClearView() {
        var storage = PreferenceUtil.getCleanStorageNum().split(":")
        var num = storage[0]
        var unit = storage[1]
        function_icon.setImageResource(R.mipmap.finish_icon_ok)
        val content = AndroidUtil.zoomText(num.plus(unit), 2f, 0, num.length)
        function_title.text = content
        function_sub_title.text = "垃圾已清理"
        function_sub_title.textSize=10F
    }

    //一键加速
    private fun showOneKeySpeedUp() {
        var num = PreferenceUtil.getOneKeySpeedNum()
        function_icon.setImageResource(R.mipmap.finish_icon_speedup)
        val content = "运行速度已提升$num%"
        val spannableString = SpannableString(content)
        val styleSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(styleSpan, content.length - 1 - num.length, content.length - 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        function_title.text = spannableString
        function_sub_title.text = "快试试其他功能吧！"
    }

    //病毒查杀
    private fun showKillVirusView() {
        function_icon.setImageResource(R.mipmap.finish_icon_virus)
        function_sub_title.visibility = View.GONE
        function_title.text = "安全，已经解决所有风险"
    }

    //超强省电
    private fun showPowerSaving() {
        function_icon.setImageResource(R.mipmap.finish_icon_power)
        function_title.text = "已达到最佳状态"
        function_sub_title.text = "快去体验其他功能"
    }

    //微信清理
    private fun showWeiXinClear() {
        function_icon.setImageResource(R.mipmap.finish_icon_weixin)
        function_title.text = "已清理"
        function_sub_title.text = "快试试其他功能吧！"
    }

    //手机降温
    private fun showPhoneCold() {
        var num = PreferenceUtil.getCleanCoolNum().toString()
        var time = "60s"
        function_icon.setImageResource(R.mipmap.finish_icon_cold)
        val content = "成功降温$num°C"
        val spannableString = SpannableString(content)
        val styleSpan = StyleSpan(Typeface.BOLD)
        spannableString.setSpan(styleSpan, content.indexOf(num), content.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        function_title.text = spannableString
        val subContent = "${time}后达到最佳降温效果"
        val subSpannableString = SpannableString(subContent)
        subSpannableString.setSpan(styleSpan, 0, subContent.indexOf("s"), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        function_sub_title.text = subSpannableString
    }

    //通知栏清理
    private fun showNotificationClear() {
        function_icon.setImageResource(R.mipmap.finish_icon_notification)
        function_title.text = "通知栏很干净"
        function_sub_title.text = "快去体验其他清理功能"
    }

    //网络加速
    private fun showNetSpeedUp() {
        var num = PreferenceUtil.getSpeedNetworkValue()
        function_icon.setImageResource(R.mipmap.finish_icon_ok)
        val content = num.plus("%")
        val spannableString = SpannableString(content)
        val sizeSpan = AbsoluteSizeSpan(DisplayUtils.sp2px(this, 30F))
        spannableString.setSpan(sizeSpan, 0, num.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        function_title.text = spannableString
        function_sub_title.text = "网络已提速"
        function_sub_title.textSize = 10F
    }

    //手机清理
    private fun showPhoneClear() {
        function_icon.setImageResource(R.mipmap.finish_icon_ok)
        function_title.text = "已达到最佳状态"
        function_sub_title.text = "快去体验其他清理功能"
    }

    override fun netError() {

    }

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
        view.visibility = View.VISIBLE
        view.setImage(item.imageIcon)
        view.setLeftTitle(item.title)
        view.setSubTitle1(item.content1)
        view.setSubTitle2(item.content2)
        view.setButtonText(item.buttonText)
        if (item.title.equals("手机加速")) {
            view.setImageLabelVisible()
            view.setImageLabel(RecmedItemDataStore.getInstance().memory)
        } else {
            view.setImageLabelHide()
        }
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

    /**
     * 金币弹框
     */
    override fun showGoldCoinDialog(bubbleCollected: BubbleCollected) {
        val bean = GoldCoinDialogParameter()
        bean.dialogType = 3
        bean.obtainCoinCount = bubbleCollected.data.goldCount
        bean.totalCoinCount = bubbleCollected.data.totalGoldCount.toDouble()
        bean.adId = MidasConstants.FINISH_GET_GOLD_COIN
        bean.context = this
        bean.isRewardOpen = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_GOLD_DIALOG_SHOW_VIDEO)
        bean.closeClickListener = View.OnClickListener { view: View? -> pointer.goldCoinClose() }
        bean.onDoubleClickListener = View.OnClickListener { v: View? ->
            if (AndroidUtil.isFastDoubleBtnClick(1000)) {
                return@OnClickListener
            }
            pointer.goldCoinDoubleClick()
            mPresenter.loadVideoAdv(bubbleCollected)
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
        return this.titleName
    }

    override fun onPostResume() {
        super.onPostResume()
        pointer.exposurePoint()

        val unused = newIntent.getBooleanExtra("unused", false)
        //真正使用过功能才请求弹框
        if (!unused&&isFirst) {
            isFirst=false
            //插屏广告滞后请求，处理友盟bug
            mPresenter.loadPopView()
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

    /**
     * 一键清理
     */
    fun startClean() {
        RecmedItemDataStore.getInstance().click_clean=true
        var intent=Intent(this,NowCleanActivity::class.java)
        intent.putExtra("fromRecommend",true)
        startActivity(intent)
        finish()
    }

    /**
     * 病毒查杀
     */
    fun startVirus() {
        RecmedItemDataStore.getInstance().click_virus=true
        startActivity(VirusKillActivity::class.java)
        finish()
    }

    /**
     * 一键加速
     */
    fun startAcc() {
        RecmedItemDataStore.getInstance().click_acc=true
        val bundle = Bundle()
        bundle.putString(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_one_key_speed))
        startActivity(PhoneAccessActivity::class.java, bundle)
        finish()
    }

    /**
     * 超强省电
     */
    fun startPower() {
        RecmedItemDataStore.getInstance().click_power=true
        startActivity(PhoneSuperPowerActivity::class.java)
        finish()
    }

    /**
     * 微信清理
     */
    fun startWxClean() {
        RecmedItemDataStore.getInstance().click_wx=true
        startActivity(WechatCleanHomeActivity::class.java)
        finish()
    }

    /**
     * 手机降温
     */
    fun startCool() {
        RecmedItemDataStore.getInstance().click_cool=true
        startActivity(RouteConstants.PHONE_COOLING_ACTIVITY)
        finish()
    }

    /**
     * 通知栏清理
     */
    fun startNotify() {
        RecmedItemDataStore.getInstance().click_notify=true
        NotifyCleanManager.startNotificationCleanActivity(getActivity(), 0)
        finish()
    }

    /**
     * 刮刮卡列表
     */
    fun startScratch() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("type", "huodong")
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        jumpMainPage()
    }

    private fun jumpMainPage() {
        EventBus.getDefault().post(FromHomeCleanFinishEvent(titleName))
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("back_from_finish", true)
        startActivity(intent)
        finish()
    }

}
