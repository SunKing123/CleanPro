package com.xiaoniu.cleanking.ui.tool.notify.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.tool.notify.bean.NotificationSettingInfo;
import com.xiaoniu.cleanking.utils.DisplayImageUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.common.widget.xrecyclerview.CommonRecyclerAdapter;
import com.xiaoniu.common.widget.xrecyclerview.CommonViewHolder;

public class NotifySettingAdapter extends CommonRecyclerAdapter<NotificationSettingInfo> {


    public NotifySettingAdapter(Context context) {
        super(context, null, R.layout.item_clean_notification_setting);
    }

    @Override
    public void convert(RecyclerView.ViewHolder holder, final NotificationSettingInfo itemData, int position) {
        if (holder instanceof CommonViewHolder) {
            CommonViewHolder commonHolder = (CommonViewHolder) holder;
            ImageView ivIcon = (ImageView) commonHolder.getView(R.id.ivIcon);
            TextView tvName = (TextView) commonHolder.getView(R.id.tvName);
            final ImageView ivSelect = (ImageView) commonHolder.getView(R.id.ivSelect);

            tvName.setText(itemData.appName);
            DisplayImageUtils.getInstance().displayImage(itemData.pkg, ivIcon);

            ivSelect.setSelected(itemData.selected);
            ivSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemData.selected = !itemData.selected;
                    if (itemData.selected) {
                        ToastUtils.showShort(mContext.getString(R.string.notify_no_clean_tips, itemData.appName));
                        SPUtil.addDisableCleanNotificationPackages(itemData.pkg);
                    } else {
                        ToastUtils.showShort(mContext.getString(R.string.notify_yes_clean_tips, itemData.appName));
                        SPUtil.removeDisableCleanNotificationPackage(itemData.pkg);
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder attachToViewHolder(int viewType, View itemView) {
        return new CommonViewHolder(itemView);
    }
}
