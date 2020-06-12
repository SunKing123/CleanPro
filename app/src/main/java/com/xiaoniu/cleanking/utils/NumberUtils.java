package com.xiaoniu.cleanking.utils;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoniu.common.utils.DateUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

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

    public static long getLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static long string2Long(String str) {
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

    /**
     * 两个整数之间的随机数
     *
     * @param start
     * @param end
     * @return
     */
    public static String mathRandom(int start, int end) {
        int number = (int) (start + Math.random() * (end - start + 1));
        return String.valueOf(number);
    }

    /**
     * 两个整数之间的随机数
     *
     * @param start
     * @param end
     * @return
     */
    public static int mathRandomInt(int start, int end) {
        int number = (int) (start + Math.random() * (end - start + 1));
        return number;
    }


    /**
     * 产生n位随机数
     *
     * @return
     */
    public static long generateRandomNumber(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("随机数位数必须大于0");
        }
        return (long) (Math.random() * 9 * Math.pow(10, n - 1)) + (long) Math.pow(10, n - 1);
    }


    /**
     * 获取文件中保存的uuid，可能是生成的随机字符串
     *
     * @param context
     * @return
     */
    public static synchronized String getSpUuid(Context context) {
        String uuid = null;
        if (TextUtils.isEmpty(uuid)) {
            if (TextUtils.isEmpty(uuid)) {
                //获取失败，则生成随机数保存
                //"uuid": 接口请求传的"aa3a1cc2ab24c049_28b2748c"
                uuid = "jk_clean" + DateUtils.getCurrentSimpleYYYYMMDDHHMM() + "_" + generateRandomNumber(8);
            }

        }
        return uuid;
    }

    /**
     * 生成一个startNum 到 endNum之间的随机数(不包含endNum的随机数)
     *
     * @param startNum
     * @param endNum
     * @return
     */
    public static int getNum(int startNum, int endNum) {
        if (endNum > startNum) {
            Random random = new Random();
            return random.nextInt(endNum - startNum) + startNum;
        }
        return 0;
    }

    /**
     * 生成一个startNum 到 endNum之间的随机数(不包含endNum的随机数)
     *
     * @param startNum
     * @param endNum
     * @return
     */
    public static String getNumStr(int startNum, int endNum) {
        if (endNum > startNum) {
            Random random = new Random();
            return (random.nextInt(endNum - startNum) + startNum) + "%";
        }
        return "";
    }
}
