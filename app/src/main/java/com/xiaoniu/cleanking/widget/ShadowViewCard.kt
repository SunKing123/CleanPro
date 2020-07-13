package com.xiaoniu.cleanking.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.xiaoniu.cleanking.R


class ShadowViewCard(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet) {
    private val DEFAULT_VALUE_SHADOW_COLOR: Int = R.color.shadow_default_color

    private val DEFAULT_VALUE_SHADOW_CARD_COLOR: Int = R.color.shadow_card_default_color
    //圆角大小，默认是6DP
    private val DEFAULT_VALUE_SHADOW_ROUND = 0

    private val DEFAULT_VALUE_SHADOW_RADIUS = 10

    private val DEFAULT_VALUE_SHADOW_TOP_HEIGHT = 5

    private val DEFAULT_VALUE_SHADOW_LEFT_HEIGHT = 5

    private val DEFAULT_VALUE_SHADOW_RIGHT_HEIGHT = 5

    private val DEFAULT_VALUE_SHADOW_BOTTOM_HEIGHT = 5

    private val DEFAULT_VALUE_SHADOW_OFFSET_Y = 0

    private val DEFAULT_VALUE_SHADOW_OFFSET_X = DEFAULT_VALUE_SHADOW_TOP_HEIGHT / 3

    private var shadowRound = 0
    private var shadowColor = 0
    private var shadowCardColor = 0
    private var shadowRadius = 0F
    private var shadowOffsetY = 0
    private var shadowOffsetX = 0F
    private var shadowTopHeight = 0
    private var shadowLeftHeight = 0
    private var shadowRightHeight = 0
    private var shadowBottomHeight = 0

    init {
        val a: TypedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ShadowViewCard)
        shadowRound = a.getDimensionPixelSize(R.styleable.ShadowViewCard_shadowRound, DEFAULT_VALUE_SHADOW_ROUND)
        shadowColor = a.getColor(R.styleable.ShadowViewCard_shadowColor, resources.getColor(DEFAULT_VALUE_SHADOW_COLOR))
        shadowCardColor = a.getColor(R.styleable.ShadowViewCard_shadowCardColor, resources.getColor(DEFAULT_VALUE_SHADOW_CARD_COLOR))
        shadowTopHeight = a.getDimensionPixelSize(R.styleable.ShadowViewCard_shadowTopHeight, dp2px(getContext(), DEFAULT_VALUE_SHADOW_TOP_HEIGHT))
        shadowRightHeight = a.getDimensionPixelSize(R.styleable.ShadowViewCard_shadowRightHeight, dp2px(getContext(), DEFAULT_VALUE_SHADOW_RIGHT_HEIGHT))
        shadowLeftHeight = a.getDimensionPixelSize(R.styleable.ShadowViewCard_shadowLeftHeight, dp2px(getContext(), DEFAULT_VALUE_SHADOW_LEFT_HEIGHT))
        shadowBottomHeight = a.getDimensionPixelSize(R.styleable.ShadowViewCard_shadowBottomHeight, dp2px(getContext(), DEFAULT_VALUE_SHADOW_BOTTOM_HEIGHT))
        shadowOffsetY = a.getDimensionPixelSize(R.styleable.ShadowViewCard_shadowOffsetY, dp2px(getContext(), DEFAULT_VALUE_SHADOW_OFFSET_Y))
        shadowOffsetX = a.getDimensionPixelSize(R.styleable.ShadowViewCard_shadowOffsetX, dp2px(getContext(), DEFAULT_VALUE_SHADOW_OFFSET_X)).toFloat()
        shadowRadius = a.getInteger(R.styleable.ShadowViewCard_shadowRadius, DEFAULT_VALUE_SHADOW_RADIUS).toFloat()
        a.recycle()
        setPadding(shadowLeftHeight, shadowTopHeight, shadowRightHeight, shadowBottomHeight)
        //setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    private fun dp2px(context: Context, dipValue: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        val shadowPaint = Paint()
        shadowPaint.color = Color.WHITE
        shadowPaint.style = Paint.Style.FILL
        shadowPaint.isAntiAlias = true
        val left = shadowLeftHeight.toFloat()
        val top = shadowTopHeight.toFloat()
        val right = width - shadowRightHeight.toFloat()
        val bottom = height - shadowBottomHeight.toFloat()
        shadowPaint.setShadowLayer(shadowRadius, shadowOffsetX, shadowOffsetX, shadowColor)
        val rectF = RectF(left, top, right, bottom)
        canvas?.drawRoundRect(rectF, shadowRound.toFloat(), shadowRound.toFloat(), shadowPaint)
        canvas?.save()
        super.dispatchDraw(canvas)
    }
}
