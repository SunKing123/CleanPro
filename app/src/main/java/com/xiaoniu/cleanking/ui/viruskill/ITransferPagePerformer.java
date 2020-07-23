package com.xiaoniu.cleanking.ui.viruskill;

import com.xiaoniu.cleanking.ui.viruskill.model.ScanTextItemModel;

import java.util.ArrayList;

/**
 * Created by xinxiaolong on 2020/7/23.
 * email：xinxiaolong123@foxmail.com
 */
public interface ITransferPagePerformer {
    void onTransferResultPage(ArrayList<ScanTextItemModel> pList, ArrayList<ScanTextItemModel> nList);

    void onTransferCleanPage(ArrayList<ScanTextItemModel> pList, ArrayList<ScanTextItemModel> nList);

    void cleanComplete();
}
