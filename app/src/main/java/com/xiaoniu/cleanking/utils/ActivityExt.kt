package com.xiaoniu.cleanking.utils

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

/**
 * 对 AppCompatActivity进行扩展
 * 动态替换fragment对象
 */
fun AppCompatActivity.replaceFragment(resId: Int, fragment: Fragment) {
    this.supportFragmentManager.beginTransaction()
            .replace(resId, fragment, fragment.javaClass.simpleName)
            .commit()
}

/**
 * 动态替换fragment对象，增加回退栈
 */
fun AppCompatActivity.replaceFragmentStack(resId: Int, fragment: Fragment) {
    this.supportFragmentManager.beginTransaction()
            .replace(resId, fragment, fragment.javaClass.simpleName)
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
}