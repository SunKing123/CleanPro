package com.xiaoniu.cleanking.ui.newclean.contact;

import com.xiaoniu.cleanking.mvp.IBaseModel;
import com.xiaoniu.cleanking.mvp.IBasePresenter;
import com.xiaoniu.cleanking.mvp.IBaseView;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface ScanCleanContact {

    interface View extends IBaseView {

        /**
         * 设置垃圾清理总数
         */
        void setTotalJunkCount(String totalSize, String unit);

        /**
         * 开始清理垃圾
         */
        void setStartCleanJunk(float number, String unit);

        /**
         * 设置获取到可以加速的应用名单Android O以下的获取最近使用情况
         */
        void getAccessListBelow(ArrayList<FirstJunkInfo> strings);

        /**
         * 清空数据完成
         */
        void setCleanFinish();

        /**
         * 清理垃圾完成
         */
        void setCleanJunkOver();
    }

    interface Model extends IBaseModel {

    }

    interface IPresenter extends IBasePresenter {

        /**
         * 开始垃圾清理动作
         */
        void startClean(LinkedHashMap<ScanningResultType, ArrayList<FirstJunkInfo>> junkContentMap);

        /**
         * 获取到可以加速的应用名单Android O以下的获取最近使用情况
         */
        void getAccessListBelow();
    }
}
