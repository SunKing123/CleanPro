package com.xiaoniu.cleanking.ui.news.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendListEntity;
import com.xiaoniu.cleanking.utils.GlideUtils;

import java.util.List;

public class HomeRecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<HomeRecommendListEntity> mData;
    private onCheckListener mOnCheckListener;
    private Activity mActivity;

    public HomeRecommendAdapter(Activity activity) {
        mActivity = activity;
    }

    public void setData(List<HomeRecommendListEntity> list) {
        mData = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_recommend, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            if (TextUtils.isEmpty(mData.get(position).getButtonName())) {
                ((ImageViewHolder) holder).tv_button.setVisibility(View.GONE);
            }

            GlideUtils.loadImage(mActivity, mData.get(position).getIconUrl(), ((ImageViewHolder) holder).iv_icon);
            ((ImageViewHolder) holder).tv_name.setText(mData.get(position).getName());
            ((ImageViewHolder) holder).tv_content.setText(mData.get(position).getContent());
            ((ImageViewHolder) holder).tv_button.setText(mData.get(position).getButtonName());

            ((ImageViewHolder) holder).v_item.setOnClickListener(v -> {
                if (mOnCheckListener != null)
                    mOnCheckListener.onCheck(mData, position);
            });

        }
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public View v_item;
        public ImageView iv_icon;
        public TextView tv_name;
        public TextView tv_content;
        public TextView tv_button;

        public ImageViewHolder(View itemView) {
            super(itemView);
            v_item = itemView.findViewById(R.id.v_item);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_button = itemView.findViewById(R.id.tv_button);
        }
    }

    public interface onCheckListener {
        void onCheck(List<HomeRecommendListEntity> listFile, int pos);
    }

    public void setmOnCheckListener(onCheckListener mOnCheckListener) {
        this.mOnCheckListener = mOnCheckListener;
    }
}
