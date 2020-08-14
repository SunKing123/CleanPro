package com.xiaoniu.cleanking.ui.finish.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.xiaoniu.cleanking.R

/**
 * Created by xinxiaolong on 2020/8/14.
 * email：xinxiaolong123@foxmail.com
 */
public class HeadView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {

    var mView: View = LayoutInflater.from(context).inflate(R.layout.item_finish_layout, this, true)

}
