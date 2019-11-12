package com.xiaoniu.cleanking.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;

import java.io.File;
import java.util.List;

/**
 * Created by lang.chen on 2019/8/2
 */
public class WXImgAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<FileChildEntity> mLists;

    private OnSelectListener onSelectListener;

    public WXImgAdapter(Context context, List<FileChildEntity> lists) {
        this.mContext = context;
        this.mLists = lists;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_wx_img_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        FileChildEntity fileChildEntity = mLists.get(position);

        RequestOptions options = new RequestOptions()
                .placeholder(R.color.color_666666)
                .dontAnimate();// 正在加载中的图片


        Glide.with(mContext).load(new File(fileChildEntity.path)).apply(options).into(viewHolder.mImg);

        if (fileChildEntity.isSelect) {
            viewHolder.mImgSelect.setBackgroundResource(R.mipmap.icon_check);
            viewHolder.mImgLayer.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mImgSelect.setBackgroundResource(R.mipmap.icon_check_normal);
            viewHolder.mImgLayer.setVisibility(View.GONE);
        }


        viewHolder.mLLSelect.setOnClickListener(v -> {
            if (fileChildEntity.isSelect) {
                fileChildEntity.isSelect = false;
            } else {
                fileChildEntity.isSelect = true;
            }
            if (fileChildEntity.isSelect) {
                viewHolder.mImgSelect.setBackgroundResource(R.mipmap.icon_check);
                viewHolder.mImgLayer.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mImgSelect.setBackgroundResource(R.mipmap.icon_check_normal);
                viewHolder.mImgLayer.setVisibility(View.GONE);
            }

            if (null != onSelectListener) {
                onSelectListener.select(position, fileChildEntity.isSelect);
            }
            //notifyDataSetChanged();

        });

        viewHolder.mImg.setOnClickListener(v -> {
            if (null != onSelectListener) {
                onSelectListener.onClickImg(position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mLists.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void modifyData(List<FileChildEntity> lists) {
        if (null != lists) {
            this.mLists.addAll(lists);
            notifyDataSetChanged();
        }
    }


    private class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImg;
        private ImageView mImgSelect;
        private LinearLayout mLLSelect;
        private ImageView mImgLayer;

        public ViewHolder(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mImgSelect = itemView.findViewById(R.id.check_select);
            mLLSelect = itemView.findViewById(R.id.ll_check_select);
            mImgLayer = itemView.findViewById(R.id.img_layer);
        }
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    interface OnSelectListener {
        void select(int position, boolean isSelect);

        void onClickImg(int position);
    }
}
