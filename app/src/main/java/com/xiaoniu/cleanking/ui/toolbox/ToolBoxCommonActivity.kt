package com.xiaoniu.cleanking.ui.toolbox

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.xiaoniu.cleanking.R
import com.xiaoniu.common.base.BaseActivity
import kotlinx.android.synthetic.main.activity_toolbox.*

/**
 * Desc:
 * <p>
 * Date: 2020/8/26
 * Copyright: Copyright (c) 2016-2022
 * Company: @小牛科技
 * Email:zengbo@xiaoniuhy.com
 * Update Comments:
 *
 * @author ZengBo
 */
class ToolBoxCommonActivity : BaseActivity() {

    companion object {
        const val PAGE_TYPE = "page_type"
        const val PAGE_WIFI_SECURITY = "wifi_security"
        const val PAGE_BATTERY_EXAMINATION = "batter_examination"
        const val PAGE_VIRUS_LIBRARY = "virus_library"
        const val PAGE_PAY_ENVIRONMENT = "pay_environment"
        const val PAGE_CAMERA_SCAN = "camera_scan"
    }

    override fun getLayoutResId(): Int = R.layout.activity_toolbox

    private var mPage: String = PAGE_WIFI_SECURITY
    override fun initVariable(intent: Intent?) {
        intent?.let {
            mPage = it.getStringExtra(PAGE_TYPE)
        }
    }

    override fun initViews(savedInstanceState: Bundle?) {
        when (mPage) {
            PAGE_WIFI_SECURITY -> showFragment("WIFI安全", WiFiSecurityFragment.INSTANCE)
            PAGE_BATTERY_EXAMINATION -> showFragment("电池体检", BatteryExaminationFragment.INSTANCE)
            PAGE_VIRUS_LIBRARY -> showFragment("病毒库更新", VirusLibraryUpdateFragment.INSTANCE)
            PAGE_PAY_ENVIRONMENT -> showFragment("支付环境检测", PayEnvironmentFragment.INSTANCE)
            PAGE_CAMERA_SCAN -> showFragment("摄像头扫描", CameraScanFragment.INSTANCE)
        }
    }

    private fun showFragment(title: String, fragment: Fragment) {
        setLeftTitle(title)
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commitAllowingStateLoss()
    }

    override fun setListener() {
    }

    override fun loadData() {
    }
}