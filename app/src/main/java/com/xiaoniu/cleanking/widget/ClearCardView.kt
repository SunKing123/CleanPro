package com.xiaoniu.cleanking.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.xiaoniu.cleanking.R

class ClearCardView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    private var mContext: Context = context
    var mView: View = LayoutInflater.from(context).inflate(R.layout.layout_main_clear_item, this, true)
    private var mTitleView: AppCompatTextView
    private var mItemView1: AppCompatImageView
    private var mItemView2: AppCompatImageView
    private var mItemView3: AppCompatImageView
    private var mItemView4: AppCompatImageView
    private var mItemView5: AppCompatImageView
    private lateinit var mButton: AppCompatButton


    init {
        mTitleView = mView.findViewById(R.id.title)
        mItemView1 = mView.findViewById(R.id.item_1)
        mItemView2 = mView.findViewById(R.id.item_2)
        mItemView3 = mView.findViewById(R.id.item_3)
        mItemView4 = mView.findViewById(R.id.item_4)
        mItemView5 = mView.findViewById(R.id.item_5)
        mButton = mView.findViewById(R.id.button)
    }


    fun setLeftTitle(value: String) {
        mTitleView.text = value
    }

    fun setLeftIcon(res: Int) {
        val leftDrawable = mContext.getDrawable(res)
        leftDrawable?.setBounds(0, 0, leftDrawable.minimumWidth, leftDrawable.minimumHeight)
        mTitleView.setCompoundDrawables(leftDrawable, null, null, null)
    }

    fun setCommonItemImageRes(res: Int) {
        setItem1ImageRes(res)
        setItem2ImageRes(res)
        setItem3ImageRes(res)
        setItem4ImageRes(res)
        setItem5ImageRes(res)
    }

    fun setItem1ImageRes(res: Int) {
        mItemView1.setImageResource(res)
    }

    fun setItem2ImageRes(res: Int) {
        mItemView2.setImageResource(res)
    }

    fun setItem3ImageRes(res: Int) {
        mItemView3.setImageResource(res)
    }

    fun setItem4ImageRes(res: Int) {
        mItemView4.setImageResource(res)
    }

    fun setItem5ImageRes(res: Int) {
        mItemView5.setImageResource(res)
    }

    fun getButton(): AppCompatButton {
        return mButton
    }
}