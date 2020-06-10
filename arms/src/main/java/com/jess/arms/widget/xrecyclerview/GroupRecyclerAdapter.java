package com.jess.arms.widget.xrecyclerview;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public abstract class GroupRecyclerAdapter extends CommonRecyclerAdapter<MultiItemInfo> {

    List<MultiItemInfo> mOriginDatas;

    public GroupRecyclerAdapter(Context context, MultiItemTypeSupport<MultiItemInfo> multiTypeSupport) {
        super(context, multiTypeSupport);
    }

    public void setData(List<? extends MultiItemInfo> datas) {
        if (datas != null) {
            mOriginDatas = (List<MultiItemInfo>) datas;
            mDatas.clear();
            mDatas.addAll(datas);
            initData();
            notifyDataSetChanged();
        }
    }

    public void initData() {
        for (int i = 0; i < mDatas.size(); i++) {
            MultiItemInfo itemInfo = mDatas.get(i);
            /*更新选中状态*/
            updateSelectedData(itemInfo);

            /*更新展开状态*/
            if (itemInfo == null || !itemInfo.hasChild()) {
                continue;
            }
            List<MultiItemInfo> childList = itemInfo.getChildList();
            if (itemInfo.isExpanded) {
                mDatas.addAll(i + 1, childList);
            }

        }
    }

    public void selectAll(boolean selected) {
        for (int i = 0; i < mOriginDatas.size(); i++) {
            MultiItemInfo itemInfo = mOriginDatas.get(i);
            itemInfo.selected = selected ? 1 : 0;
            if (itemInfo.hasChild()) {
                selectAll(itemInfo.getChildList(), selected);
            }
            /*更新选中状态*/
            updateSelectedData(itemInfo);
        }
        notifyDataSetChanged();
    }

    private void selectAll(List<MultiItemInfo> targetList, boolean selected) {
        for (int i = 0; i < targetList.size(); i++) {
            MultiItemInfo itemInfo = targetList.get(i);
            itemInfo.selected = selected ? 1 : 0;
            if (itemInfo.hasChild()) {
                selectAll(itemInfo.getChildList(), selected);
            }
        }
    }

    private void removeData(List<MultiItemInfo> targetList, MultiItemInfo targetInfo) {
        targetList.remove(targetInfo);
        for (int i = 0; i < targetList.size(); i++) {
            MultiItemInfo itemInfo = targetList.get(i);
            if (itemInfo.hasChild()) {
                removeData(itemInfo.getChildList(), targetInfo);
            }
        }
    }

    public void removeData(MultiItemInfo targetInfo) {
        removeData(mOriginDatas, targetInfo);
        setData(mOriginDatas);
    }

    public void removeSelectedData() {
        removeSelectedData(mOriginDatas);
        setData(mOriginDatas);
    }

    private void removeSelectedData(List<MultiItemInfo> targetList) {
        for (int i = 0; i < targetList.size(); i++) {
            MultiItemInfo itemInfo = targetList.get(i);
            if (itemInfo.selected == 1) {
                targetList.remove(i);
                i--;
            } else {
                if (itemInfo.hasChild()) {
                    removeSelectedData(itemInfo.getChildList());
                }
            }
        }
    }

    private List<MultiItemInfo> getSelectedData(List<MultiItemInfo> targetList) {
        List<MultiItemInfo> selectedList = new ArrayList<>();
        for (int i = 0; i < targetList.size(); i++) {
            MultiItemInfo itemInfo = targetList.get(i);
            if (itemInfo.hasChild()) {
                selectedList.addAll(getSelectedData(itemInfo.getChildList()));
            } else {
                if (itemInfo.selected == 1) {
                    selectedList.add(itemInfo);
                }
            }
        }
        return selectedList;
    }

    public List<MultiItemInfo> getSelectedData() {
        return getSelectedData(mOriginDatas);
    }

    public long getSelectedSize() {
        List<MultiItemInfo> selectedList = getSelectedData(mOriginDatas);
        long allSize = 0l;
        for (int i = 0; i < selectedList.size(); i++) {
            allSize += selectedList.get(i).totalSize;
        }
        return allSize;
    }

    //取消或者选中全部子项
    private void updateSelectedData(MultiItemInfo itemData) {
        if (itemData.hasChild()) {
            List<MultiItemInfo> childList = itemData.getChildList();
            int selected = -1;
            itemData.selectedSize = 0;
            itemData.totalSize = 0;
            for (int i = 0; i < childList.size(); i++) {
                MultiItemInfo childItem = childList.get(i);
                updateSelectedData(childItem);

                if (childItem.selected == 2) {
                    selected = childItem.selected;
                }

                if (selected != 2) {
                    if (selected == 1 && childItem.selected == 0) {
                        selected = 2;
                    } else if (selected == 0 && childItem.selected == 1) {
                        selected = 2;
                    } else {
                        selected = childItem.selected;
                    }
                }
                itemData.selectedSize += childItem.selectedSize;
                itemData.totalSize += childItem.totalSize;
            }
            itemData.selected = selected;
        } else {
            if (itemData.selected == 1) {
                itemData.selectedSize = itemData.totalSize;
            } else {
                itemData.selectedSize = 0;
            }
        }
    }

    /*收缩该分组子节点*/
    public void expandGroup(MultiItemInfo groupData, int position) {
        List<MultiItemInfo> childList = groupData.getChildList();
        if (groupData.isExpanded) {
            groupData.isExpanded = false;
            mDatas.removeAll(childList);
        } else {
            groupData.isExpanded = true;
            mDatas.addAll(position + 1, childList);
        }
        notifyDataSetChanged();
    }

    //取消或者选中全部子项
    public void setChildSelected(MultiItemInfo itemData) {
        updateChildSelected(itemData, itemData.selected == 1 ? 0 : 1);
        //重新计算选中的数据
        updateSelectedData(itemData.rootGroupInfo);
        notifyDataSetChanged();
    }

    private void updateChildSelected(MultiItemInfo itemData, int selected) {
        itemData.selected = selected;
        if (itemData.hasChild()) {
            List<MultiItemInfo> childList = itemData.getChildList();
            for (int i = 0; i < childList.size(); i++) {
                updateChildSelected(childList.get(i), selected);
            }
        }
    }
}
