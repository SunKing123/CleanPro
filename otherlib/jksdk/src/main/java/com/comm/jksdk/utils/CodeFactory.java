package com.comm.jksdk.utils;

import android.text.TextUtils;

import java.util.Map;

/**
 * @ProjectName: GeekAdSdk
 * @Package: com.comm.jksdk.utils
 * @ClassName: CodeFactory
 * @Description: 返回码定义类
 * @Author: fanhailong
 * @CreateDate: 2019/11/13 19:23
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/11/13 19:23
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class CodeFactory {


    public final static int UNKNOWN = 900;
    public final static int LOCAL_INFO_EMPTY = 101; //本地配置信息为空

    public final static int SUCCESS = 9000000;
    public static final int ERROR_EMPTY = 9000001;//联盟返回物料空_错误码
    public static final int ERROR_RENDER_FAIL = 9000002;//联盟渲染失败_错误码


    private final static Map<Integer, String> map = CollectionUtils.createMap();

    static {
        map.put(900, "未知错误");
        //请求广告失败码
        map.put(LOCAL_INFO_EMPTY, "本地配置信息为空");
        map.put(ERROR_EMPTY, "联盟返回物料空");
        map.put(ERROR_RENDER_FAIL, "联盟渲染失败");
        map.put(SUCCESS, "成功");
        map.put(102, "未知错误");
        map.put(103, "未知错误");
        map.put(104, "未知错误");
    }

    public static String getError(int code){
        String msg = map.get(code);
        if (TextUtils.isEmpty(msg)) {
            return "未知错误，错误码："+code;
        }
        return msg;
    }
}
