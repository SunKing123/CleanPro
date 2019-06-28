package com.installment.mall.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 处理小数位数工具类
 * Created by fengpeihao on 2017/2/15.
 */

public class NumberUtils {
    private static DecimalFormat mFormat2 = new DecimalFormat("#0.00");
    private static DecimalFormat mFormat1 = new DecimalFormat("#0.0");
    private static DecimalFormat format = new DecimalFormat("#0");

    /**
     * 保留两位小数字符串
     *
     * @param d
     * @return
     */
    public static String getFloatStr2(double d) {
        try {
            return mFormat2.format(d);
        } catch (Exception e) {
            return "0.00";
        }
    }

    /**
     * 保留两位小数字符串
     *
     * @param str
     * @return
     */
    public static String getFloatStr2(String str) {
        try {
            return mFormat2.format(Double.parseDouble(str));
        } catch (Exception e) {
            return "0.00";
        }
    }

    /**
     * 保留一位小数字符串
     *
     * @param d
     * @return
     */
    public static String getFloatStr1(double d) {
        try {
            return mFormat1.format(d);
        } catch (Exception e) {
            return "0.0";
        }
    }

    /**
     * 保留一位小数字符串
     *
     * @param str
     * @return
     */
    public static String getFloatStr1(String str) {
        try {
            return mFormat1.format(Double.parseDouble(str));
        } catch (Exception e) {
            return "0.0";
        }
    }

    /**
     * 整数字符串
     *
     * @param f
     * @return
     */
    public static String getIntegerStr(double f) {
        try {
            return format.format(f);
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * 整数字符串
     *
     * @param str
     * @return
     */
    public static String getIntegerStr(String str) {
        try {
            return format.format(Double.parseDouble(str));
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * @param str
     * @return
     */
    public static double getDouble(String str) {
        try {
            return getRound(Double.parseDouble(str), 4);
        } catch (Exception e) {
            return 0.00;
        }
    }

    public static float getFloat(String str) {
        try {
            return getRound(Float.parseFloat(str), 4);
        } catch (Exception e) {
            return 0.00f;
        }
    }

    public static int getInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static long string2Long(String str){
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return 0L;
        }
    }

    public static double getAutoDecimal(String str) {
        try {
            String[] split = str.split(".");
            if (split.length > 1) {
                String s = split[1];
                while (s.endsWith("0")) {
                    s = s.substring(0, s.length() - 1);
                }
                return getDouble(split[0] + "." + s);
            } else {
                return getInteger(str);
            }
        } catch (Exception e) {
            return 0.00;
        }
    }

    public static String getAutoDecimal(double d) {
        try {
            String str = String.valueOf(getRound(d, 4));
            String[] split = str.split("\\.");
            if (split.length == 2) {
                String s = split[1];
                while (s.endsWith("0")) {
                    s = s.substring(0, s.length() - 1);
                }
                if (TextUtils.isEmpty(s)) {
                    return split[0] + ".00";
                }
                if (s.length() >= 2) {
                    return split[0] + "." + s;
                }
                return split[0] + "." + s + "0";
            } else {
                return split[0] + ".00";
            }
        } catch (Exception e) {
            return "0.00";
        }
    }

    /**
     * 返回整数（大于等于该数的那个最近值）
     *
     * @param d
     * @return
     */
    public static int getRoundCeilingInt(double d) {
        BigDecimal decimal = new BigDecimal(d);
        BigDecimal bigDecimal = decimal.setScale(0, BigDecimal.ROUND_UP);
        return bigDecimal.intValue();
    }

    /**
     * 返回double（大于等于该数的那个最近值）
     *
     * @param d
     * @param newScale 0
     * @return
     */
    public static double getRoundCeiling(double d, int newScale) {
        BigDecimal decimal = new BigDecimal(d);
        BigDecimal bigDecimal = decimal.setScale(newScale, BigDecimal.ROUND_UP);
        return bigDecimal.doubleValue();
    }

    /**
     * 对double数据进行取精度(四舍五入)
     *
     * @param d
     * @param newScale
     * @return
     */
    public static double getRound(double d, int newScale) {
        BigDecimal decimal = new BigDecimal(d);
        BigDecimal bigDecimal = decimal.setScale(newScale, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.doubleValue();
    }

    /**
     * 对float数据进行取精度(四舍五入)
     *
     * @param d
     * @param newScale
     * @return
     */
    public static float getRound(float d, int newScale) {
        BigDecimal decimal = new BigDecimal(d);
        BigDecimal bigDecimal = decimal.setScale(newScale, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.floatValue();
    }

    /**
     * 查询数值在数组中的索引
     *
     * @param arr
     * @param value
     * @return
     */
    public static int getPosition(int[] arr, int value) {
        for (int i = 0; i < arr.length; i++) {
            if (value == arr[i]) {
                return i;
            }
        }
        return 0;
    }

    public static float subtract(float totalFee, float totalFeeAfter) {
        BigDecimal totalFeeDecimal = new BigDecimal(
                Float.toString(totalFee));
        BigDecimal totalFeeAfterDecimal = new BigDecimal(
                Float.toString(totalFeeAfter));
        return totalFeeDecimal.subtract(
                totalFeeAfterDecimal).
                floatValue();
    }

    public static float plus(float totalFee, float totalFeeAfter) {
        BigDecimal totalFeeDecimal = new BigDecimal(
                Float.toString(totalFee));
        BigDecimal totalFeeAfterDecimal = new BigDecimal(
                Float.toString(totalFeeAfter));
        return totalFeeDecimal.add(
                totalFeeAfterDecimal).
                floatValue();
    }
}
