package com.xiaoniu.cleanking.ui.newclean.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.DaliyTaskListEntity;
import com.xiaoniu.cleanking.utils.GlideUtils;

import org.jetbrains.annotations.NotNull;


/**
 * 日常任务适配器
 */
public class MineDaliyTaskAdapter extends BaseQuickAdapter<DaliyTaskListEntity, BaseViewHolder> {

    private Activity mActivity;


    public MineDaliyTaskAdapter(Activity mActivity) {
        super(R.layout.item_daliy_task);
        this.mActivity = mActivity;
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, DaliyTaskListEntity itemData) {
        ImageView actionIcon = baseViewHolder.getView(R.id.iv_action_icon);
        ImageView luckIcon = baseViewHolder.getView(R.id.iv_luck_award);

        if (!TextUtils.isEmpty(itemData.getTaskIcon())) {//功能icon
            GlideUtils.loadImage(mActivity, itemData.getTaskIcon(), actionIcon);
        }
        if (!TextUtils.isEmpty(itemData.getGoldIcon())) {
            GlideUtils.loadImage(mActivity, itemData.getGoldIcon(), luckIcon);
        }
        if (!TextUtils.isEmpty(itemData.getMainTitle())) {
            baseViewHolder.setText(R.id.tv_action_name,itemData.getMainTitle());
        }
        if (!TextUtils.isEmpty(itemData.getSubtitleTitle())) {
            baseViewHolder.setText(R.id.tv_action_content,itemData.getSubtitleTitle());
        }
        baseViewHolder.setBackgroundRes(R.id.tv_action_button, itemData.getIsCollect() == 0 ? R.drawable.icon_atonce_award : R.drawable.icon_already_award);
        if (itemData.getGoldNum() > 0) {
            baseViewHolder.setText(R.id.tv_luck_num,"+" + String.valueOf(itemData.getGoldNum()));
        }

    }

}
