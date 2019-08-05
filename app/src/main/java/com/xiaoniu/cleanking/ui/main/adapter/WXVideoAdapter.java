package com.xiaoniu.cleanking.ui.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;

import java.io.File;
import java.util.List;

/**
 * Created by lang.chen on 2019/8/2
 */
public class WXVideoAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<FileChildEntity> mLists;

    private  OnSelectListener onSelectListener;

    public WXVideoAdapter(Context context, List<FileChildEntity> lists) {
        this.mContext = context;
        this.mLists=lists;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_wx_video_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        FileChildEntity fileChildEntity = mLists.get(position);

        RequestOptions options = new RequestOptions()
                .placeholder(R.color.color_666666)// 正在加载中的图片
                .diskCacheStrategy(DiskCacheStrategy.ALL); // 磁盘缓存策略
        Glide.with(mContext).load(new File(fileChildEntity.path)).apply(options).into(viewHolder.mImg);
        if (fileChildEntity.isSelect) {
            viewHolder.mImgSelect.setBackgroundResource(R.mipmap.icon_check);
        } else {
            viewHolder.mImgSelect.setBackgroundResource(R.mipmap.icon_check_normal);
        }


        viewHolder.mLLSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fileChildEntity.isSelect){
                    fileChildEntity.isSelect=false;
                }else {
                    fileChildEntity.isSelect=true;
                }
                if(null!=onSelectListener){
                    onSelectListener.select(position,fileChildEntity.isSelect);
                }
                notifyDataSetChanged();

            }
        });

        viewHolder.mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null!=onSelectListener){
                    onSelectListener.onClickImg(position);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return mLists.size();
    }

    public  void modifyData(List<FileChildEntity> lists){
        if(null!=lists){
            this.mLists.addAll(lists);
            notifyDataSetChanged();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImg;
        private ImageView mImgSelect;
        private LinearLayout mLLSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mImgSelect = itemView.findViewById(R.id.check_select);
            mLLSelect=itemView.findViewById(R.id.ll_check_select);
        }
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    interface   OnSelectListener{
        void select(int position, boolean isSelect);

        void onClickImg(int position);
    }
}
