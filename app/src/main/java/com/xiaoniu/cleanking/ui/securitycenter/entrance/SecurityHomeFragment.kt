package com.xiaoniu.cleanking.ui.securitycenter.entrance

import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.base.SimpleFragment

/**
 * Created by xinxiaolong on 2020/8/25.
 * email：xinxiaolong123@foxmail.com
 */
class SecurityHomeFragment : SimpleFragment() {

    companion object {
        //懒汉模式单例
        private var fragment: SecurityHomeFragment? = null
            get() {
                if (field == null) {
                    field = SecurityHomeFragment()
                }
                return field
            }

        @Synchronized
        fun getInstance(): SecurityHomeFragment {
            return fragment!!
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_security_home_tab_layout
    }

    override fun initView() {

    }
}
