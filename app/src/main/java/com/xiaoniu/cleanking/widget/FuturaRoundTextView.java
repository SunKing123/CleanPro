package com.xiaoniu.cleanking.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class FuturaRoundTextView extends AppCompatTextView {

    public FuturaRoundTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/FuturaRound-Medium.ttf");
        setTypeface(tf);
    }
}
