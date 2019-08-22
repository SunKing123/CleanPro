package com.xiaoniu.common.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by zy on 2017/6/15.
 */

public class KeyboardUtils {
    /**
     * 打软键盘
     * @param mEditText 输入框
     */
    public static void openKeyboard(View mEditText) {
        InputMethodManager imm = (InputMethodManager) ContextUtils.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public static boolean openKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            return imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
        return false;
    }

    /**
     * 关闭软键盘
     *
     * @param mEditText 输入框
     */
    public static void closeKeyboard(View mEditText) {
        InputMethodManager imm = (InputMethodManager) ContextUtils.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    public static void toggleSoftInput() {
        InputMethodManager imm = (InputMethodManager) ContextUtils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static boolean isActive() {
        InputMethodManager imm = (InputMethodManager) ContextUtils.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }
}