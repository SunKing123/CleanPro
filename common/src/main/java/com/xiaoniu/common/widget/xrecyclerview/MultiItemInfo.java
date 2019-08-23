package com.xiaoniu.common.widget.xrecyclerview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MultiItemInfo<T extends MultiItemInfo> implements Serializable {
    public int selected;//是否选中:0未选中，1选中，2半选中
    public boolean isExpanded;//是否展开
    public long selectedSize;//该组孩子节点选中量
    public long totalSize;//该组孩子节点所有量（选中和未选中）
    private List<T> childList;//该组孩子节点，如果本身就是孩子，设置为空即可
    public MultiItemInfo rootGroupInfo;//顶级分组信息

    public MultiItemInfo() {
        rootGroupInfo = this;//默认是自己本身
    }

    public List<T> getChildList() {
        return childList;
    }

    /*如果加入其它子节点或孙子节点，各个节点的rootGroupInfo都是第一个节点*/
    public void addItemInfo(T itemInfo) {
        if (itemInfo == null) {
            return;
        }
        if (childList == null) {
            childList = new ArrayList<>();
        }
        itemInfo.rootGroupInfo = rootGroupInfo;
        childList.add(itemInfo);
    }

    public void addAllItem(List<T> allItems) {
        if (allItems == null) {
            return;
        }
        if (childList == null) {
            childList = new ArrayList<>();
        }
        for (int i = 0; i < allItems.size(); i++) {
            T itemInfo = allItems.get(i);
            itemInfo.rootGroupInfo = rootGroupInfo;
            childList.add(itemInfo);
        }
    }

    public boolean hasChild() {
        return childList != null && childList.size() > 0;
    }
}
