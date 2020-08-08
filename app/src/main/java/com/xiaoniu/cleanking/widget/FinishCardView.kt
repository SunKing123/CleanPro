package com.xiaoniu.cleanking.widget

import android.content.Context
import android.text.SpannableString
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.xiaoniu.cleanking.R

class FinishCardView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {
    private var mContext: Context = context
    var mView: View = LayoutInflater.from(context).inflate(R.layout.item_finish_layout, this, true)
    private var mTitleView: AppCompatTextView
    private var mImage: AppCompatImageView
    private var mImageLabel: AppCompatTextView
    private var mSubText1: AppCompatTextView
    private var mSubText2: AppCompatTextView
    private var mButton: AppCompatTextView


    init {
        mTitleView = mView.findViewById(R.id.title)
        mImage = mView.findViewById(R.id.image)
        mImageLabel = mView.findViewById(R.id.image_label)
        mSubText1 = mView.findViewById(R.id.sub_title_1)
        mSubText2 = mView.findViewById(R.id.sub_title_2)
        mButton = mView.findViewById(R.id.button)
    }


    fun setLeftTitle(value: String) {
        mTitleView.text = value
    }

    fun setImage(res: Int) {
        mImage.setImageResource(res)
    }

    fun setImageLabel(value: String) {
        mImageLabel.text = value
    }

    fun setImageLabelHide() {
        mImageLabel.visibility = View.GONE
    }

    fun setImageLabelVisible() {
        mImageLabel.visibility = View.VISIBLE
    }

    fun setSubTitle1(text: SpannableString) {
        mSubText1.text = text
    }

    fun setSubTitle2(text: SpannableString) {
        mSubText2.text = text
    }

    fun setButtonText(text: String) {
        mButton.text = text
    }
}
