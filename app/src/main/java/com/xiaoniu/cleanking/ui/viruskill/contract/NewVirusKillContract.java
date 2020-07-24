package com.xiaoniu.cleanking.ui.viruskill.contract;

import android.content.Context;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IPresenter;
import com.jess.arms.mvp.IView;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.viruskill.model.ScanTextItemModel;

import java.util.ArrayList;

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
        void addScanPrivacyItem(ScanTextItemModel model);
        //隐私扫描完成
        void setScanPrivacyComplete();

        //显示查杀病毒的应用列表
        void showScanVirusIcons(ArrayList<FirstJunkInfo> list);
        //病毒扫描完成
        void setScanVirusComplete();

        void startScanNetwork();
        //添加网络扫描条目
        void addScanNetWorkItem(ScanTextItemModel model);
        //扫描完毕
        void scanAllComplete(ArrayList<ScanTextItemModel> pList, ArrayList<ScanTextItemModel> nList);

        Context getContext();
    }

    interface LieModel extends IModel{

    }
    /**
     * 病毒扫描present
     */
    interface VirusScanPresenter extends IPresenter {
        void onCreate();
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
