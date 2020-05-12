package com.xiaoniu.cleanking.ad.interfaces;

import com.xiaoniu.cleanking.ad.bean.AdRequestBean;
import com.xiaoniu.cleanking.ad.bean.AdRequestParamentersBean;

import java.util.Deque;

/**
 * @ProjectName: clean
 * @Package: com.xiaoniu.cleanking.ad.interfaces
 * @ClassName: AdAgainRequest
 * @Description:
 * @Author: youkun_zhou
 * @CreateDate: 2020/5/9 17:02
 */

public interface AdAgainRequestCallBack {

    void againRequestCallback(Deque<AdRequestBean> adRequest, AdRequestParamentersBean adRequestParamentersBean);

}
