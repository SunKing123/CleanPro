package com.xiaoniu.cleanking.ui.newclean.presenter;

import com.xiaoniu.cleanking.mvp.BasePresenter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.newclean.bean.JunkResultWrapper;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.ui.newclean.contact.ScanResultContact;
import com.xiaoniu.cleanking.ui.newclean.model.ScanResultModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScanResultPresenter extends BasePresenter<ScanResultContact.View, ScanResultModel>
        implements ScanResultContact.IPresenter {

    /**
     * 垃圾清理类别
     */
    private LinkedHashMap<ScanningResultType, JunkGroup> junkTitleMap = new LinkedHashMap<>();

    /**
     * 垃圾清理内容
     */
    private LinkedHashMap<ScanningResultType, ArrayList<FirstJunkInfo>> junkContentMap = new LinkedHashMap<>();

    @Override
    public void buildJunkResultModel(LinkedHashMap<ScanningResultType, JunkGroup> scanningResultMap) {
        //拆分清理数据类别
        splitJunkGroup(scanningResultMap);

        //构建清理数据展示模型
        initBuildJunkDataModel();
    }

    @Override
    public void updateExpendState(JunkResultWrapper wrapper) {
        JunkGroup junkGroup = wrapper.junkGroup;
        if (junkGroup != null) {
            ScanningResultType scanningResultType = ScanningResultType.getScanningResultTypeByTypeId(junkGroup.junkType);
            if (scanningResultType != null) {
                JunkGroup junkGroupResult = junkTitleMap.get(scanningResultType);
                if (junkGroupResult != null) {
                    //变更状态为展示
                    junkGroupResult.isExpand = !junkGroupResult.isExpand;
                    junkTitleMap.put(scanningResultType, junkGroupResult);
                    if (getView() != null) {
                        getView().setSubmitResult(buildJunkDataModel());
                    }
                }
            }
        }
    }

    /**
     * 拆分出来清理类别 & 清理内容，方便数据展示
     */
    private void splitJunkGroup(LinkedHashMap<ScanningResultType, JunkGroup> scanningResultMap) {
        if (scanningResultMap != null && scanningResultMap.size() > 0) {
            junkTitleMap.clear();
            junkContentMap.clear();
            for (Map.Entry<ScanningResultType, JunkGroup> resultTypeJunkGroupEntry : scanningResultMap.entrySet()) {
                //拆分清理类型
                junkTitleMap.put(resultTypeJunkGroupEntry.getKey(), resultTypeJunkGroupEntry.getValue());
                //拆分清理内容
                junkContentMap.put(resultTypeJunkGroupEntry.getKey(), resultTypeJunkGroupEntry.getValue().mChildren);
            }
        }
    }

    /**
     * 构造清理数据模型
     */
    private void initBuildJunkDataModel() {
        if (getView() != null) {
            getView().setInitSubmitResult(buildJunkDataModel());
        }
    }

    private List<JunkResultWrapper> buildJunkDataModel() {
        if (junkTitleMap == null || junkTitleMap.size() == 0) {
            return null;
        }
        final List<JunkResultWrapper> junkResultWrappers = new ArrayList<>();
        for (Map.Entry<ScanningResultType, JunkGroup> resultTypeJunkGroupEntry : junkTitleMap.entrySet()) {
            ArrayList<FirstJunkInfo> firstJunkInfoList = junkContentMap.get(resultTypeJunkGroupEntry.getKey());
            if (firstJunkInfoList != null && firstJunkInfoList.size() > 0) {
                //添加类别数据
                junkResultWrappers.add(new JunkResultWrapper(JunkResultWrapper.ITEM_TYPE_TITLE, resultTypeJunkGroupEntry.getValue()));

                if (resultTypeJunkGroupEntry.getValue().isExpand) {
                    //遍历添加内容数据
                    for (FirstJunkInfo firstJunkInfo : firstJunkInfoList) {
                        junkResultWrappers.add(new JunkResultWrapper(JunkResultWrapper.ITEM_TYPE_CONTENT, firstJunkInfo));
                    }
                }
            }
        }
        return junkResultWrappers;
    }
}
