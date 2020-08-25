package com.xiaoniu.cleanking.ui.finish.view

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.utils.AndroidUtil
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import com.xiaoniu.common.utils.DisplayUtils
import kotlinx.android.synthetic.main.item_finish_head_view_layout.view.*

/**
 * Created by xinxiaolong on 2020/8/14.
 * email：xinxiaolong123@foxmail.com
 */
public class FinishHeadView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    var mView: View = LayoutInflater.from(context).inflate(R.layout.item_finish_head_view_layout, this, true)

     fun initViewData(titleName:String){
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
             "账号检测" -> showAccount()
             "支付环境" -> showPayment()
             "自动杀毒" -> showAutoKillVirus()
             "软件检测" -> showSoftware()
             "wifi安全" -> showWifi()
             "流量检测" -> showFlow()
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
        function_sub_title.textSize = 10F
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
        val sizeSpan = AbsoluteSizeSpan(DisplayUtils.sp2px(context, 30F))
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

    //账号检测
    private fun showAccount() {
        function_icon.setImageResource(R.mipmap.finish_icon_ok)
        function_title.text = "已达到最佳状态"
        function_sub_title.text = "快去体验其他清理功能"
    }

    //支付环境
    private fun showPayment() {
        function_icon.setImageResource(R.mipmap.finish_icon_ok)
        function_title.text = "已达到最佳状态"
        function_sub_title.text = "快去体验其他清理功能"
    }

    //自动杀毒
    private fun showAutoKillVirus() {
        function_icon.setImageResource(R.mipmap.finish_icon_ok)
        function_title.text = "已达到最佳状态"
        function_sub_title.text = "快去体验其他清理功能"
    }


    //软件检测
    private fun showSoftware() {
        function_icon.setImageResource(R.mipmap.finish_icon_ok)
        function_title.text = "已达到最佳状态"
        function_sub_title.text = "快去体验其他清理功能"
    }


    //wifi安全
    private fun showWifi() {
        function_icon.setImageResource(R.mipmap.finish_icon_ok)
        function_title.text = "已达到最佳状态"
        function_sub_title.text = "快去体验其他清理功能"
    }

    //流量检测
    private fun showFlow() {
        function_icon.setImageResource(R.mipmap.finish_icon_ok)
        function_title.text = "已达到最佳状态"
        function_sub_title.text = "快去体验其他清理功能"
    }

}
