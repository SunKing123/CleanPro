package com.xiaoniu.cleanking.ui.newclean.contact;

import com.xiaoniu.cleanking.mvp.IBaseModel;
import com.xiaoniu.cleanking.mvp.IBasePresenter;
import com.xiaoniu.cleanking.mvp.IBaseView;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.newclean.bean.JunkResultWrapper;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;

import java.util.LinkedHashMap;
import java.util.List;

public interface ScanResultContact {

    interface View extends IBaseView {

        /**
         * 展示首次数据展示模型
         */
        void setInitSubmitResult(List<JunkResultWrapper> junkResultWrappers);

        /**
         * 非首次展示数据模型
         */
        void setSubmitResult(List<JunkResultWrapper> buildJunkDataModel);

        /**
         * 设置扫描到的垃圾总大小
         *
         * @param totalSize 垃圾总量
         * @param unit      垃圾总量单位
         */
        void setJunkTotalResultSize(String totalSize, String unit);

        /**
         * 选中状态的垃圾总量
         */
        void setCheckedJunkResult(String resultSize);
    }

    interface IPresenter extends IBasePresenter {

        /**
         * 构建扫描结果展示模型
         */
        void buildJunkResultModel(LinkedHashMap<ScanningResultType, JunkGroup> scanningResultMap);

        /**
         * 更新视图折叠状态
         */
        void updateExpendState(JunkResultWrapper data);

        /**
         * 更新垃圾类别选中状态
         */
        void updateJunkTypeCheckSate(JunkResultWrapper data);

        /**
         * 更新垃圾内容选中状态
         */
        void updateJunkContentCheckState(JunkResultWrapper data);
    }

    interface IModel extends IBaseModel {

    }
}
