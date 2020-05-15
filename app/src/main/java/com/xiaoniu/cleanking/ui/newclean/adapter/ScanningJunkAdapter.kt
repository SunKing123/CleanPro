package com.xiaoniu.cleanking.ui.newclean.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup
import com.xiaoniu.cleanking.utils.CleanUtil
import com.xiaoniu.cleanking.utils.inflater
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.scaning_item.view.*
import org.jetbrains.anko.imageResource


class ScanningJunkAdapter : RecyclerView.Adapter<ScanningJunkAdapter.ScanningJunkViewHolder>() {

    private val junkGroupList = ArrayList<JunkGroup>()

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
                        //展示总共扫描到多少垃圾
                        val countEntity = CleanUtil.formatShortFileSize(it.mSize)
                        tv_scaning_junk_total.text = "已选" + countEntity.resultSize
                    } else {
                        //展示扫描中状态
                        iv_scaning_state.imageResource = R.drawable.scanning_loading
                        //设置扫描loading动画
                        renderScanningLoading(iv_scaning_state)
                        //展示当前扫描到多少垃圾
                        val countEntity = CleanUtil.formatShortFileSize(it.mSize)
                        tv_scaning_junk_total.text = countEntity.resultSize
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

    fun submitList(junkList: List<JunkGroup>) {
        junkGroupList.clear()
        junkGroupList.addAll(junkList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanningJunkViewHolder {
        return ScanningJunkViewHolder(parent.inflater(R.layout.scaning_item))
    }

    override fun onBindViewHolder(holder: ScanningJunkViewHolder, position: Int) {
        holder.bind(junkGroupList[position])
    }

    override fun getItemCount(): Int {
        return junkGroupList.size
    }
}
