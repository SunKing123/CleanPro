package com.xiaoniu.cleanking.ui.main.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xiaoniu.cleanking.ui.main.adapter.CleanExpandAdapter;

public class FirstLevelEntity extends AbstractExpandableItem<SecondLevelEntity> implements MultiItemEntity {


    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getItemType() {
        return CleanExpandAdapter.TYPE_LEVEL_1;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}
