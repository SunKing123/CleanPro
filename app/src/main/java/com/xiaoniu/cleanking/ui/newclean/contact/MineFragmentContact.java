package com.xiaoniu.cleanking.ui.newclean.contact;

import com.xiaoniu.cleanking.base.BaseView;
import com.xiaoniu.cleanking.ui.main.bean.MinePageInfoBean;

/**
 * Created by zhaoyingtao
 * Date: 2020/7/6
 * Describe:
 */
public interface MineFragmentContact {
    interface View extends BaseView {
        void getInfoDataSuccess(MinePageInfoBean infoBean);
    }

}