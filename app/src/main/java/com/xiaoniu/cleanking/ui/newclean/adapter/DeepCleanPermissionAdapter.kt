package com.xiaoniu.cleanking.ui.newclean.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.ui.newclean.bean.DeepCleanPermissionBean
import com.xiaoniu.cleanking.utils.inflater
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.deep_clean_permission_item.view.*
import org.jetbrains.anko.imageResource

class DeepCleanPermissionAdapter : RecyclerView.Adapter<DeepCleanPermissionAdapter.DeepCleanPermissionViewHolder>() {

    private val dataSets = ArrayList<DeepCleanPermissionBean>()

    class DeepCleanPermissionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

        fun bind(bean: DeepCleanPermissionBean) {
            with(itemView) {
                iv_deep_clean_icon.imageResource = bean.icon
                tv_deep_clean_title.text = bean.title
                tv_deep_clean_content.text = bean.content
            }
        }

        override val containerView: View?
            get() = itemView
    }

    fun submitList(data: List<DeepCleanPermissionBean>) {
        dataSets.clear()
        dataSets.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeepCleanPermissionViewHolder {
        return DeepCleanPermissionViewHolder(parent.inflater(R.layout.deep_clean_permission_item))
    }

    override fun getItemCount(): Int {
        return dataSets.size
    }

    override fun onBindViewHolder(holder: DeepCleanPermissionViewHolder, position: Int) {
        holder.bind(dataSets[position])
    }
}