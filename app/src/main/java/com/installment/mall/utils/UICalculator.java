package com.installment.mall.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * 用於畫面處理
 * @author Lance Johannson
 * @version 20141031[JCzz]  1.新增getAutoTextSize 會回傳字型大小
 */
public class UICalculator 
{
	public static int getDimensionPixelSize(Context context, int size)
	{
		return getDimensionPixelSize(context, TypedValue.COMPLEX_UNIT_DIP, size);
	}

	public static int getDimensionPixelSize(Context context, int unit, int size)
	{
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return (int) TypedValue.applyDimension(unit, size, dm);
	}

	/**
	 * 取得百分比寬度
	 * @param activity
	 * @param resID
	 * @return 百分比寬度
	 */
	public static int getPercentWidth(Activity activity, int resID)
	{
		return getPercentWidth(activity, resID, getWidth(activity));
	}

	private static int getPercentWidth(Activity activity, int resID, float totalWidth)
	{
		int nPercent = activity.getResources().getInteger(resID);
		return (int)(totalWidth * nPercent / 100);
	}

	/**
	 * 取得螢幕寬度
	 * @param activity
	 * @return
	 */
	public static float getWidth(Activity activity)
	{
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}
	
	/**
	 * 取得比例寬
	 * @param activity
	 * @param size 文字大小
	 * @return 比例寬
	 */
	public static float getRatioWidth(Activity activity, int size){
		return getRatioWidth(activity,size, true);
	}
	
	/**
	 * 取得比例寬
	 * @param activity
	 * @param size 文字大小
	 * @param b 是否決定取長、寬最小值
	 * @return 比例寬
	 */
	public static float getRatioWidth(Activity activity, int size, boolean b){
		if(b)
			return size* Math.min(getWidth(activity), getHeight(activity))/320;
		else
			return size*getWidth(activity)/320;
	}

	/**
	 * 取得螢幕高度
	 * @param activity
	 * @return
	 */
	public static float getHeight(Activity activity)
	{
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

    /**
     * 取得狀態列高度
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

	/**
	 * 取得百分比高度
	 * @param activity
	 * @param resID
	 * @return 百分比高度
	 */
	public static int getPercentHeight(Activity activity, int resID)
	{
		return getPercentHeight(activity, resID, getHeight(activity));
	}

	private static int getPercentHeight(Activity activity, int resID, float totalHeight)
	{
		int nPercent = activity.getResources().getInteger(resID);
		return (int)(totalHeight * nPercent / 100);
	}
	

	/**
	 * 取得字串長度
	 * @param s
	 * @param textSize
	 * @return
	 */
	public static float getTextWidth(String s, int textSize)
	{
		if(s == null || s.length() == 0)
		{
			return 0;
		}

		float[] tmp = new float[s.length()];
		Paint p = new Paint();
		p.setTextSize((float)textSize);
		p.getTextWidths(s, tmp);
		int width = 0;

		for(int j = 0 ; j < tmp.length ;j++)
		{
			width += tmp[j];
		}
		return width;
	}
	
	/**
	 * 取得字串長度(px)
	 * @param s
	 * @param textSize
	 * @return
	 */
	public static int getTextWidth(String s, float textSize)
	{
	    if(s == null || s.length() == 0)
	    {
	    	return 0;
	    }
	    
	    float[] textLength = new float[s.length()];
	    Paint p = new Paint();
	    p.setTextSize(textSize);
	    p.getTextWidths(s, textLength);
	    
	    for(int i = 1 ; i < textLength.length ; i++)
	    {
	    	textLength[0] += textLength[i];
	    }
	    
	    return (int)textLength[0];
	}

    public static int getTextWidth(Context context, int unit, String text, float textSize)
    {
        TextView tv = new TextView(context);
        tv.setTextSize(unit, textSize);
        TextPaint paint = tv.getPaint();
        return (int)paint.measureText(text + "  ");
    }

	/**
     * 自動計算字串長度
     * @param text textview
     * @param str 字串
     * @param maxWidth 寬度
     * @param normalSize 給予textview原本標準文字大小(PX)
     */
    public static void setAutoText(TextView text, String str, int maxWidth, float normalSize) {
        if(str != null && str.length() > 0 && maxWidth > 0) {
            float [] textLength = new float[str.length()];
            Paint paint = new Paint();
            paint.setTextSize(normalSize);
            paint.getTextWidths(str, textLength);
            for(int i = 1 ; i < textLength.length ; i++) {
                textLength[0] += textLength[i];
            }
            while (textLength[0] > maxWidth) {
                normalSize -= 2;
                paint.setTextSize(normalSize);
                paint.getTextWidths(str, textLength);
                for(int i = 1 ; i < textLength.length ; i++) {
                    textLength[0] += textLength[i];
                }
            }
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX,normalSize);
        } else {
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX,normalSize);
        }
        text.setText(str);
    }
    
	/**
     * 自動計算字串長度 且最後會回傳字型大小
     * @param text textview
     * @param str 字串
     * @param maxWidth 寬度
     * @param normalSize 給予textview原本標準文字大小(PX)
     */
    public static float getAutoTextSize(TextView text, String str, int maxWidth, float normalSize)
	{
        if(str != null && str.length() > 0 && maxWidth > 0)
		{
            float [] textLength = new float[str.length()];
            Paint paint = new Paint();
            paint.setTextSize(normalSize);
            paint.getTextWidths(str, textLength);

            for(int i = 1 ; i < textLength.length ; i++)
			{
                textLength[0] += textLength[i];
            }

            while(textLength[0] > maxWidth)
			{
                normalSize -= 2;
                paint.setTextSize(normalSize);
                paint.getTextWidths(str, textLength);

                for(int i = 1 ; i < textLength.length ; i++)
				{
                    textLength[0] += textLength[i];
                }
            }
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX,normalSize);
        }
		else
		{
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX,normalSize);
        }

        text.setText(str);
        return normalSize;
    }
    
    
    
    /**
     * 自動計算字串長度
     * @param text textview
     * @param str 字串
     * @param maxWidth 寬度
     * @param normalSize 給予textview原本標準文字大小(PX)
     * @param color 文字顏色
     */
    public static void setAutoText(TextView text, String str, int maxWidth, float normalSize, int color) {
        if(str != null && str.length() > 0 && maxWidth > 0) {
            float [] textLength = new float[str.length()];
            Paint paint = new Paint();
            paint.setTextSize(normalSize);
            paint.getTextWidths(str, textLength);
            for(int i = 1 ; i < textLength.length ; i++) {
                textLength[0] += textLength[i];
            }
            while (textLength[0] > maxWidth) {
                normalSize -= 2;
                paint.setTextSize(normalSize);
                paint.getTextWidths(str, textLength);
                for(int i = 1 ; i < textLength.length ; i++) {
                    textLength[0] += textLength[i];
                }
            }
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX,normalSize);
        } else {
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX,normalSize);
        }
        text.setText(str);
        text.setTextColor(color);
    }
    
    /**
     * 自動計算字串長度 且最後會回傳字型大小
     * @param text textview
     * @param str 字串
     * @param maxWidth 寬度
     * @param normalSize 給予textview原本標準文字大小(PX)
     * @param color 文字顏色
     */
    public static float getAutoText(TextView text, String str, int maxWidth, float normalSize, int color) {
        if(str != null && str.length() > 0 && maxWidth > 0) {
            float [] textLength = new float[str.length()];
            Paint paint = new Paint();
            paint.setTextSize(normalSize);
            paint.getTextWidths(str, textLength);
            for(int i = 1 ; i < textLength.length ; i++) {
                textLength[0] += textLength[i];
            }
            while (textLength[0] > maxWidth) {
                normalSize -= 2;
                paint.setTextSize(normalSize);
                paint.getTextWidths(str, textLength);
                for(int i = 1 ; i < textLength.length ; i++) {
                    textLength[0] += textLength[i];
                }
            }
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX,normalSize);
        } else {
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX,normalSize);
        }
        text.setText(str);
        text.setTextColor(color);
        return normalSize;
    }
    
    public static int getLimitFontSize(final Activity activity, final int maxWidth, final String text, final float size, final int orientation)
	{
		float fontSize = getRatioWidth(activity, (int)size);
		int textWidth = 0;
		
		for(int i = 0 ; i < 6 ; i++)
		{
			textWidth = getTextWidth(text, fontSize);

			if(textWidth < maxWidth)
			{
				break;
			}
			else
			{
				fontSize -= 2;
			}
		}

		return (int)fontSize;
	}

	
	public static TextView drawTextView(final Context context, final String text, final int size, final int color, final int backgroundColor, final int gravity)
	{
		return drawTextView(context, text, size, true, color, false, backgroundColor, gravity);
	}
	
	public static TextView drawTextView(final Context context, final String text, final int size, final boolean zoomPixelSize, final int color, final boolean isResource, final int backgroundColorOrResource, final int gravity)
	{
		return drawTextView(context, text, size, zoomPixelSize, Configuration.ORIENTATION_PORTRAIT, color, isResource, backgroundColorOrResource, gravity);
	}

	public static TextView drawTextView(final Context context, final String text, final int size, final boolean zoomPixelSize, final int orientation , final int color, final boolean isResource, final int backgroundColorOrResource, final int gravity)
	{
		TextView textView = new TextView(context);
		
		if(true == zoomPixelSize)
		{
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getRatioWidth((Activity)context, size));
		}
		else
		{
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
		}
		
		textView.setText(text);
		textView.setTextColor(color);
		textView.setGravity(gravity);
		
		if(false == isResource && backgroundColorOrResource != -999)
		{
			textView.setBackgroundColor(backgroundColorOrResource);
		}
		else if(true == isResource)
		{
			textView.setBackgroundResource(backgroundColorOrResource);
		}
		
		textView.setSingleLine();
		textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
		
		return textView;
	}
}
