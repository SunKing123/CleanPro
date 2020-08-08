package com.xiaoniu.cleanking.ui.finish

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
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
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by xinxiaolong on 2020/8/4.
 * email：xinxiaolong123@foxmail.com
 */
public class NewCleanFinishPlusActivity : BaseActivity<CleanFinishPlusPresenter>(), NewCleanFinishPlusContract.CleanFinishView {

    var titleName: String = ""
    lateinit var pointer: CleanFinishPointer

    override fun getLayoutId(): Int {
        return R.layout.activity_new_clean_finish_plus_layout
    }

    override fun inject(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initView()
    }

    override fun initView() {
        StatusBarUtil.setTransparentForWindow(this)
        titleName = intent.getStringExtra("title")
        pointer = CleanFinishPointer(titleName)
        mPresenter.attachView(this)
        mPresenter.onCreate()

        loadAdv()
        initTitle()
        initHeadView()
        mPresenter.loadRecommendData()
    }

    private fun loadAdv() {
        mPresenter.loadOneAdv(findViewById(R.id.ad_container_1))
        mPresenter.loadTwoAdv(findViewById(R.id.ad_container_2))
    }

    private fun initTitle() {
        left_title.text = titleName
        left_title.setOnClickListener {
            pointer.returnPoint()
            onBackPressed()
        }
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
        var storage=PreferenceUtil.getCleanStorageNum().split(":")
        var num=storage[0]
        var unit=storage[1]
        function_icon.setImageResource(R.mipmap.finish_icon_ok)
        val content = AndroidUtil.zoomText(num.plus(unit), 2f,0,num.length)
        function_title.text = content
        function_sub_title.text = "垃圾已清理"
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
    }

    //病毒查杀
    private fun showKillVirusView() {
        function_icon.setImageResource(R.mipmap.finish_icon_virus)
        function_sub_title.visibility = View.GONE
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
        val subContent = "${time}s后达到最佳降温效果"
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
        if(item.title.equals("手机加速")){
            view.setImageLabel(RecmedItemDataStore.getInstance().memory)
        }else{
            view.setImageLabelHide()
        }
        view.setOnClickListener({ onRecommendViewClick(item.title) })
    }

    fun onRecommendViewClick(title: String) {
        pointer.recommendClickPoint(title)
        when (title) {
            "垃圾文件太多" -> startClean()
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
            pointer.goldCoinRequestAdv2()
            mPresenter.loadVideoAdv(bubbleCollected)
        }
        pointer.goldCoinDialogExposure()
        pointer.goldCoinRequestAdv1()
        GoldCoinDialog.showGoldCoinDialog(bean)
    }

    override fun dismissGoldCoinDialog() {
        GoldCoinDialog.dismiss()
    }

    private fun getStatisticsMap(): Map<String, Any>? {
        val map: MutableMap<String, Any> = HashMap()
        map["position_id"] = 5
        map["function_name"] = titleName
        return map
    }

    private fun getStatisticsJson(): JSONObject? {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("position_id", 5)
            jsonObject.put("function_name", titleName)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun getFunctionTitle(): String {
        return this.titleName
    }

    override fun onPostResume() {
        super.onPostResume()
        mPresenter.onPostResume()
        pointer.exposurePoint()
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
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * 一键清理
     */
    fun startClean() {
        startActivity(NowCleanActivity::class.java)
    }

    /**
     * 病毒查杀
     */
    fun startVirus() {
        startActivity(VirusKillActivity::class.java)
    }

    /**
     * 一键加速
     */
    fun startAcc() {
        val bundle = Bundle()
        bundle.putString(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_one_key_speed))
        startActivity(PhoneAccessActivity::class.java, bundle)
    }

    /**
     * 超强省电
     */
    fun startPower() {
        startActivity(PhoneSuperPowerActivity::class.java)
    }

    /**
     * 微信清理
     */
    fun startWxClean() {
        startActivity(WechatCleanHomeActivity::class.java)
    }

    /**
     * 手机降温
     */
    fun startCool() {
        startActivity(RouteConstants.PHONE_COOLING_ACTIVITY)
    }

    /**
     * 通知栏清理
     */
    fun startNotify() {
        NotifyCleanManager.startNotificationCleanActivity(getActivity(), 0)
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
}
