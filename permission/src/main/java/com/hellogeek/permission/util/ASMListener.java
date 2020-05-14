package com.hellogeek.permission.util;


import com.hellogeek.permission.bean.ASBase;
import com.hellogeek.permission.bean.ASIntentBean;
import com.hellogeek.permission.bean.ASStepBean;

public interface ASMListener {
    void complete(ASBase arg1);
    void pause(ASStepBean arg1);
    void jumpActivity(ASIntentBean intent);
}