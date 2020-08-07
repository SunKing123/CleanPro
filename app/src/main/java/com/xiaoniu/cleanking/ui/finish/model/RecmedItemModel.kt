package com.xiaoniu.cleanking.ui.finish.model

import android.text.SpannableString

/**
 * Created by xinxiaolong on 2020/8/4.
 * email：xinxiaolong123@foxmail.com
 *
 * 推荐功能数据
 */
data class RecmedItemModel constructor(var title:String, var content1: SpannableString, var content2: SpannableString, var imageIcon:Int, var buttonText:String)
