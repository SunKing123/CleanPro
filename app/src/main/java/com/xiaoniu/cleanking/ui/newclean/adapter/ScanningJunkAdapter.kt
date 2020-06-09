package com.xiaoniu.cleanking.ui.newclean.adapter

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.scaning_item.view.*


class ScanningJunkAdapter : ListAdapter<JunkGroup, ScanningJunkAdapter.ScanningJunkViewHolder>(diffCallback) {

    companion object {

        @JvmStatic
        val diffCallback: DiffUtil.ItemCallback<JunkGroup> = object : DiffUtil.ItemCallback<JunkGroup>() {
            override fun areItemsTheSame(oldItem: JunkGroup, newItem: JunkGroup): Boolean {
                return oldItem.mName == newItem.mName
            }

            override fun areContentsTheSame(oldItem: JunkGroup, newItem: JunkGroup): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ScanningJunkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

        fun bind(item: JunkGroup?) {
            item?.let {
                with(itemView) {

                    //设置扫描类型名称
                    tv_scan_cate_title.text = it.mName

                    if (it.isScanningOver) {
                        //如果加载完成之后，则清除渲染状态
                        iv_scaning_state.clearAnimation()
                        //展示扫描完成状态
                        iv_scaning_state.imageResource = R.drawable.scanning_complete
                    } else {
                        //展示扫描中状态
                        iv_scaning_state.imageResource = R.drawable.scanning_loading
                        //设置扫描loading动画
                        renderScanningLoading(iv_scaning_state)
                    }
                }
            }
        }

        private fun renderScanningLoading(canningState: ImageView) {
            val rotate = RotateAnimation(0F, 720F, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rotate.repeatCount = -1
            rotate.interpolator = LinearInterpolator()
            rotate.duration = 2000
            rotate.fillAfter = true
            canningState.startAnimation(rotate)
        }

        override val containerView: View?
            get() = itemView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanningJunkViewHolder {
        return ScanningJunkViewHolder(parent.inflater(R.layout.scaning_item))
    }

    override fun onBindViewHolder(holder: ScanningJunkViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
