package com.xiaoniu.cleanking.ui.newclean.contact;

import com.xiaoniu.cleanking.base.BaseView;
import com.xiaoniu.cleanking.ui.main.bean.BubbleCollected;
import com.xiaoniu.cleanking.ui.main.bean.BubbleConfig;
import com.xiaoniu.cleanking.ui.main.bean.DaliyTaskListData;
import com.xiaoniu.cleanking.ui.main.bean.MinePageInfoBean;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/6
 * Describe:
 */
public interface MineFragmentContact {
    interface View extends BaseView {
        void getInfoDataSuccess(MinePageInfoBean infoBean);

        void setBubbleView(BubbleConfig bubbleView);

        void bubbleCollected(BubbleCollected dataBean);

        void setTaskData(DaliyTaskListData data);
    }

}
