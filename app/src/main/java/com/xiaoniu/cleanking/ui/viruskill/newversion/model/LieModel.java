package com.xiaoniu.cleanking.ui.viruskill.newversion.model;

import com.xiaoniu.cleanking.bean.VirusKillBean;
import com.xiaoniu.cleanking.ui.viruskill.old.contract.VirusKillContract;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by xinxiaolong on 2020/7/22.
 * email：xinxiaolong123@foxmail.com
 */
public class LieModel implements VirusKillContract.Model{
    @Override
    public Observable<List<VirusKillBean>> getUsers(int lastIdQueried, boolean update) {
        return null;
    }

    @Override
    public void onDestroy() {

    }
}
