package com.xiaoniu.cleanking.utils

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * 对ViewGroup进行扩展，增加layoutInflater支持
 */
fun ViewGroup.inflater(@LayoutRes resId: Int): View {
    return LayoutInflater.from(this.context).inflate(resId, this, false)
}