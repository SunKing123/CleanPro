package com.xiaoniu.cleanking.ui.newclean.contact;

import com.xiaoniu.cleanking.base.BaseView;
import com.xiaoniu.cleanking.mvp.IBaseModel;
import com.xiaoniu.cleanking.mvp.IBasePresenter;
import com.xiaoniu.cleanking.mvp.IBaseView;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;

/**
 * Created by xinxiaolong on 2020/5/27.
 * email：xinxiaolong123@foxmail.com
 */
public interface CleanMainTopViewContact {

    interface View {
        //设置互动广告图片
        void loadAdvImage(String url);
        //加载权限提示图标
        void loadPermissionTipImage();
        //是否显示权限提示标
        void setVisibleImage(int visible);
        //是否显示权限提示悬浮框
        void setVisibleFloatingView(int visible);
        //topView点击
        void onTopViewClick();
    }

    interface Presenter extends IBasePresenter{

        void onCreate();
        //获取焦点
        void onResume();
        //滚动事件传递
        void onScroll(boolean isSuckTop);
        //用户点击了顶部提示布局
        void onTopViewClick();
        //销毁
        void onDestroy();
    }

    interface Model extends IBaseModel{
        void getInteractionSwitch(Common4Subscriber<InteractionSwitchList> commonSubscriber);
    }
}
