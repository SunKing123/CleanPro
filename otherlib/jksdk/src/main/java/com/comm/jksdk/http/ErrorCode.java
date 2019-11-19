package com.comm.jksdk.http;

/**
 * 代码描述<p>
 *
 *     错误代码描述
 *
 * @author anhuiqing
 * @since 2019/4/12 13:22
 */
public class ErrorCode {
    public static final int SUCCESS = 0;
    public static final int ERROR = -1;//系统默认错误码
    public static final int ERROR_ABNORMAL = 50000;//异常捕获默认异常错误码
    public static final int ERROR_SYSTEM = 1000;//系统繁忙，请稍后重试
    public static final int ERROR_PARAMETER = 10001;//参数错误
    public static final int ERROR_UUID_NO_EMPTY = 10002;//设备号uuid不能为空!
    public static final int ERROR_AGENCY_STATE_NO  = 10004;//待办信息的状态不存在
    public static final int ERROR_PARAMETER_REQUIRED = 10005;//参数为必填
    public static final int ERROR_HEADER_PARAMETER = 10013;//消息头必填参数不全
    public static final int ERROR_TOKEN_VALIDATE_FAILE = 11111;//token验证失败
}
