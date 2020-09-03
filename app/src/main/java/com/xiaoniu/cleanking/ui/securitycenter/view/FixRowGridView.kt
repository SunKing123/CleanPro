package com.xiaoniu.cleanking.ui.securitycenter.view

import android.content.Context
import android.util.AttributeSet
import android.widget.GridView

/**
 * Created by xinxiaolong on 2020/8/26.
 * email：xinxiaolong123@foxmail.com
 */
class FixRowGridView(context: Context,attrs: AttributeSet) :GridView(context,attrs){

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = MeasureSpec.makeMeasureSpec(
                Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }
}
