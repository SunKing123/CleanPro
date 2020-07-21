package com.xiaoniu.cleanking.ui.viruskill.newversion.presenter;

import com.jess.arms.mvp.BasePresenter;
import com.xiaoniu.cleanking.ui.viruskill.newversion.contract.NewVirusKillContract;
import com.xiaoniu.cleanking.ui.viruskill.newversion.model.PrivacyDataStore;
import com.xiaoniu.cleanking.ui.viruskill.newversion.model.PrivacyItemModel;
import com.xiaoniu.cleanking.ui.viruskill.old.contract.VirusKillContract;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by xinxiaolong on 2020/7/20.
 * email：xinxiaolong123@foxmail.com
 */
public class VirusScanPresenter extends BasePresenter<VirusKillContract.Model, NewVirusKillContract.VirusScanView> implements NewVirusKillContract.VirusScanPresenter {

    private final int PRIVACY_PROGRESS = 40;
    private final int VIRUS_PROGRESS = 60;
    private final int NETWORK_PROGRESS = 100;

    //隐私扫描文案下标
    private int p_index = -1;
    //网络扫描文案下标
    private int n_index = -1;

    private int privacyWarnNum = 0;
    private PrivacyDataStore store;
    private List<PrivacyItemModel> pList;


    @Inject
    public VirusScanPresenter(NewVirusKillContract.VirusScanView rootView) {
        super(null, rootView);
    }

    @Override
    public void onCreate() {
        p_index = -1;
        n_index = -1;
        
        store = PrivacyDataStore.getInstance();
        //随机标记隐私风险
        pList= store.randomMarkWarning();
        //获取标记风险个数
        privacyWarnNum = store.getCacheNeedMarksIds().length;
    }

    /**
     * 扫描进度监听
     * @param progress
     */
    @Override
    public void onScanLoadingProgress(int progress) {
        dispatchHandle(progress);
    }

    /**
     * 分发扫描进度逻辑
     *
     * @param progress
     */
    private void dispatchHandle(int progress) {
        if (progress == 0) {
            //开始扫描隐私风险
            handleStartPrivacy();
        } else if (progress < PRIVACY_PROGRESS) {
            //隐私风想扫描中
            handlePrivacy(progress);
        } else if (progress == PRIVACY_PROGRESS) {
            //隐私风险扫描结束
            handlePrivacyEnd();
            //病毒查杀扫描开始
            handleVirusStart();
        } else if (progress < VIRUS_PROGRESS) {
            //病毒查杀扫描中
            handleVirus(progress);
        } else if (progress == VIRUS_PROGRESS) {
            //病毒查杀结束
            handleVirusEnd();
            //网络风险扫描开始
            handleNetworkStart();
        } else if (progress > NETWORK_PROGRESS) {
            //网络风险扫描中
            handleNetwork(progress);
        } else {
            //所有扫描任务结束
            handleAllComplete();
        }
    }


    /*
     * *********************************************************************************************************************************************************
     * **************************************************************privacy start******************************************************************************
     * *********************************************************************************************************************************************************
     */

    /**
     * 开始扫描隐私风险
     */
    private void handleStartPrivacy() {
        mRootView.setScanTitle("隐私风险扫描中...");
    }

    /**
     * 处理扫描隐私逻辑
     * 这里需要添加8条扫描固定项，总时长40格。每5格添加一个数据
     *
     * @param progress 0~40
     */
    private void handlePrivacy(int progress) {
        int thisIndex = progress / 5;
        if (thisIndex > p_index) {
            //添加扫描文案
            p_index = thisIndex;
            mRootView.addScanPrivacyItem(pList.get(p_index));
        }
    }

    /**
     * 隐私扫描完成
     */
    private void handlePrivacyEnd() {
        mRootView.setPrivacyCount(privacyWarnNum);
    }

    /*
     * *********************************************************************************************************************************************************
     * **************************************************************virus start********************************************************************************
     * *********************************************************************************************************************************************************
     */

    /**
     * 处理开始扫描病毒逻辑
     */
    private void handleVirusStart() {
        mRootView.setScanTitle("病毒应用扫描中...");
    }

    /**
     * 处理扫描病毒逻辑
     *
     * @param progress 40~60
     */
    private void handleVirus(int progress) {

    }


    /**
     * 病毒扫描完成
     */
    private void handleVirusEnd() {

    }


    /*
     * *********************************************************************************************************************************************************
     * *************************************************************network start*******************************************************************************
     * *********************************************************************************************************************************************************
     */

    /**
     * 处理开始扫描网络逻辑
     */
    private void handleNetworkStart() {
        mRootView.setScanTitle("网络安全描中...");
    }

    /**
     * 处理扫描网络逻辑
     *
     * @param progress 60~100
     */
    private void handleNetwork(int progress) {

    }

    /**
     * 所有扫描完成
     */
    private void handleAllComplete() {

    }

}
