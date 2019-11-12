package com.xiaoniu.cleanking.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProcessIconAdapter extends RecyclerView.Adapter<ProcessIconAdapter.ViewHolder> {

    private List<FirstJunkInfo> mList;

    public void setData(List<FirstJunkInfo> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_icon, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirstJunkInfo junkInfo = mList.get(position);
        holder.mViewImageIcon.setImageDrawable(junkInfo.getGarbageIcon());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.view_image_icon)
        ImageView mViewImageIcon;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
