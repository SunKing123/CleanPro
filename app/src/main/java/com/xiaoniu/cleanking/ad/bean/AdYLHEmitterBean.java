package com.xiaoniu.cleanking.ad.bean;

import com.qq.e.ads.nativ.NativeExpressADView;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ad.bean
 * @ClassName: AdYLHEmitterBean
 * @Description:
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/14 21:49
 */

public class AdYLHEmitterBean {
    public NativeExpressADView nativeExpressADView;
    public int index;
    public long time;
/*
     1,加载成功
     2,渲染成功onRenderSuccess
     3,关闭
     4，点击
     5,跳过
     6，剩余时间
     7,倒计时或点击后消失
     */

    public int type;

    public AdYLHEmitterBean() {
    }

    public AdYLHEmitterBean(NativeExpressADView nativeExpressADView, int index, int type) {
        this.nativeExpressADView = nativeExpressADView;
        this.index = index;
        this.type = type;
    }

    public AdYLHEmitterBean(int index, long time, int type) {
        this.index = index;
        this.time = time;
        this.type = type;
    }
}
