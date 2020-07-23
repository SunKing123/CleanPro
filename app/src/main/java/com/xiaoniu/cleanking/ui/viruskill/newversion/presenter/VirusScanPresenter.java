package com.xiaoniu.cleanking.ui.viruskill.newversion.presenter;

import com.jess.arms.mvp.BasePresenter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.viruskill.newversion.contract.NewVirusKillContract;
import com.xiaoniu.cleanking.ui.viruskill.newversion.model.LieModel;
import com.xiaoniu.cleanking.ui.viruskill.newversion.model.NetworkDataStore;
import com.xiaoniu.cleanking.ui.viruskill.newversion.model.PrivacyDataStore;
import com.xiaoniu.cleanking.ui.viruskill.newversion.model.ScanTextItemModel;
import com.xiaoniu.cleanking.ui.viruskill.old.contract.VirusKillContract;
import com.xiaoniu.cleanking.utils.AndroidUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinxiaolong on 2020/7/20.
 * email：xinxiaolong123@foxmail.com
 */
public class VirusScanPresenter extends BasePresenter<VirusKillContract.Model, NewVirusKillContract.VirusScanView> implements NewVirusKillContract.VirusScanPresenter {

    private final int[] P_START_RANGE = {0, 2};
    private final int[] P_PROCESS_RANGE = {3, 36};
    private final int[] P_END_RANGE = {36, 40};

    private final int[] V_START_RANGE = {41, 43};
    private final int[] V_PROCESS_RANGE = {44, 58};

    private final int[] N_START_RANGE = {59, 61};
    private final int[] N_PROCESS_RANGE = {62, 96};

    private final int P_START = 1;
    private final int P_PROCESS = 2;
    private final int P_END = 3;
    private final int V_START = 4;
    private final int V_PROCESS = 5;
    private final int N_START = 6;
    private final int N_PROCESS = 7;
    private final int COMPLETE = 8;

    //隐私扫描文案下标
    private int p_index = -1;
    //网络扫描文案下标
    private int n_index = -1;
    //隐私条目数据
    private PrivacyDataStore pStore;
    private List<ScanTextItemModel> pList;
    //网络条目数据
    private NetworkDataStore nStore;
    private List<ScanTextItemModel> nList;
    //病毒条目数据
    private ArrayList<FirstJunkInfo> iconList;

    public VirusScanPresenter(NewVirusKillContract.VirusScanView rootView) {
        super(new LieModel(), rootView);
    }

    @Override
    public void onCreate() {
        p_index = -1;
        n_index = -1;

        pStore = PrivacyDataStore.getInstance();
        //随机标记隐私风险
        pList = pStore.randomMarkWarning();

        nStore = NetworkDataStore.getInstance();
        //随机标记隐私风险
        nList = nStore.randomMarkWarning();

        //初始化图标信息
        iconList = AndroidUtil.getRandomMaxCountInstallApp(mRootView.getContext(), 10);

    }

    /**
     * 扫描进度监听
     *
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
        int range = getRange(progress);
        switch (range) {
            case P_START:
                handlePrivacyStart();
                break;
            case P_PROCESS:
                handlePrivacyProcess(progress);
                break;
            case P_END:
                handlePrivacyEnd();
                break;
            case V_START:
                handleVirusStart();
                break;
            case V_PROCESS:
                handleVirusProcess(progress);
                break;
            case N_START:
                handleVirusEnd();
                handleNetworkStart();
                break;
            case N_PROCESS:
                handleNetworkProcess(progress);
                break;
            default:
                handleAllComplete();
                break;
        }
    }

    /**
     * 获取当前进度属于哪个阶段
     * @param progress
     * @return
     */
    private int getRange(int progress) {
        if (inRange(progress, P_START_RANGE)) {
            return P_START;
        } else if (inRange(progress, P_PROCESS_RANGE)) {
            return P_PROCESS;
        }  else if (inRange(progress, P_END_RANGE)) {
            return P_END;
        } else if (inRange(progress, V_START_RANGE)) {
            return V_START;
        } else if (inRange(progress, V_PROCESS_RANGE)) {
            return V_PROCESS;
        } else if (inRange(progress, N_START_RANGE)) {
            return N_START;
        } else if (inRange(progress, N_PROCESS_RANGE)) {
            return N_PROCESS;
        } else {
            return COMPLETE;
        }
    }

    private boolean inRange(int progress, int[] range) {
        return progress >= range[0] && progress <= range[1];
    }

    /*
     * *********************************************************************************************************************************************************
     * **************************************************************privacy start******************************************************************************
     * *********************************************************************************************************************************************************
     */

    int warningCount = 0;

    /**
     * 开始扫描隐私风险
     */
    private void handlePrivacyStart() {
        warningCount = 0;
        mRootView.setScanTitle("隐私风险扫描中...");
    }

    /**
     * 处理扫描隐私逻辑
     * 这里需要添加8条扫描固定项，总时长40格。每5格添加一个数据
     *
     * @param progress 0~40
     */
    private void handlePrivacyProcess(int progress) {
        progress=progress-P_START_RANGE[1];
        int thisIndex =(progress / 4);
        if (thisIndex > p_index && thisIndex < pList.size()) {
            //添加扫描文案
            p_index = thisIndex;
            ScanTextItemModel model = pList.get(p_index);
            mRootView.addScanPrivacyItem(model);

            if (model.warning) {
                warningCount++;
                mRootView.setPrivacyCount(warningCount);
            }
        }
    }

    /**
     * 隐私扫描完成
     */
    private void handlePrivacyEnd() {
        mRootView.setScanPrivacyComplete();
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
        mRootView.showScanVirusIcons(iconList);
    }

    /**
     * 处理扫描病毒逻辑
     *
     * @param progress 40~60
     */
    private void handleVirusProcess(int progress) {

    }

    /**
     * 病毒扫描完成
     */
    private void handleVirusEnd() {
        mRootView.setScanVirusComplete();
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
        mRootView.startScanNetwork();
    }

    /**
     * 处理扫描网络逻辑
     *
     * @param progress 60~100
     */
    private void handleNetworkProcess(int progress) {
        progress = progress - N_START_RANGE[1];
        int thisIndex = progress / 4;
        if (thisIndex > n_index && thisIndex < nList.size()) {
            //添加扫描文案
            n_index = thisIndex;
            ScanTextItemModel model = nList.get(n_index);
            mRootView.addScanNetWorkItem(model);
        }
    }

    /**
     * 所有扫描完成
     */
    private void handleAllComplete() {
        mRootView.setScanTitle("扫描完成！");
        mRootView.scanAllComplete(pStore.getMarkedList(),nStore.getMarkedList());
    }

}
