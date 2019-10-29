package com.xiaoniu.cleanking.ui.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.ImgBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 问题反馈图片选择
 * Created by lang.chen on 2019/7/5
 */
public class QuestionReportImgAdapter extends RecyclerView.Adapter {
    private List<ImgBean> mLists = new ArrayList<>();

    private Context mContext;
    private LayoutInflater mInflater;
    private OnItemImgClickListener mOnItemImgClickListener;

    public QuestionReportImgAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }


    public void modifyData(List<ImgBean> imgBeans) {
        if (imgBeans.size() > 0) {
            this.mLists.addAll(imgBeans);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        mLists.clear();
    }

    public List<ImgBean> getLists() {
        return this.mLists;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_question_report_img, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ImgBean bean = mLists.get(position);
        if (holder.getClass() == ViewHolder.class) {
            ViewHolder mViewHolder = (ViewHolder) holder;

            //显示照片选择图片
            if (bean.itemType == 1) {
                Glide.with(mContext).load(R.mipmap.icon_btn_camera).into(mViewHolder.mImg);
                mViewHolder.mImgClose.setVisibility(View.INVISIBLE);
                mViewHolder.mImg.setOnClickListener(v -> {
                    if (null != mOnItemImgClickListener) {
                        mOnItemImgClickListener.onSelectImg();
                    }
                });
            }else {
                mViewHolder.mImg.setOnClickListener(null);
                mViewHolder.mImgClose.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(new File(bean.path)).into(mViewHolder.mImg);
                mViewHolder.mImgClose.setOnClickListener(v -> {
                    if(null!=mOnItemImgClickListener){
                        mOnItemImgClickListener.onDelImg(position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }


    /**
     * 照片选择监听
     */
    public interface OnItemImgClickListener {
        void onSelectImg();

        void onDelImg(int position);
    }

    public void setOnItemImgClickListener(OnItemImgClickListener onItemImgClickListener) {
        this.mOnItemImgClickListener = onItemImgClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImg;
        private ImageView mImgClose;

        public ViewHolder(View itemView) {
            super(itemView);
            mImg = itemView.findViewById(R.id.img);
            mImgClose=itemView.findViewById(R.id.img_close);
        }
    }
}
