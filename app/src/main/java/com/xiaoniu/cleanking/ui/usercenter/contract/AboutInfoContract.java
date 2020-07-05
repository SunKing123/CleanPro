package com.xiaoniu.cleanking.ui.usercenter.contract;

import android.app.Activity;
import android.content.Context;

import com.jess.arms.mvp.IView;
import com.jess.arms.mvp.IModel;
import com.xiaoniu.cleanking.ui.main.bean.AppVersion;

import io.reactivex.Observable;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/05/2020 16:29
 */
public interface AboutInfoContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        Activity getActivity();
        void setShowVersion(AppVersion result);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<AppVersion> getVersion(Context ctx);
    }
}
