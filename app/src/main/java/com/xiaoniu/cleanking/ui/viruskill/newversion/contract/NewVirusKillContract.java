package com.xiaoniu.cleanking.ui.viruskill.newversion.contract;

import com.jess.arms.mvp.IPresenter;
import com.jess.arms.mvp.IView;

/**
 * Created by xinxiaolong on 2020/7/20.
 * email：xinxiaolong123@foxmail.com
 */
public interface NewVirusKillContract {

    /**
     * 病毒扫描view
     */
    interface VirusScanView extends IView {
        //开始扫描loading动画
        void startScanLoading();
        //设置扫描标题
        void setScanTitle(String title);
        //设置隐私条目
        void setPrivacyCount(int count);
        //添加隐私扫描条目
        void addScanItem(String text);

        //显示查杀病毒的应用列表
        void showScanVirusIcons();
        //病毒扫描完成
        void setScanVirusComplete();

        //添加网络扫描条目
        void addScanNetWorkItem(String text);
        //扫描完毕
        void scanComplete();
    }

    /**
     * 病毒扫描present
     */
    interface VirusScanPresent extends IPresenter {
        //扫描进度回调
        void onScanLoadingProgress(int num);
    }


    /**
     * 病毒扫描结果view
     */
    interface VirusScanResultView extends IView {

    }

    /**
     * 病毒扫描结果present
     */
    interface VirusScanResultPresent extends IPresenter {

    }

    /**
     * 病毒清理view
     */
    interface VirusCleanView extends IView {

    }

    /**
     * 病毒清理present
     */
    interface VirusCleanPresent extends IPresenter {

    }

}
