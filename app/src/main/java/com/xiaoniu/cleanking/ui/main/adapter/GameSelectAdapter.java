package com.xiaoniu.cleanking.ui.main.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.utils.GlideUtils;

import java.util.List;

public class GameSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<FirstJunkInfo> mData;
    private onCheckListener mOnCheckListener;
    private Activity mActivity;

    public GameSelectAdapter(Activity activity) {
        mActivity = activity;
    }

    public void setData(List<FirstJunkInfo> list) {
        mData = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_select, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {

            ((ImageViewHolder) holder).iv_icon.setImageDrawable(mData.get(position).getGarbageIcon());
            ((ImageViewHolder) holder).tv_name.setText(mData.get(position).getAppName());

            ((ImageViewHolder) holder).iv_icon.setOnClickListener(v -> {
                if (mOnCheckListener != null && mData.size() > 0 && position == mData.size() - 1)
                    mOnCheckListener.onCheck(mData, position);
            });

        }
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_icon;
        public TextView tv_name;

        public ImageViewHolder(View itemView) {
            super(itemView);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }

    public interface onCheckListener {
        void onCheck(List<FirstJunkInfo> listFile, int pos);
    }

    public void setmOnCheckListener(onCheckListener mOnCheckListener) {
        this.mOnCheckListener = mOnCheckListener;
    }
}
