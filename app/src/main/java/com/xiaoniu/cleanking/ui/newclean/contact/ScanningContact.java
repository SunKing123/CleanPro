package com.xiaoniu.cleanking.ui.newclean.contact;

import com.xiaoniu.cleanking.mvp.IBaseModel;
import com.xiaoniu.cleanking.mvp.IBasePresenter;
import com.xiaoniu.cleanking.mvp.IBaseView;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;

import java.util.LinkedHashMap;
import java.util.List;

public interface ScanningContact {

    interface View extends IBaseView {

        /**
         * 展示扫描到的垃圾总量
         *
         * @param totalResult 垃圾总量描述
         * @param unit        垃圾总量单位描述
         */
        void setScanningJunkTotal(String totalResult, String unit);

        /**
         * 展示当前扫描文件路径
         */
        void setScanningFilePath(String filePath);

        /**
         * 根据扫描文件的容量，配置展示扫描背景颜色
         *
         * @param oldColor 上次展示颜色
         * @param newColor 当前应该展示颜色
         */
        void setScanningBackgroundColor(int oldColor, int newColor);

        /**
         * 展示初始化扫描模型
         */
        void setInitScanningModel(List<JunkGroup> scanningModelList);

        /**
         * 扫描完成结果集
         */
        void setScanningFinish(LinkedHashMap<ScanningResultType, JunkGroup> scanningModelList);

        /**
         * 总共的扫描时间
         */
        void setScanningCountTime(long scanningCountTime);

        /**
         * 文件总数
         */
        void setScanningFileCount(int fileCount);
    }

    interface IPresenter extends IBasePresenter {

        /**
         * 扫描垃圾文件之前准备工作
         */
        void readyScanningJunk();

        /**
         * 开始扫描垃圾文件
         */
        void scanningJunk();
    }

    interface IModel extends IBaseModel {

    }
}
