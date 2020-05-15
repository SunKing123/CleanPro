package com.xiaoniu.cleanking.ad.bean;

import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.List;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ad.bean
 * @ClassName: TemplateAdRespo
 * @Description:
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/13 10:09
 */

public class TemplateAdResponse {
    //1 优量会 2穿山甲
    public String union;
    public List<NativeExpressADView> ylhAdResponse;
    public List<TTNativeExpressAd> csjAdResponse;

    public TemplateAdResponse(String union, List<NativeExpressADView> ylhAdResponse, List<TTNativeExpressAd> csjAdResponse) {
        this.union = union;
        this.ylhAdResponse = ylhAdResponse;
        this.csjAdResponse = csjAdResponse;
    }
}
