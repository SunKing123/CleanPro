package com.xiaoniu.cleanking.ui.tool.wechat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxChildInfo;
import com.xiaoniu.cleanking.ui.tool.wechat.bean.CleanWxGroupInfo;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.common.widget.xrecyclerview.CommonRecyclerAdapter;
import com.xiaoniu.common.widget.xrecyclerview.CommonViewHolder;
import com.xiaoniu.common.widget.xrecyclerview.GroupRecyclerAdapter;
import com.xiaoniu.common.widget.xrecyclerview.MultiItemInfo;

public class WechatCleanFileAdapter extends GroupRecyclerAdapter {


    onCheckListener mOnCheckListener;

    public WechatCleanFileAdapter(Context context) {
        super(context, new WechatMultiItemTypeSupport());
    }

    @Override
    public void convert(RecyclerView.ViewHolder holder, MultiItemInfo itemData, int position) {
        final CommonViewHolder viewHolder = (CommonViewHolder) holder;
        if (itemData instanceof CleanWxGroupInfo) {
            final CleanWxGroupInfo tileInfo = (CleanWxGroupInfo) itemData;
            TextView tvTitle = viewHolder.getView(R.id.tvDay);
            ImageView ivArrow = viewHolder.getView(R.id.ivArrow);
            TextView tvSelectAll = viewHolder.getView(R.id.tv_select_all);
            TextView tvFileSize = viewHolder.getView(R.id.tv_file_size);
            tvTitle.setText(tileInfo.title);
            if (tileInfo.isExpanded) {
                ivArrow.setImageResource(R.mipmap.arrow_up);
            } else {
                ivArrow.setImageResource(R.mipmap.arrow_down);
            }

            if (tileInfo.selected == 1) {
                tvSelectAll.setBackgroundResource(R.drawable.icon_select);
            } else {
                tvSelectAll.setBackgroundResource(R.drawable.icon_unselect);
            }

            tvFileSize.setText(CleanAllFileScanUtil.byte2FitSizeOne(tileInfo.selectedSize));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandGroup(tileInfo, position);
                }
            });

            tvSelectAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setChildSelected(tileInfo);
                    if (mOnCheckListener != null)
                        mOnCheckListener.onCheck(tileInfo);
                }
            });

        } else if (itemData instanceof CleanWxChildInfo) {
            final CleanWxChildInfo itemInfo = (CleanWxChildInfo) itemData;
            final TextView tv_name = viewHolder.getView(R.id.tv_name);
            final ImageView iv_photo_filelist_pic = viewHolder.getView(R.id.iv_photo_filelist_pic);
            final TextView tv_time = viewHolder.getView(R.id.tv_time);
            final TextView tv_select = viewHolder.getView(R.id.tv_select);
            final TextView tv_size = viewHolder.getView(R.id.tv_size);

            iv_photo_filelist_pic.setImageResource(getImgRes(itemInfo.file.getName()));
            tv_name.setText(itemInfo.file.getName());
            tv_time.setText(itemInfo.stringDay);
            tv_size.setText(CleanAllFileScanUtil.byte2FitSizeOne(itemInfo.totalSize));
            tv_select.setBackgroundResource(itemInfo.selected == 1 ? R.drawable.icon_select : R.drawable.icon_unselect);
            tv_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setChildSelected(itemInfo);
                    if (mOnCheckListener != null)
                        mOnCheckListener.onCheck(itemData);
                }
            });

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AndroidUtil.openFileSafe(mContext, String.valueOf(itemInfo.file));
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder attachToViewHolder(int viewType, View itemView) {
        return new CommonViewHolder(itemView);
    }


    public static class WechatMultiItemTypeSupport implements CommonRecyclerAdapter.MultiItemTypeSupport<MultiItemInfo> {

        @Override
        public int getItemViewType(int position, MultiItemInfo itemData) {
            if (itemData instanceof CleanWxGroupInfo) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getLayoutId(int viewType) {
            if (viewType == 0) {
                return R.layout.item_wechat_file_title;
            } else {
                return R.layout.item_wechat_file_content;
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

    //zip rar txt xlsx pdf docx pptx
    public int getImgRes(String filename) {
        int resImg = R.mipmap.icon_file;
        if (filename.contains("zip")) {
            resImg = R.mipmap.icon_zip;
        } else if (filename.contains("rar")) {
            resImg = R.mipmap.icon_zip;
        } else if (filename.contains("txt")) {
            resImg = R.mipmap.icon_txt;
        } else if (filename.contains("xls")) {
            resImg = R.mipmap.icon_excel;
        } else if (filename.contains("pdf")) {
            resImg = R.mipmap.icon_pdf;
        } else if (filename.contains("docx")) {
            resImg = R.mipmap.icon_file;
        } else if (filename.contains("ppt")) {
            resImg = R.mipmap.icon_ppt;
        }
        return resImg;
    }


    public interface onCheckListener {
        public void onCheck(Object info);
    }

    public void setmOnCheckListener(onCheckListener mOnCheckListener) {
        this.mOnCheckListener = mOnCheckListener;
    }
}
