package com.installment.mall.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证手机号格式工具类
 * Created by fengpeihao on 2017/2/9.
 */

public class PhoneUtils {

    public static boolean isPhone(String phone) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }
}
