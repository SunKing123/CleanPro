package com.xiaoniu.cleanking.ui.main.adapter;

import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageShowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Map<String, String>> listImage = new ArrayList<>();
    Activity mActivity;

    public ImageShowAdapter(Activity mActivity, List<Map<String, String>> listImage) {
        super();
        this.mActivity = mActivity;
        this.listImage = listImage;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_list, parent, false);
        return new ImageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            long totalSize = 0;
            String mediaBean = listImage.get(position).get("path");
            Glide.with(mActivity)
                    .load(mediaBean)
                    .into(((ImageViewHolder) holder).iv_photo_filelist_pic);
            ConstraintLayout.LayoutParams llp = (ConstraintLayout.LayoutParams) ((ImageViewHolder) holder).iv_photo_filelist_pic.getLayoutParams();
            llp.width = (DeviceUtils.getScreenWidth() - DeviceUtils.dip2px(48)) / 3;
            llp.height = llp.width;
            ((ImageViewHolder) holder).iv_photo_filelist_pic.setLayoutParams(llp);

        }
    }

    @Override
    public int getItemCount() {
        return listImage.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_photo_filelist_pic;

        public ImageViewHolder(View itemView) {
            super(itemView);
            iv_photo_filelist_pic = (ImageView) itemView.findViewById(R.id.iv_photo_filelist_pic);
        }
    }


}
