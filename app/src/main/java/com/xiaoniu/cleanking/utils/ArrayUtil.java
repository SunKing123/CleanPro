package com.xiaoniu.cleanking.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.utils
 * @ClassName: ArrayUtil
 * @Description: 数组工具类
 * @Author: LiDing
 * @CreateDate: 2020/5/17 16:31
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/17 16:31
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ArrayUtil {

    /**
     * 数组元素中是否包含某个字符串
     *
     * @param list
     * @param str
     * @return
     */
    public static Boolean arrayContainsStr(List<String> list, String str) {
        for (String item : list) {
            if (str.contains(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 数组中是否包含某个元素
     *
     * @param list
     * @param str
     * @return
     */
    public static Boolean arrayEqualsStr(List<String> list, String str) {
        return false;
    }


}
