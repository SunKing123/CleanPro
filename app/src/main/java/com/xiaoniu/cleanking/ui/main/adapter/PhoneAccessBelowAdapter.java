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
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;

import java.util.ArrayList;
import java.util.List;

public class PhoneAccessBelowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<FirstJunkInfo> listImage;
    private onCheckListener mOnCheckListener;

    public void deleteData(List<FirstJunkInfo> tempList) {
        if (listImage != null)
            listImage.removeAll(tempList);
        notifyDataSetChanged();
    }

    public ArrayList<FirstJunkInfo> getListImage() {
        return listImage;
    }

    public PhoneAccessBelowAdapter(Activity mActivity, ArrayList<FirstJunkInfo> listImage) {
        super();
        this.listImage = listImage;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phone_access, parent, false);
        return new ImageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            ((ImageViewHolder) holder).iv_photo_filelist_pic.setImageDrawable(listImage.get(position).getGarbageIcon());
            ((ImageViewHolder) holder).tv_size.setText(CleanAllFileScanUtil.byte2FitSize(listImage.get(position).getTotalSize()));
            ((ImageViewHolder) holder).tv_name.setText(listImage.get(position).getAppName());
            ((ImageViewHolder) holder).tv_select.setBackgroundResource(listImage.get(position).getIsSelect() ? R.drawable.icon_select : R.drawable.icon_unselect);
            ((ImageViewHolder) holder).tv_select.setOnClickListener(v -> {
                listImage.get(position).setIsSelect(!listImage.get(position).getIsSelect());
                ((ImageViewHolder) holder).tv_select.setBackgroundResource(listImage.get(position).getIsSelect() ? R.drawable.icon_select : R.drawable.icon_unselect);
                if (mOnCheckListener != null)
                    mOnCheckListener.onCheck(listImage, position);
            });

        }
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_photo_filelist_pic;
        public TextView tv_select;
        public TextView tv_size;
        public TextView tv_name;

        public ImageViewHolder(View itemView) {
            super(itemView);
            iv_photo_filelist_pic = itemView.findViewById(R.id.iv_photo_filelist_pic);
            tv_select = itemView.findViewById(R.id.tv_select);
            tv_size = itemView.findViewById(R.id.tv_size);
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
