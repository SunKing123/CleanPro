package com.xiaoniu.cleanking.ui.viruskill.newversion.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.widget.RotationLoadingView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.viruskill.newversion.model.ScanTextItemModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xinxiaolong on 2020/7/22.
 * email：xinxiaolong123@foxmail.com
 */
public class VirusScanTextItemAdapter extends RecyclerView.Adapter<VirusScanTextItemAdapter.MineViewHolder> {

    private List<ScanTextItemModel> modelList = new ArrayList<>();
    private ScanTextItemModel lastModel;

    @NonNull
    @Override
    public MineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_virus_scan_text_layout, parent, false);
        return new MineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MineViewHolder holder, int position) {
        holder.bindData(modelList.get(position));
    }

    public void clean() {
        lastModel = null;
        modelList.clear();
        notifyDataSetChanged();
    }


    public void addData(ScanTextItemModel model) {
        modelList.add(model);
        notifyItemInserted(modelList.size() - 1);
        updateState();
        lastModel = model;
    }

    public void updateState() {
        if (lastModel != null) {
            lastModel.state = lastModel.warning ? 2 : 1;
            notifyDataSetChanged();
        }
    }

    public void removeTop() {
        modelList.remove(0);
        notifyItemRemoved(0);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MineViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_virus_scan_text)
        TextView tvText;
        @BindView(R.id.image_virus_complete)
        ImageView imageState;
        @BindView(R.id.rotationLoading)
        RotationLoadingView loadingView;

        public MineViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            loadingView.setResId(R.mipmap.icon_dengdai_white);
        }

        public void bindData(ScanTextItemModel model) {
            tvText.setText(model.name);
            switch (model.state) {
                case 0:
                    imageState.setVisibility(View.GONE);
                    loadingView.setVisibility(View.VISIBLE);
                    loadingView.startRotationAnimation();
                    break;
                case 1:
                    imageState.setImageResource(R.drawable.icon_virus_ok);
                    imageState.setVisibility(View.VISIBLE);
                    loadingView.setVisibility(View.GONE);
                    break;
                case 2:
                    imageState.setImageResource(R.drawable.icon_virus_no_ok);
                    imageState.setVisibility(View.VISIBLE);
                    loadingView.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
