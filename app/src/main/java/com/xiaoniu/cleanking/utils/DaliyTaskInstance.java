package com.xiaoniu.cleanking.utils;

import com.xiaoniu.cleanking.ui.main.bean.DaliyTaskListEntity;
import com.xiaoniu.cleanking.ui.main.bean.weatherdao.GreenDaoManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的页面
 * 日常任务待处理任务;
 */
public class DaliyTaskInstance {

    private List<DaliyTaskListEntity> taskList = new ArrayList<>();

    private static DaliyTaskInstance mInstance;

    //获取单例
    public static DaliyTaskInstance getInstance() {
        if (mInstance == null) {
            synchronized (DaliyTaskInstance.class) {
                if (mInstance == null) {
                    mInstance = new DaliyTaskInstance();
                }
            }
        }
        return mInstance;
    }


    public List<DaliyTaskListEntity> getTaskList() {
        return taskList;
    }

    /**
     * 添加任务
     * @param mList
     */
    public void addTask(DaliyTaskListEntity mList) {
        taskList.clear();
        taskList.add(mList);
    }

    /**
     * 清除任务
     */
    public void cleanTask(){
        this.taskList.clear();
    }

    /**
     * 是否存在任务
     * @return
     */
    public boolean isExistTask(){
        return !CollectionUtils.isEmpty(taskList);
    }
}
