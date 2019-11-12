package com.xiaoniu.cleanking.ui.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.PowerChildInfo;
import com.xiaoniu.cleanking.ui.main.bean.PowerGroupInfo;
import com.xiaoniu.cleanking.ui.newclean.interfice.ClickListener;
import com.xiaoniu.cleanking.ui.newclean.util.AlertDialogUtil;
import com.xiaoniu.cleanking.utils.DisplayImageUtils;
import com.xiaoniu.common.widget.xrecyclerview.CommonViewHolder;
import com.xiaoniu.common.widget.xrecyclerview.GroupRecyclerAdapter;
import com.xiaoniu.common.widget.xrecyclerview.MultiItemInfo;

public class SuperPowerCleanAdapter extends GroupRecyclerAdapter {
    private onCheckListener mOnCheckListener;

    public SuperPowerCleanAdapter(Context context) {
        super(context, new PowerMultiItemTypeSupport());
    }

    @Override
    public void convert(RecyclerView.ViewHolder holder, MultiItemInfo itemData, int position) {
        final CommonViewHolder viewHolder = (CommonViewHolder) holder;
        if (itemData instanceof PowerGroupInfo) {
            final PowerGroupInfo groupInfo = (PowerGroupInfo) itemData;
            TextView tvTitle = viewHolder.getView(R.id.tvTitle);
            ImageView ivIcon = viewHolder.getView(R.id.ivIcon);
            ImageView ivExpand = viewHolder.getView(R.id.ivExpand);
            TextView tvDesc = viewHolder.getView(R.id.tvDesc);

            tvTitle.setText(groupInfo.title);
            ivExpand.setSelected(groupInfo.isExpanded);

            if (groupInfo.type == 0) {
                ivIcon.setImageResource(R.drawable.icon_power_kill_group);
                tvDesc.setText(groupInfo.getChildList().size() + "个待休眠应用");
            } else {
                ivIcon.setImageResource(R.drawable.icon_power_hold_group);
                tvDesc.setText(groupInfo.getChildList().size() + "个必要的应用");
            }
            viewHolder.itemView.setOnClickListener(v -> expandGroup(groupInfo, position));
        } else if (itemData instanceof PowerChildInfo) {
            final PowerChildInfo itemInfo = (PowerChildInfo) itemData;
            final TextView tvName = viewHolder.getView(R.id.tvName);
            final ImageView ivIcon = viewHolder.getView(R.id.ivIcon);
            final ImageView ivSelect = viewHolder.getView(R.id.ivSelect);

            tvName.setText(itemInfo.appName);
            DisplayImageUtils.getInstance().displayImage(itemInfo.packageName, ivIcon);

            ivSelect.setSelected(itemInfo.selected == 1);
            ivSelect.setOnClickListener(v -> {
                if (itemInfo.selected == 1) {//取消不弹窗
                    setChildSelected(itemInfo);
                    if (mOnCheckListener != null)
                        mOnCheckListener.onCheck(itemData);
                    return;
                }
                AlertDialogUtil.alertBanLiveDialog(mContext, "休眠 " + itemInfo.appName + " 减少耗电","该应用可能正在后天工作" ,"是","否", new ClickListener() {
                    @Override
                    public void clickOKBtn() {
                        setChildSelected(itemInfo);
                        if (mOnCheckListener != null)
                            mOnCheckListener.onCheck(itemData);
                    }

                    @Override
                    public void cancelBtn() {

                    }
                });

            });
        }
    }

    @Override
    public RecyclerView.ViewHolder attachToViewHolder(int viewType, View itemView) {
        return new CommonViewHolder(itemView);
    }

    public static class PowerMultiItemTypeSupport implements MultiItemTypeSupport<MultiItemInfo> {

        @Override
        public int getItemViewType(int position, MultiItemInfo itemData) {
            if (itemData instanceof PowerGroupInfo) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getLayoutId(int viewType) {
            if (viewType == 0) {
                return R.layout.item_power_clean_group;
            } else {
                return R.layout.item_power_clean_child;
            }
        }

        @Override
        public boolean isFullSpan(int position) {
            return false;
        }

        @Override
        public int getSpanSize(int position, int spanCount) {
            return 0;
        }
    }

    public interface onCheckListener {
        void onCheck(Object info);
    }

    public void setOnCheckListener(onCheckListener mOnCheckListener) {
        this.mOnCheckListener = mOnCheckListener;
    }
}
