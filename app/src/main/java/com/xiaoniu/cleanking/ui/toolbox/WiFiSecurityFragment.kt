package com.xiaoniu.cleanking.ui.toolbox

import android.os.Bundle
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.base.SimpleFragment
import com.xiaoniu.cleanking.mvp.BaseFragment

/**
 * Desc: WIFI安全
 * <p>
 * Date: 2020/8/26
 * Copyright: Copyright (c) 2016-2022
 * Company: @小牛科技
 * Email:zengbo@xiaoniuhy.com
 * Update Comments:
 *
 * @author ZengBo
 */
class WiFiSecurityFragment : SimpleFragment() {

    companion object {
        val INSTANCE = WiFiSecurityFragment()
    }

    override fun getLayoutId(): Int = R.layout.fragment_wifi_security

    override fun initView() {
    }

}