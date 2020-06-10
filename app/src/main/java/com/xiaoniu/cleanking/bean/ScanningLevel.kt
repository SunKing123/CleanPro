package com.xiaoniu.cleanking.ui.newclean.bean

import android.graphics.Color

enum class ScanningLevel(var level: Int, var color: Int) {

    /**
     * 少量
     */
    Little(1, Color.parseColor("#16C9C5")),

    /**
     * 较多
     */
    Middle(2, Color.parseColor("#FF7740")),

    /**
     * 大量
     */
    Large(3, Color.parseColor("#FF5454"))

}