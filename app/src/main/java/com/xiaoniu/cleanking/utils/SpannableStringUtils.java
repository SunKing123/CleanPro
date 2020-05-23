package com.xiaoniu.cleanking.utils;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import java.util.List;

public class SpannableStringUtils {

    public static SpannableStringBuilder createSpannableString(List<SpannableBean> spannableBeans) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (SpannableBean bean : spannableBeans) {
            SpannableString spannableString = new SpannableString(bean.label);
            spannableString.setSpan(new ForegroundColorSpan(bean.color), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (bean.onClickListener != null) {
                spannableString.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        bean.onClickListener.onClick(widget);
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        ds.bgColor = Color.parseColor("#FFFFFF");
                        ds.linkColor = bean.color;
                    }
                }, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            builder.append(spannableString);
        }
        return builder;
    }

    public static class SpannableBean {
        public String label;
        public int color;
        public View.OnClickListener onClickListener;

        public SpannableBean(String label, int color, View.OnClickListener onClickListener) {
            this.label = label;
            this.color = color;
            this.onClickListener = onClickListener;
        }
    }
}
