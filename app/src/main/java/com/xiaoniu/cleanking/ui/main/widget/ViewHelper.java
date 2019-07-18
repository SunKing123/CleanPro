package com.xiaoniu.cleanking.ui.main.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Method;

/**
 * deprecation:View工具类
 * author:ayb
 * time:2017/10/31
 */

public class ViewHelper {

    public static GradientDrawable buildGradientDrawable() {
        int strokeWidth = 1; // 1dp 边框宽度
        int roundRadius = 100; // 2dp 圆角半径
        int strokeColor = 0xab000000;//边框颜色
        int fillColor = 0xab000000;//内部填充颜色
        GradientDrawable gradientDrawable = new GradientDrawable();//创建drawable
        gradientDrawable.setColor(fillColor);
        gradientDrawable.setCornerRadius(roundRadius);
        gradientDrawable.setStroke(strokeWidth, strokeColor);
        return gradientDrawable;
    }

    public static Bitmap getRoundCornerImage(int imageSize, Bitmap bitmap, int roundPixels) {
        if (bitmap == null) return null;
        if (imageSize <= roundPixels) return null;
        Bitmap roundCornerImage = null;
        try {
            roundCornerImage = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888);//创建一个和原始图片一样大小的位图
            Canvas canvas = new Canvas(roundCornerImage);//创建位图画布
            Paint paint = new Paint();//创建画笔
            Rect rect = new Rect(0, 0, imageSize, imageSize);//创建一个和原始图片一样大小的矩形
            RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);// 抗锯齿
            canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);//画一个基于前面创建的矩形大小的圆角矩形
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));//设置相交模式
            canvas.drawBitmap(bitmap, null, rect, paint);//把图片画到矩形去
        } catch (Exception e) {
        }
        return roundCornerImage != null ? Bitmap.createBitmap(roundCornerImage, roundPixels, 0, imageSize - roundPixels, imageSize - roundPixels) : bitmap;
    }

    private ViewHelper() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void setVisibility(View view, int visibility) {
        view.setVisibility(visibility);
    }

    public static void setVisibility(View view, boolean isShow) {
        view.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public static <T extends TextView> boolean safelySetText(T view, CharSequence text) {
        return safelySetText(view, text, false);
    }

    /**
     * 过滤空字符串进行文本设置
     *
     * @param view         控件
     * @param text         文字
     * @param isHtmlFormat 是否Html格式转换
     * @return 是否显示
     */
    public static <T extends TextView> boolean safelySetText(T view, CharSequence text, boolean isHtmlFormat) {
        if (!TextUtils.isEmpty(text)) {
            setVisibility(view, true);
            view.setText(isHtmlFormat ? Html.fromHtml(text.toString().trim()) : text.toString());
            return true;
        } else {
            setVisibility(view, false);
            return false;
        }
    }

    public static <T extends TextView> boolean safelySetText(T view, View parent, CharSequence text) {
        return safelySetText(view, parent, text, false);
    }

    /**
     * 过滤空字符串进行文本设置
     *
     * @param view
     * @param parent
     * @param text
     * @param isHtmlFormat 是否Html格式转换
     * @return 是否显示
     */
    public static <T extends TextView> boolean safelySetText(T view, View parent, CharSequence text, boolean isHtmlFormat) {
        if (!TextUtils.isEmpty(text)) {
            setVisibility(parent, true);
            view.setText(isHtmlFormat ? Html.fromHtml(text.toString().trim()) : text.toString().trim());
            return true;
        } else {
            setVisibility(parent, false);
            return false;
        }
    }

    /**
     * 使用默认文案替代空字符串
     *
     * @param view
     * @param text
     * @param defVal
     */
    public static <T extends TextView> void safelySetText(T view, CharSequence text, CharSequence defVal) {
        safelySetText(view, text, defVal, false);
    }

    public static <T extends TextView> void safelySetText(T view, CharSequence text, CharSequence defVal, boolean isHtmlFormat) {
        view.setText(!TextUtils.isEmpty(text)
                ? isHtmlFormat ? Html.fromHtml(text.toString().trim()) : text.toString().trim()
                : isHtmlFormat ? Html.fromHtml(defVal.toString().trim()) : defVal.toString().trim());
    }

    /**
     * 带完善重构方法
     *
     * @param view         TargetView
     * @param parent       Target 父布局, 用于数据为空是整块不展示, 传 null 不处理
     * @param text         内容数据
     * @param defVal       默认数据
     * @param nullVisible  内容数据为空是否展示       (def: false)
     * @param isHtmlFormat 是否使用 Html 进行解析    (def: false)
     * @param <T>
     */
    public static <T extends TextView> void safelySetText(T view, @Nullable ViewGroup parent
            , @Nullable CharSequence text, @NonNull CharSequence defVal
            , boolean nullVisible, boolean isHtmlFormat) {
        boolean isEmpty = TextUtils.isEmpty(text);
        boolean isDefEmpty = TextUtils.isEmpty(defVal);
        if (isEmpty) {
            if (null == parent) {
                if (nullVisible) {
                    view.setText(isHtmlFormat ? Html.fromHtml(text.toString().trim()) : text.toString().trim());
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
            } else {

            }
        } else {
        }
    }

    public static <T extends TextView> void safelySetTextColor(Activity activity, T view, int color) {
        if (activity == null || activity.getResources() == null) return;
        if (view == null) return;
        view.setTextColor(activity.getResources().getColor(color));
    }

    public static <T extends TextView> void safelySetTextBackGroundColor(Activity activity, T view, int drawableId) {
        if (activity == null || activity.getResources() == null) return;
        if (view == null) return;
        view.setBackground(activity.getResources().getDrawable(drawableId));
    }

    public static <T extends ImageView> void saveSetImageDrawable(Context context, T view, int drawableId) {
        if (context != null && context.getResources() != null) {
            view.setImageDrawable(context.getResources().getDrawable(drawableId));
        }
    }

    public static <T extends TextView> void safelySetTextColor(T view, String color) {
        try {
            if (view == null) return;
            view.setTextColor(Color.parseColor(color));
        } catch (Exception e) {
        }
    }

    /**
     * 设置字体DIN-Bold
     *
     * @param textView 字体控件
     */
    public static void setTextViewToBoldOTF(TextView textView) {
        Typeface typeface;
        try {
            typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/DIN-Bold.otf");
        } catch (Exception e) {
            typeface = null;
        }
        if (typeface != null) {
            textView.setTypeface(typeface);
        }
    }

    /**
     * 设置字体DIN-Regular
     *
     * @param textView 字体控件
     */
    public static void setTextViewToDinRegularOTF(TextView textView) {
        Typeface typeface;
        try {
            typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/DIN-Regular.otf");
        } catch (Exception e) {
            typeface = null;
        }
        if (typeface != null) {
            textView.setTypeface(typeface);
        }
    }


    /**
     * 设置字体DIN-Medium
     *
     * @param textView 字体控件
     */
    public static void setTextViewToMediumOTF(TextView textView) {
        Typeface typeface;
        try {
            typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/DIN-Medium.otf");
        } catch (Exception e) {
            typeface = null;
        }
        if (typeface != null) {
            textView.setTypeface(typeface);
        }
    }

    /**
     * 礼物横幅设置字体DIN-BoldItalic
     *
     * @param textView 字体控件
     */
    public static void setTextViewToBoldItalic(TextView textView) {
        Typeface typeface;
        try {
            typeface = Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/DIN-BoldItalic.otf");
        } catch (Exception e) {
            typeface = null;
        }
        if (typeface != null) {
            textView.setTypeface(typeface);
        }
    }

    /**
     * 禁用EditTextView输入Enter
     *
     * @param editText EditTextView控件
     */
    public static void setDisableKeyCodeEnter(EditText editText) {
        if (editText == null) {
            return;
        }
        editText.setOnEditorActionListener((v, actionId, event)
                -> {
            if (event != null) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            } else {
                return false;
            }
        });
    }

    /**
     * 设置字体DIN-Medium
     *
     * @param checkBox 字体控件
     */
    public static void setTextViewToMediumOTF(CheckBox checkBox) {
        Typeface typeface;
        try {
            typeface = Typeface.createFromAsset(checkBox.getContext().getAssets(), "fonts/DIN-Medium.otf");
        } catch (Exception e) {
            typeface = null;
        }
        if (typeface != null) {
            checkBox.setTypeface(typeface);
        }
    }


    /**
     * 让 activity transition 动画过程中可以正常渲染页面
     */
    public static void setDrawDuringWindowsAnimating(View view) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // 1 android n以上  & android 4.1以下不存在此问题，无须处理
            return;
        }
        // 4.2不存在setDrawDuringWindowsAnimating，需要特殊处理
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            handleDispatchDoneAnimating(view);
            return;
        }
        try {
            // 4.3及以上，反射setDrawDuringWindowsAnimating来实现动画过程中渲染
            ViewParent rootParent = view.getRootView().getParent();
            Method method = rootParent.getClass()
                    .getDeclaredMethod("setDrawDuringWindowsAnimating", boolean.class);
            method.setAccessible(true);
            method.invoke(rootParent, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * android4.2可以反射handleDispatchDoneAnimating来解决
     */
    private static void handleDispatchDoneAnimating(View paramView) {
        try {
            ViewParent localViewParent = paramView.getRootView().getParent();
            Class localClass = localViewParent.getClass();
            Method localMethod = localClass.getDeclaredMethod("handleDispatchDoneAnimating");
            localMethod.setAccessible(true);
            localMethod.invoke(localViewParent);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

}
