package com.xiaoniu.cleanking.ui.main.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.callback.OnItemSelectListener;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.JunkGroup;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.utils.CleanUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 一键清理详情的列表Adapter
 */
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
            holder.mIconArrow = convertView.findViewById(R.id.icon_arrow);
            holder.mViewDivider = convertView.findViewById(R.id.view_divider);
            holder.mLayoutCheck = convertView.findViewById(R.id.layout_check);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        JunkGroup group = mJunkGroups.get(groupPosition);
        holder.mPackageNameTv.setText(group.mName);
        holder.mPackageSizeTv.setText(CleanUtil.formatShortFileSize(mContext, group.mSize));
        holder.mCheckButton.setSelected(group.isChecked);
        holder.mIconArrow.setImageDrawable(!group.isExpand ? mContext.getResources().getDrawable(R.mipmap.arrow_up) : mContext.getResources().getDrawable(R.mipmap.arrow_down));
        holder.mViewDivider.setVisibility(group.isExpand ? View.GONE : View.VISIBLE);
        holder.mCheckButton.setOnClickListener(v -> {
            group.isChecked = !group.isChecked;
            resetSelectButton(group, group.isChecked);
        });

        return convertView;
    }

    /**
     * 第一级选择框点击时， 设置所有子按钮为选中/未选中状态
     *
     * @param group
     * @param isChecked
     */
    private void resetSelectButton(JunkGroup group, boolean isChecked) {
        for (FirstJunkInfo firstJunkInfo : group.mChildren) {
            if (!firstJunkInfo.isLock()) {
                //选中没有上锁的apk应用
                firstJunkInfo.setAllchecked(isChecked);
            }
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
            holder.mTextVersion = convertView.findViewById(R.id.text_version);
            holder.mRootView = convertView.findViewById(R.id.layout_root);
            holder.mLayoutCheck = convertView.findViewById(R.id.layout_check);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        //名称
        holder.mJunkTypeTv.setText(info.getAppName());
        //垃圾的LOGO
        holder.mLogo.setImageDrawable(info.getGarbageIcon());
        //垃圾大小
        holder.mJunkSizeTv.setText(CleanUtil.formatShortFileSize(mContext, info.getTotalSize()));
        //选择框
        holder.mLayoutCheck.setSelected(info.isAllchecked());
        //只有安装包文件显示版本信息
        if ("TYPE_APK".equals(info.getGarbageType())) {
            holder.mTextVersion.setVisibility(View.VISIBLE);
            holder.mTextVersion.setText(info.getDescp() + " v" + info.getVersionName());
            holder.mRootView.setOnClickListener(v -> {
                //点击监听
                showApkWhiteDialog(info);
            });
            if (!TextUtils.isEmpty(info.getGarbageCatalog())) {
                if (getWhiteList().contains(dealPath(info.getGarbageCatalog()))) {
                    //已在白名单 加锁
                    holder.mCheckButton.setBackground(mContext.getResources().getDrawable(R.mipmap.icon_lock));
                    info.setAllchecked(false);
                    info.setLock(true);
                    resetItemSelectButton(mJunkGroups.get(groupPosition));
                } else {
                    holder.mCheckButton.setBackground(mContext.getResources().getDrawable(R.drawable.icon_choose_selector));
                    info.setLock(false);
                }
            }
        } else {
            holder.mTextVersion.setVisibility(View.GONE);
            holder.mRootView.setOnClickListener(null);
            holder.mCheckButton.setBackground(mContext.getResources().getDrawable(R.drawable.icon_choose_selector));
        }


        holder.mLayoutCheck.setOnClickListener(v -> {
            boolean checked = !info.isAllchecked();
            info.setAllchecked(checked);
            resetItemSelectButton(mJunkGroups.get(groupPosition));
        });

        return convertView;
    }

    /**
     * 重置父类的全选按钮状态
     *
     * @param group
     */
    private void resetItemSelectButton(JunkGroup group) {
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

    /**
     * Apk安装包保护弹窗
     */
    public void showApkWhiteDialog(FirstJunkInfo junkInfo) {
        //提示对话框
        final Dialog dialog = new Dialog(mContext, R.style.custom_dialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_white_apk, null);
        TextView mTextTitle = view.findViewById(R.id.text_title);
        View mBlueBg = view.findViewById(R.id.bg_blue_view);
        TextView mTextTips = view.findViewById(R.id.text_tips);
        TextView mTextDivider = view.findViewById(R.id.text_divider);
        TextView mTextSize = view.findViewById(R.id.text_size);
        CheckBox mCheckView = view.findViewById(R.id.icon_check);
        TextView mTextTrace = view.findViewById(R.id.text_trace);
        TextView mTextClose = view.findViewById(R.id.text_close);

        //app名称
        mTextTitle.setText(junkInfo.getAppName());
        //大小
        mTextSize.setText("大小：" + CleanUtil.formatShortFileSize(mContext, junkInfo.getTotalSize()));
        //路径
        mTextTrace.setText("路径：" + junkInfo.getGarbageCatalog());

        mCheckView.setChecked(junkInfo.isLock());

        mTextClose.setOnClickListener(v -> dialog.dismiss());
        mCheckView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mBlueBg.setVisibility(View.VISIBLE);
            mTextTips.setVisibility(View.VISIBLE);
            mTextDivider.setVisibility(View.GONE);
            if (isChecked) {
                mTextTips.setText(mContext.getResources().getString(R.string.text_apk_white_add));
                savePath(dealPath(junkInfo.getGarbageCatalog()));
            } else {
                mTextTips.setText(mContext.getResources().getString(R.string.text_white_removed));
                removePath(dealPath(junkInfo.getGarbageCatalog()));
            }
        });
        dialog.setContentView(view);
        if (dialog.isShowing()) {
            return;
        }
        dialog.show();
    }

    /**
     * 移除apk白名单缓存路径
     *
     * @param garbageCatalog
     */
    private void removePath(String garbageCatalog) {
        Set<String> caches = new HashSet<>();
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Set<String> stringSet = sp.getStringSet(SpCacheConfig.WHITE_LIST_KEY_DIRECTORY, caches);
        if (stringSet != null) {
            stringSet.remove(garbageCatalog);
        }
        editor.putStringSet(SpCacheConfig.WHITE_LIST_KEY_DIRECTORY, stringSet);
        editor.commit();
        notifyDataSetChanged();
    }

    /**
     * 添加apk白名单缓存路径
     *
     * @param path
     */
    private void savePath(String path) {
        Set<String> caches = new HashSet<>();
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Set<String> stringSet = sp.getStringSet(SpCacheConfig.WHITE_LIST_KEY_DIRECTORY, caches);
        if (stringSet != null) {
            stringSet.add(path);
        }
        editor.putStringSet(SpCacheConfig.WHITE_LIST_KEY_DIRECTORY, stringSet);
        editor.commit();
        notifyDataSetChanged();
    }

    private Set<String> getWhiteList() {
        Set<String> caches = new HashSet<>();
        SharedPreferences sp = mContext.getSharedPreferences(SpCacheConfig.CACHES_NAME_WHITE_LIST_INSTALL_PACKE, Context.MODE_PRIVATE);
        return sp.getStringSet(SpCacheConfig.WHITE_LIST_KEY_DIRECTORY, caches);
    }

    /**
     * 路径处理
     *
     * @param path
     * @return
     */
    private String dealPath(String path) {
        if (!TextUtils.isEmpty(path)) {
            return path.substring(0, path.lastIndexOf("/"));
        } else {
            return "";
        }
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public class GroupViewHolder {
        public TextView mPackageNameTv;
        public TextView mPackageSizeTv;
        public ImageView mCheckButton;
        public ImageView mIconArrow;
        public View mViewDivider;
        public FrameLayout mLayoutCheck;
    }

    public static class ChildViewHolder {
        public TextView mJunkTypeTv;
        public TextView mJunkSizeTv;
        public ImageView mLogo;
        public ImageView mCheckButton;
        public TextView mTextVersion;
        public LinearLayout mRootView;
        public LinearLayout mLayoutCheck;
    }
}
