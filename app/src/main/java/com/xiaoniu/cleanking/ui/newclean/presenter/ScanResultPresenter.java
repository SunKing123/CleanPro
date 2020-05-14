package com.xiaoniu.cleanking.ui.newclean.presenter;

import com.xiaoniu.cleanking.mvp.BasePresenter;
import com.xiaoniu.cleanking.ui.main.bean.CountEntity;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.newclean.bean.JunkResultWrapper;
import com.xiaoniu.cleanking.ui.newclean.bean.ScanningResultType;
import com.xiaoniu.cleanking.ui.newclean.contact.ScanResultContact;
import com.xiaoniu.cleanking.ui.newclean.model.ScanResultModel;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.CollectionUtils;

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

        //展示垃圾总量
        calJunkTotalSize();
    }

    /**
     * 计算扫描到的垃圾总量
     */
    private void calJunkTotalSize() {
        int totalSize = 0;
        for (Map.Entry<ScanningResultType, JunkGroup> junkTitle : junkTitleMap.entrySet()) {
            totalSize += junkTitle.getValue().mSize;
        }
        CountEntity mCountEntity = CleanUtil.formatShortFileSize(totalSize);
        //展示扫描到的垃圾总量
        if (getView() != null) {
            getView().setJunkTotalResultSize(mCountEntity.getTotalSize(), mCountEntity.getUnit());
        }
    }

    @Override
    public void updateExpendState(JunkResultWrapper wrapper) {
        JunkGroup junkGroup = wrapper.junkGroup;
        if (junkGroup != null) {
            JunkGroup junkGroupResult = junkTitleMap.get(wrapper.scanningResultType);
            if (junkGroupResult != null) {
                //变更状态为展示
                junkGroupResult.isExpand = !junkGroupResult.isExpand;
                junkTitleMap.put(wrapper.scanningResultType, junkGroupResult);
                refreshResultModel();
            }
        }
    }

    @Override
    public void updateJunkTypeCheckSate(JunkResultWrapper wrapper) {
        if (junkTitleMap == null || junkTitleMap.size() == 0) {
            return;
        }
        JunkGroup junkGroup = junkTitleMap.get(wrapper.scanningResultType);
        if (junkGroup != null) {
            junkGroup.isChecked = !junkGroup.isChecked;
            ArrayList<FirstJunkInfo> firstJunkList = junkContentMap.get(wrapper.scanningResultType);
            if (!CollectionUtils.isEmpty(firstJunkList)) {
                for (FirstJunkInfo firstJunkInfo : firstJunkList) {
                    firstJunkInfo.setAllchecked(!firstJunkInfo.isAllchecked());
                }
                junkContentMap.put(wrapper.scanningResultType, firstJunkList);
                refreshResultModel();
            }
        }
    }

    @Override
    public void updateJunkContentCheckState(JunkResultWrapper wrapper) {
        if (junkTitleMap == null || junkTitleMap.size() == 0) {
            return;
        }
        ArrayList<FirstJunkInfo> firstJunkList = junkContentMap.get(wrapper.scanningResultType);
        if (!CollectionUtils.isEmpty(firstJunkList)) {
            int checkedCount = 0;
            for (FirstJunkInfo firstJunkInfo : firstJunkList) {
                if (firstJunkInfo.equals(wrapper.firstJunkInfo)) {
                    firstJunkInfo.setAllchecked(!firstJunkInfo.isAllchecked());
                }
                if (firstJunkInfo.isAllchecked()) {
                    checkedCount++;
                }
            }
            junkContentMap.put(wrapper.scanningResultType, firstJunkList);
            JunkGroup junkGroup = junkTitleMap.get(wrapper.scanningResultType);
            if (junkGroup != null) {
                junkGroup.isCheckPart = checkedCount != 0 && checkedCount != firstJunkList.size();
                if (checkedCount == 0) {
                    junkGroup.isChecked = false;
                } else {
                    junkGroup.isChecked = checkedCount == firstJunkList.size();
                }
                junkTitleMap.put(wrapper.scanningResultType, junkGroup);
            }
            refreshResultModel();
        }
    }

    /**
     * 刷新界面展示
     */
    private void refreshResultModel() {
        if (getView() != null) {
            getView().setSubmitResult(buildJunkDataModel());
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
                junkResultWrappers.add(new JunkResultWrapper(JunkResultWrapper.ITEM_TYPE_TITLE, resultTypeJunkGroupEntry.getKey(), resultTypeJunkGroupEntry.getValue()));

                if (resultTypeJunkGroupEntry.getValue().isExpand) {
                    //遍历添加内容数据
                    for (FirstJunkInfo firstJunkInfo : firstJunkInfoList) {
                        junkResultWrappers.add(new JunkResultWrapper(JunkResultWrapper.ITEM_TYPE_CONTENT, resultTypeJunkGroupEntry.getKey(), firstJunkInfo));
                    }
                }
            }
        }

        //设置选中状态垃圾总量
        if (getView() != null) {
            long checkedJunkResult = setCheckedJunkResult();
            CountEntity countEntity = CleanUtil.formatShortFileSize(checkedJunkResult);
            getView().setCheckedJunkResult(countEntity.getResultSize());
        }

        return junkResultWrappers;
    }

    /**
     * 获取选中的垃圾清理量
     */
    private long setCheckedJunkResult() {
        long checkedTotalSize = 0;
        for (Map.Entry<ScanningResultType, ArrayList<FirstJunkInfo>> contentMap : junkContentMap.entrySet()) {
            if (!CollectionUtils.isEmpty(contentMap.getValue())) {
                for (FirstJunkInfo firstJunkInfo : contentMap.getValue()) {
                    if (firstJunkInfo.isAllchecked()) {
                        checkedTotalSize += firstJunkInfo.getTotalSize();
                    }
                }
            }
        }
        return checkedTotalSize;
    }
}
