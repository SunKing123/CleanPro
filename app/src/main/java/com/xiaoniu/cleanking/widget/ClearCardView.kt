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
    private var mClearImage: AppCompatImageView
    private var mContentText: AppCompatTextView
    private var mSubContentText: AppCompatTextView
    private var mButton: AppCompatButton


    init {
        mTitleView = mView.findViewById(R.id.title)
        mClearImage = mView.findViewById(R.id.clear_img)
        mContentText = mView.findViewById(R.id.clear_content)
        mSubContentText = mView.findViewById(R.id.clear_sub_content)
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


    fun setClearItemImage(res: Int) {
        mClearImage.setImageResource(res)
    }

    fun setClearItemContent(text: String) {
        mContentText.text = text
    }

    fun setClearItemSubContent(text: String) {
        mSubContentText.text = text
    }


    fun getButton(): AppCompatButton {
        return mButton
    }
}
