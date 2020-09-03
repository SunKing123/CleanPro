package com.xiaoniu.cleanking.ui.securitycenter.entrance.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.xiaoniu.cleanking.R

/**
 * Created by xinxiaolong on 2020/9/2.
 * email：xinxiaolong123@foxmail.com
 */
class RecommendBarView(context: Context, attrs: AttributeSet) :LinearLayout(context, attrs){

    var mView: View = LayoutInflater.from(context).inflate(R.layout.view_security_home_recommend_bar_layout, this, true)

}
