package com.xiaoniu.cleanking.ui.newclean.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.DaliyTaskListEntity;
import com.xiaoniu.cleanking.utils.GlideUtils;

import java.util.List;

/**
 * 日常任务适配器
 */
@Deprecated
public class MineDaliyTaskAdapter01 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<DaliyTaskListEntity> mData;
    private onCheckListener mOnCheckListener;
    private Activity mActivity;

    public MineDaliyTaskAdapter01(Activity activity) {
        mActivity = activity;
    }

    public void setData(List<DaliyTaskListEntity> list) {
        mData = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_daliy_task, parent, false);
        return new ItemTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemTaskViewHolder) {
            ItemTaskViewHolder itemHolder = (ItemTaskViewHolder) holder;
            DaliyTaskListEntity itemData = mData.get(position);
            if (!TextUtils.isEmpty(itemData.getTaskIcon())) {//功能icon
                GlideUtils.loadImage(mActivity, itemData.getTaskIcon(), itemHolder.mIvActionIcon);
            }
            if (!TextUtils.isEmpty(itemData.getGoldIcon())) {
                GlideUtils.loadImage(mActivity, itemData.getGoldIcon(), itemHolder.mIvLuckAward);
            }

            if (!TextUtils.isEmpty(itemData.getMainTitle())) {
                itemHolder.mTvActionName.setText(itemData.getMainTitle());
            }

            if (!TextUtils.isEmpty(itemData.getSubtitleTitle())) {
                itemHolder.mTvActionContent.setText(itemData.getSubtitleTitle());
            }

            if (itemData.getGoldNum() > 0) {
                itemHolder.mTvLuckNum.setText("+" + String.valueOf(itemData.getGoldNum()));
            }

            itemHolder.itemView.setOnClickListener(v -> {
                if (mOnCheckListener != null)
                    mOnCheckListener.onCheck(mData, position);
            });

        }
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    public class ItemTaskViewHolder extends RecyclerView.ViewHolder {
        private ImageView mIvActionIcon;
        private ImageView mIvLuckAward;
        private TextView mTvLuckNum;
        private TextView mTvActionButton;
        private LinearLayout mLinearRight;
        private TextView mTvActionName;
        private TextView mTvActionContent;
        private LinearLayout mTvContainer;
        private RelativeLayout mVItem;

        public ItemTaskViewHolder(View itemView) {
            super(itemView);
            mIvActionIcon = itemView.findViewById(R.id.iv_action_icon);
            mIvLuckAward = itemView.findViewById(R.id.iv_luck_award);
            mTvLuckNum = itemView.findViewById(R.id.tv_luck_num);
            mTvActionButton = itemView.findViewById(R.id.tv_action_button);
            mLinearRight = itemView.findViewById(R.id.linear_right);
            mTvActionName = itemView.findViewById(R.id.tv_action_name);
            mTvActionContent = itemView.findViewById(R.id.tv_action_content);
            mTvContainer = itemView.findViewById(R.id.tv_container);
            mVItem = itemView.findViewById(R.id.v_item);
        }


    }

    public interface onCheckListener {
        void onCheck(List<DaliyTaskListEntity> listFile, int pos);
    }

    public void setmOnCheckListener(onCheckListener mOnCheckListener) {
        this.mOnCheckListener = mOnCheckListener;
    }
}
