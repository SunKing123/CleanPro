package com.xiaoniu.cleanking.utils

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.comm.jksdk.utils.DisplayUtil
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.midas.MidasRequesCenter
import com.xiaoniu.unitionadbase.abs.AbsAdBusinessCallback
import com.xiaoniu.unitionadbase.model.AdInfoModel

/**
 * Desc:
 * <p>
 * Date: 2020/8/12
 * Copyright: Copyright (c) 2016-2022
 * Company: @小牛科技
 * Email:zengbo@xiaoniuhy.com
 * Update Comments:
 *
 * @author ZengBo
 */
class InsideScreenDialogUtil {


    fun showInsideDialog(activity: AppCompatActivity?, adId: String) {

        MidasRequesCenter.requestAndShowAd(activity, adId, object : AbsAdBusinessCallback() {
            var dialog: Dialog? = null
            override fun onAdLoaded(adInfoModel: AdInfoModel) {
                super.onAdLoaded(adInfoModel)
                if (activity != null && !activity.isFinishing) {
                    dialog = Dialog(activity)
                    dialog?.setContentView(R.layout.insert_screen_ad_layout)
                    val window = dialog!!.window
                    if (window != null) {
                        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        val p = window.attributes
                        p.width = (DisplayUtil.getScreenWidth(activity) * 0.80f).toInt()
                        window.attributes = p
                    }
                    val frameLayout = dialog?.findViewById<FrameLayout>(R.id.ad_container)
                    adInfoModel.addInContainer(frameLayout)
                    if (!activity.isFinishing) {
                        dialog?.show()
                    }
                }
            }

            override fun onAdClose(adInfoModel: AdInfoModel) {
                super.onAdClose(adInfoModel)
                dialog?.let {
                    if (it.isShowing) {
                        it.dismiss()
                    }
                }
            }
        })


    }


}