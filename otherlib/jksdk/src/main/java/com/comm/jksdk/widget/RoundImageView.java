package com.comm.jksdk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import com.comm.jksdk.R;

/**
  *
  * @ProjectName:    ${PROJECT_NAME}
  * @Package:        ${PACKAGE_NAME}
  * @ClassName:      ${NAME}
  * @Description:     四个圆角的imageview
  * @Author:         fanhailong
  * @CreateDate:     ${DATE} ${TIME}
  * @UpdateUser:     更新者：
  * @UpdateDate:     ${DATE} ${TIME}
  * @UpdateRemark:   更新说明：
  * @Version:        1.0
 */public class RoundImageView extends AppCompatImageView {

    float width, height;

    private int fillet = 0;
    public RoundImageView(Context context) {
        this(context, null);
        init(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomsRoundAngleImageView, defStyleAttr, 0);

        fillet = a.getDimensionPixelSize(R.styleable.CustomsRoundAngleImageView_fillet, 0);
        a.recycle();
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width >= fillet && height > fillet) {
            Path path = new Path();
            //四个圆角
            path.moveTo(fillet, 0);

            path.lineTo(width - fillet, 0);
            path.quadTo(width, 0, width, fillet);

            path.lineTo(width, height - fillet);
            path.quadTo(width, height, width - fillet, height);
            path.lineTo(fillet, height);
            path.quadTo(0, height, 0, height - fillet);

            //左上角
            path.lineTo(0, fillet);
            path.quadTo(0, 0, fillet, 0);

            canvas.clipPath(path);
        }
        super.onDraw(canvas);
    }

    private int dp2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
