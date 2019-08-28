package com.xiaoniu.cleanking.ui.main.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.PowerChildInfo;
import com.xiaoniu.cleanking.ui.main.bean.PowerGroupInfo;
import com.xiaoniu.cleanking.ui.main.presenter.ImageListPresenter;
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
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandGroup(groupInfo, position);
                }
            });
        } else if (itemData instanceof PowerChildInfo) {
            final PowerChildInfo itemInfo = (PowerChildInfo) itemData;
            final TextView tvName = viewHolder.getView(R.id.tvName);
            final ImageView ivIcon = viewHolder.getView(R.id.ivIcon);
            final ImageView ivSelect = viewHolder.getView(R.id.ivSelect);

            tvName.setText(itemInfo.appName);
            DisplayImageUtils.getInstance().displayImage(itemInfo.packageName, ivIcon);

            ivSelect.setSelected(itemInfo.selected == 1);
            ivSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemInfo.selected == 1) {//取消不弹窗
                        setChildSelected(itemInfo);
                        if (mOnCheckListener != null)
                            mOnCheckListener.onCheck(itemData);
                        return;
                    }
                    alertBanLiveDialog(mContext, itemInfo.appName, new ImageListPresenter.ClickListener() {
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

                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder attachToViewHolder(int viewType, View itemView) {
        return new CommonViewHolder(itemView);
    }


    public AlertDialog alertBanLiveDialog(Context context, String appName, final ImageListPresenter.ClickListener okListener) {
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        if (((Activity) context).isFinishing()) {
            return dlg;
        }
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.alite_redp_send_dialog);
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
        //这里设置居中
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView btnOk = (TextView) window.findViewById(R.id.btnOk);

        TextView btnCancle = (TextView) window.findViewById(R.id.btnCancle);
        TextView tipTxt = (TextView) window.findViewById(R.id.tipTxt);
        TextView content = (TextView) window.findViewById(R.id.content);
        tipTxt.setText("休眠 " + appName + " 减少耗电");
        content.setText("该应用可能正在后天工作");
        btnOk.setText("是");
        btnCancle.setText("否");
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                okListener.clickOKBtn();
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                okListener.cancelBtn();
            }
        });
        return dlg;
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
        public void onCheck(Object info);
    }

    public void setmOnCheckListener(onCheckListener mOnCheckListener) {
        this.mOnCheckListener = mOnCheckListener;
    }
}
