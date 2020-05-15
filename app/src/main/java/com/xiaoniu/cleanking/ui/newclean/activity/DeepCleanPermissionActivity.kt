package com.xiaoniu.cleanking.ui.newclean.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.mvp.BaseActivity
import com.xiaoniu.cleanking.ui.newclean.adapter.DeepCleanPermissionAdapter
import com.xiaoniu.cleanking.ui.newclean.bean.DeepCleanPermissionBean
import com.xiaoniu.common.utils.StatusBarUtil
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration
import kotlinx.android.synthetic.main.deep_clean_permission_activity.*
import org.jetbrains.anko.intentFor

/**
 * 深度清理未授权展示提醒页
 */
class DeepCleanPermissionActivity : BaseActivity() {

    private lateinit var adapter: DeepCleanPermissionAdapter

    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(context.intentFor<DeepCleanPermissionActivity>())
        }
    }

    override fun initLayout(savedInstanceState: Bundle?) {
        setContentView(R.layout.deep_clean_permission_activity)
    }

    override fun initViews() {
        StatusBarUtil.setTransparentForWindow(this)
        tv_back.setOnClickListener {
            onBackPressed()
        }
        tv_open_deep_clean.setOnClickListener {
            //todo 开启权限操作逻辑
        }

        adapter = DeepCleanPermissionAdapter()
        rv_content_list.layoutManager = LinearLayoutManager(this)
        rv_content_list.addItemDecoration(HorizontalDividerItemDecoration.Builder(this)
                .marginResId(R.dimen.dimen_16dp, R.dimen.dimen_16dp)
                .sizeResId(R.dimen.dimen_0_5dp)
                .colorResId(R.color.color_EDEDED)
                .build())
        rv_content_list.adapter = adapter

        val permissionList = arrayListOf(
                DeepCleanPermissionBean(R.drawable.ic_memory, getString(R.string.memory_clean_title), getString(R.string.memory_clean_content)),
                DeepCleanPermissionBean(R.drawable.ic_notification, getString(R.string.notification_clean_title), getString(R.string.notification_clean_content)),
                DeepCleanPermissionBean(R.drawable.ic_power, getString(R.string.power_clean_title), getString(R.string.power_clean_content))
        )
        adapter.submitList(permissionList)
    }

    override fun initData() {}

}