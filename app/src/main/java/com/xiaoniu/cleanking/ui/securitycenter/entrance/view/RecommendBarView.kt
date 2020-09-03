package com.xiaoniu.cleanking.ui.securitycenter.entrance.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.ui.securitycenter.entrance.model.RecommendModel
import kotlinx.android.synthetic.main.view_security_home_recommend_bar_layout.view.*

/**
 * Created by xinxiaolong on 2020/9/2.
 * email：xinxiaolong123@foxmail.com
 */
class RecommendBarView(context: Context, attrs: AttributeSet) :RelativeLayout(context, attrs){

    var mView: View = LayoutInflater.from(context).inflate(R.layout.view_security_home_recommend_bar_layout, this, true)

    fun initViewData(model:RecommendModel){
        image_icon.setImageResource(model.icon)
        tv_title.text=model.title
    }

}

