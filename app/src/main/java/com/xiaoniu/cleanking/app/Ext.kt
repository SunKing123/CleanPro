package com.xiaoniu.cleanking.app

import android.content.Intent

fun Intent.getStringExtraNotNull(key: String): String {
    return if (getStringExtra(key) == null) "" else getStringExtra(key)
}