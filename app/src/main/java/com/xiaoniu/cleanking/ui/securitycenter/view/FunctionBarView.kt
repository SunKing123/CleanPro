package com.xiaoniu.cleanking.ui.securitycenter.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.ui.securitycenter.model.FunctionBarModel
import kotlinx.android.synthetic.main.view_security_home_function_bar_layout.view.*

/**
 * Created by xinxiaolong on 2020/9/3.
 * email：xinxiaolong123@foxmail.com
 */
class FunctionBarView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    var mView: View = LayoutInflater.from(context).inflate(R.layout.view_security_home_function_bar_layout, this, true)
    var model: FunctionBarModel?=null


    fun setViewData(model: FunctionBarModel) {
        this.model = model
        tv_title.text = model.title
        tv_content.text = model.content
        image_icon.setImageResource(model.icon)
    }

    fun showMark() {
        tv_mark.visibility = View.VISIBLE
        tv_mark.text = model?.warning
    }

    fun goneMark() {
        tv_mark.visibility = View.GONE
    }

}
