package com.xiaoniu.cleanking.ui.login.contract;

import android.app.Activity;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.xiaoniu.cleanking.ui.login.bean.LoginDataBean;

import io.reactivex.Observable;
import okhttp3.RequestBody;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 07/02/2020 19:16
 */
public interface LoginWeiChatContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        Activity getActivity();

        void dealLoginResult(int flag, LoginDataBean loginDataBean);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<LoginDataBean> loginWithWeiChat(RequestBody body);

        Observable<LoginDataBean> bindingWeiChat(RequestBody body);
    }
}
