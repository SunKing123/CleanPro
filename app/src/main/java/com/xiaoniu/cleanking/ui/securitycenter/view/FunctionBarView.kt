package com.xiaoniu.cleanking.ui.securitycenter.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.xiaoniu.cleanking.R

/**
 * Created by xinxiaolong on 2020/9/3.
 * email：xinxiaolong123@foxmail.com
 */
class FunctionBarView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    var mView: View = LayoutInflater.from(context).inflate(R.layout.view_security_home_function_bar_layout, this, true)

}
