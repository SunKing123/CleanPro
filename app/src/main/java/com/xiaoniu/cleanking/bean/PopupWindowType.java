package com.xiaoniu.cleanking.bean;

import androidx.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@StringDef({PopupWindowType.POPUP_RED_PACKET,
        PopupWindowType.POPUP_RETAIN_WINDOW,})
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.PARAMETER})
public @interface PopupWindowType {

    String POPUP_RED_PACKET = "red_envelope_pop_up_window";
    String POPUP_RETAIN_WINDOW = "retain_the_pop_up_window";
}
