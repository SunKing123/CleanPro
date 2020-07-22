package com.xiaoniu.cleanking.ui.viruskill.newversion.model;

import android.text.TextUtils;
import android.util.SparseArray;

import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by xinxiaolong on 2020/7/21.
 * email：xinxiaolong123@foxmail.com
 */
public class PrivacyDataStore {

    private final String[] items = new String[]{"手机IP泄露", "通讯录泄露", "信息被偷窥", "支付环境安全", "摄像头防窥", "麦克风防窃听", "相册安全保密", "聊天信息加密"};

    private List<String> listIds = new ArrayList<>(items.length);
    private List<ScanTextItemModel> itemList = new ArrayList<>(items.length);

    private static PrivacyDataStore store;
    private String[] mCacheMarksWarnIds;

    public static PrivacyDataStore getInstance() {
        if (store == null) {
            store = new PrivacyDataStore();
        }
        return store;
    }

    public String[] getCacheNeedMarksIds() {
        return mCacheMarksWarnIds;
    }

    private PrivacyDataStore() {
        initData();
    }

    private void initData() {
        int index = 0;
        for (String item : items) {
            ScanTextItemModel itemData = new ScanTextItemModel();
            itemData.id = index;
            itemData.name = item;

            itemList.add(itemData);
            listIds.add(String.valueOf(index));
            index++;
        }

        if (TextUtils.isEmpty(PreferenceUtil.getPrivacyItemRandomIds())) {
            randomIds();
        }
    }

    /**
     * 随机打上警告标签
     */
    public List<ScanTextItemModel> randomMarkWarning() {
        mCacheMarksWarnIds = mCacheMarksWarnIds ==null?getRandomNeedMarksIds(): mCacheMarksWarnIds;
        return markWarning(mCacheMarksWarnIds);
    }

    /**
     * 随机标记风险项
     * 对已经标记过的id过滤。
     */
    private String[] getRandomNeedMarksIds() {
        //取出可用的随机id列表
        String[] ids = PreferenceUtil.getPrivacyItemRandomIds().split(",");
        //这次提示几个风险项
        int riskCount = NumberUtils.mathRandomInt(1, 3);
        //需要标记风险的id集合
        String[] needMarksIds = new String[riskCount];

        if (riskCount >= ids.length) {
            //不够取了，剩余的全部标记
            needMarksIds = ids;
        } else {
            //从随机id里取出需要标记的风险项，数据复制到needMarksIds中
            System.arraycopy(ids, 0, needMarksIds, 0, riskCount);
        }
        return needMarksIds;
    }


    /**
     * 在清理完成够调用此方法，对标记过风险的数据项进行排除
     */
    public void removeMarkedIdsInRandomTable() {

        String[] ids = PreferenceUtil.getPrivacyItemRandomIds().split(",");

        if (mCacheMarksWarnIds == null || mCacheMarksWarnIds.length <= 0) {
            return;
        }

        int count = mCacheMarksWarnIds.length;

        if (ids.length <= count) {
            //需要全部清除，重新生成随机表
            randomIds();
        } else {
            //将已标记的风险id排除
            int size = ids.length - count;
            String[] remainIds = new String[ids.length - count];
            System.arraycopy(ids, count, remainIds, 0, size);
            saveRandomIds(new ArrayList<>(Arrays.asList(remainIds)));
        }
        mCacheMarksWarnIds = null;
    }

    private void randomIds() {
        //将id随机。
        Collections.shuffle(listIds);
        saveRandomIds(listIds);
    }

    /**
     * 保存
     *
     * @param randomIds 已生成的随机id列表
     */
    private void saveRandomIds(List<String> randomIds) {

        StringBuilder builder = new StringBuilder();

        for (String id : randomIds) {
            builder.append(id).append(",");
        }
        String ids = builder.delete(builder.length() - 1, builder.length()).toString();
        PreferenceUtil.savePrivacyItemRandomIds(ids);
    }

    /**
     * 标记风险项
     *
     * @param needMarksIds 需要标记风险的id列表
     */
    private List<ScanTextItemModel> markWarning(String[] needMarksIds) {
        SparseArray<String> map = new SparseArray<>(needMarksIds.length);
        for (String id : needMarksIds) {
            map.put(Integer.parseInt(id), id);
        }

        for (ScanTextItemModel model : itemList) {
            String id = map.get(model.id);
            model.warning = !TextUtils.isEmpty(id);
        }
        return itemList;
    }
}
