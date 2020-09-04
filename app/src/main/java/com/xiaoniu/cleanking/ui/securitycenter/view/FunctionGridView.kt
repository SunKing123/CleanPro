package com.xiaoniu.cleanking.ui.securitycenter.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.xiaoniu.cleanking.R
import kotlinx.android.synthetic.main.view_security_function_gridview_layout.view.*

/**
 * Created by xinxiaolong on 2020/8/25.
 * email：xinxiaolong123@foxmail.com
 */

class FunctionGridView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var mView: View = LayoutInflater.from(context).inflate(R.layout.view_security_function_gridview_layout, this, true)

    private val accountView = ItemView(context, ItemModel("账号检测", R.drawable.icon_account_detection, "", ITEM_ACCOUNT))
    private val payView = ItemView(context, ItemModel("支付环境", R.drawable.icon_pay_detection, "", ITEM_PAY))
    private val autoKillView = ItemView(context, ItemModel("自动杀毒", R.drawable.icon_auto_kill_virus, "定时杀毒", ITEM_AUTO_KILL))
    private val softView = ItemView(context, ItemModel("软件检测", R.drawable.icon_soft_detection, "发现恶意插件", ITEM_SOFT))
    private val wifiView = ItemView(context, ItemModel("WI-FI安全", R.drawable.icon_wifi_detection, "", ITEM_WIFI))
    private val virusUpdateView = ItemView(context, ItemModel("病毒库更新", R.drawable.icon_virus_warehouse_update, "", ITEM_VIRUS_UPDATE))

    private var itemViews = arrayOf(accountView, payView, autoKillView, softView, wifiView, virusUpdateView)

    private var adapter: FunctionGridViewAdapter
    var onItemClickListener: OnItemClickListener?=null

    companion object {
        const val ITEM_ACCOUNT = "item_account"
        const val ITEM_PAY = "item_pay"
        const val ITEM_AUTO_KILL = "item_auto_kill"
        const val ITEM_SOFT = "item_soft"
        const val ITEM_WIFI = "item_wifi"
        const val ITEM_VIRUS_UPDATE = "item_virus_update"
    }

    init {

        var innerOnItemClickListener: OnItemClickListener = object : OnItemClickListener {
            override fun onClick(code: String) {
                onItemClickListener?.onClick(code)
            }
        }

        adapter = FunctionGridViewAdapter(context, itemViews)
        for (view in itemViews) {
            view.setOnClickListener(innerOnItemClickListener)
        }

        gridView.adapter=adapter
    }


    interface OnItemClickListener {
        fun onClick(code: String)
    }

    class ItemModel {
        var name: String
        var icon: Int
        var warning: String
        var code: String

        constructor(name: String, icon: Int, warning: String, code: String) {
            this.name = name
            this.icon = icon
            this.warning = warning
            this.code = code
        }
    }

    class ItemView {

        var mView: View
        var tvName: TextView
        var imageView: ImageView
        var tvWarning: TextView
        var model: ItemModel

        constructor(context: Context, model: ItemModel) {
            mView = LayoutInflater.from(context).inflate(R.layout.view_security_function_item_layout, null, false)
            tvName = mView.findViewById(R.id.icon_text)
            imageView = mView.findViewById(R.id.icon_image)
            tvWarning = mView.findViewById(R.id.icon_warning_text)
            this.model = model
            setData(model)
        }

        private fun setData(model: ItemModel) {
            setTextName(model.name)
            setIcon(model.icon)
            setWarning(model.warning)
        }

        fun setTextName(name: String) {
            tvName.text = name
        }

        fun setIcon(icon: Int) {
            imageView.setImageResource(icon)
        }

        fun setWarning(text: String) {
            tvWarning.text = text
        }

        fun visibleWarning() {
            tvWarning.visibility = View.VISIBLE
        }

        fun goneWarning() {
            tvWarning.visibility = View.GONE
        }

        fun getView(): View {
            return mView
        }

        fun setOnClickListener(listener: OnItemClickListener) {
            getView().setOnClickListener({ listener.onClick(model.code) })
        }
    }

    class FunctionGridViewAdapter : BaseAdapter {

        var itemViews: Array<ItemView>
        var context: Context

        constructor(context: Context, itemViews: Array<ItemView>) {
            this.context = context
            this.itemViews = itemViews
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return itemViews[position].getView()
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return itemViews.size
        }

    }
}
