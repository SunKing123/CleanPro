package com.xiaoniu.cleanking.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.utils.CleanUtil;

import java.util.HashMap;

public class DockingExpandableListViewAdapter extends BaseExpandableListAdapter {

    private final ExpandableListView mListView;
    private HashMap<Integer, JunkGroup> mJunkGroups = new HashMap<>();

    private OnItemSelectListener mOnItemSelectListener;

    private Context mContext;

    public DockingExpandableListViewAdapter(Context context, ExpandableListView listView) {
        mListView = listView;
        mContext = context;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        mOnItemSelectListener = onItemSelectListener;
    }

    public void setData(HashMap<Integer, JunkGroup> data) {
        mJunkGroups = data;
        if (mJunkGroups == null) {
            mJunkGroups = new HashMap<>();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mJunkGroups.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if (mJunkGroups.get(i).mChildren != null) {
            return mJunkGroups.get(i).mChildren.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getGroup(int i) {
        return mJunkGroups.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mJunkGroups.get(groupPosition).mChildren.get(childPosition);
    }

    public Object getChild(int groupPosition) {
        return mJunkGroups.get(groupPosition);
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.group_list, null);
            holder = new GroupViewHolder();
            holder.mPackageNameTv = convertView.findViewById(R.id.package_name);
            holder.mPackageSizeTv = convertView.findViewById(R.id.package_size);
            holder.mCheckButton = convertView.findViewById(R.id.icon_check);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        JunkGroup group = mJunkGroups.get(groupPosition);
        holder.mPackageNameTv.setText(group.mName);
        holder.mPackageSizeTv.setText(CleanUtil.formatShortFileSize(mContext, group.mSize));
        holder.mCheckButton.setSelected(group.isChecked);
        holder.mCheckButton.setOnClickListener(v -> {
            group.isChecked = !group.isChecked;
            resetSelectButton(group, group.isChecked);
        });

        return convertView;
    }

    public void resetSelectButton(JunkGroup group, boolean isChecked) {
        for (FirstJunkInfo firstJunkInfo : group.mChildren) {
            firstJunkInfo.setAllchecked(isChecked);
        }
        if (mOnItemSelectListener != null) {
            mOnItemSelectListener.onCount();
        }
        notifyDataSetChanged();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        FirstJunkInfo info = mJunkGroups.get(groupPosition).mChildren.get(childPosition);


        ChildViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.level1_item_list, null);
            holder = new ChildViewHolder();
            holder.mJunkTypeTv = convertView.findViewById(R.id.text_app_name);
            holder.mLogo = convertView.findViewById(R.id.app_logo);
            holder.mJunkSizeTv = convertView.findViewById(R.id.junk_size);
            holder.mCheckButton = convertView.findViewById(R.id.icon_check);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        holder.mJunkTypeTv.setText(info.getAppName());
        holder.mLogo.setImageBitmap(info.getGarbageIcon());
        holder.mJunkSizeTv.setText(CleanUtil.formatShortFileSize(mContext, info.getTotalSize()));
        holder.mCheckButton.setSelected(info.isAllchecked());
        holder.mCheckButton.setOnClickListener(v -> {
            boolean checked = !info.isAllchecked();
            info.setAllchecked(checked);
            resetItemSelectButton(mJunkGroups.get(groupPosition));
        });

        return convertView;
    }

    public void resetItemSelectButton(JunkGroup group) {
        group.isChecked = true;
        for (FirstJunkInfo firstJunkInfo : group.mChildren) {
            if (!firstJunkInfo.isAllchecked()) {
                group.isChecked = false;
                break;
            }
        }
        if (mOnItemSelectListener != null) {
            mOnItemSelectListener.onCount();
        }
        notifyDataSetChanged();
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public class GroupViewHolder {
        public TextView mPackageNameTv;
        public TextView mPackageSizeTv;
        public ImageView mCheckButton;
    }

    public static class ChildViewHolder {
        public TextView mJunkTypeTv;
        public TextView mJunkSizeTv;
        public ImageView mLogo;
        public ImageView mCheckButton;
    }

    public interface OnItemSelectListener{
        void onCount();
    }
}
