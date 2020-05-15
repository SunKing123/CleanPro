package com.xiaoniu.cleanking.ad.mvp.contract;


import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.splash.SplashADListener;
import com.xiaoniu.cleanking.ad.bean.AdRequestBean;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;
import com.xiaoniu.cleanking.ad.bean.AdYLHEmitterBean;
import com.xiaoniu.cleanking.ad.interfaces.AdShowCallBack;
import com.xiaoniu.cleanking.base.BaseView;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;


/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ad
 * @ClassName: AdContract
 * @Description:
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/9 14:52
 */
public interface AdContract {

    interface View extends BaseView {

    }

    interface Presenter {
        void requestAd(AdRequestParamentersBean adRequestParamentersBean, AdShowCallBack adShowCallBack);
    }


    interface Model {
        Observable<AdYLHEmitterBean> getYLHTemplateAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean);

        Observable<List<TTNativeExpressAd>> getCSJTemplateAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean);

        Observable<AdYLHEmitterBean> getYLHSplashAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean);

        Observable<TTSplashAd> getCSJSplashAd(AdRequestParamentersBean adRequestParamentersBean, AdRequestBean adRequestBean);
    }
}
