package com.xiaoniu.cleanking.ui.tool.notify.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.tool.notify.bean.NotificationInfo;
import com.xiaoniu.common.utils.DateUtils;
import com.xiaoniu.common.widget.xrecyclerview.CommonRecyclerAdapter;
import com.xiaoniu.common.widget.xrecyclerview.CommonViewHolder;

import java.util.List;

public class NotifyCleanAdapter extends CommonRecyclerAdapter<NotificationInfo> {


    public NotifyCleanAdapter(Context context, List<NotificationInfo> datas) {
        super(context, datas, R.layout.item_clean_notification);
    }

    @Override
    public void convert(RecyclerView.ViewHolder holder, NotificationInfo itemData, int position) {
        if (holder instanceof CommonViewHolder) {
            CommonViewHolder commonHolder = (CommonViewHolder) holder;
            ImageView ivIcon = (ImageView) commonHolder.getView(R.id.ivIcon);
            TextView tvNotificationTitle = (TextView) commonHolder.getView(R.id.tvNotificationTitle);
            TextView tvNotificationDesc = (TextView) commonHolder.getView(R.id.tvNotificationDesc);
            TextView tvNotificationTime = (TextView) commonHolder.getView(R.id.tvNotificationTime);

            if (itemData == null) {
                return;
            }
            tvNotificationTitle.setText(itemData.title);
            if (!TextUtils.isEmpty(itemData.content)) {
                tvNotificationDesc.setVisibility(View.VISIBLE);
                tvNotificationDesc.setText(itemData.content);
            } else {
                tvNotificationDesc.setVisibility(View.GONE);
            }

            if (itemData.icon != null) {
                ivIcon.setImageBitmap(itemData.icon);
            } else {
                ivIcon.setImageResource(R.mipmap.icon_clean_image);
            }

            if (itemData.time > 0) {
                String date = DateUtils.fromTheCurrentTime(itemData.time, itemData.time);
                tvNotificationTime.setText(date);
            } else {
                tvNotificationTime.setText("");
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder attachToViewHolder(int viewType, View itemView) {
        return new CommonViewHolder(itemView);
    }
}
