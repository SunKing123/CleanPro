package com.xiaoniu.cleanking.ui.viruskill.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xinxiaolong on 2020/7/22.
 * email：xinxiaolong123@foxmail.com
 */
public class VirusScanIconItemAdapter extends RecyclerView.Adapter<VirusScanIconItemAdapter.MineViewHolder> {

    ArrayList<FirstJunkInfo> mList = new ArrayList<>();

    @NonNull
    @Override
    public MineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_virus_scan_icon_layout, parent, false);
        return new MineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MineViewHolder holder, int position) {
        holder.bindData(mList.get(position));
    }

    public void setDataList(ArrayList<FirstJunkInfo> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MineViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_virus_icon)
        ImageView imageIcon;

        public MineViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(FirstJunkInfo model) {
            imageIcon.setImageDrawable(model.getGarbageIcon());
        }
    }

}
