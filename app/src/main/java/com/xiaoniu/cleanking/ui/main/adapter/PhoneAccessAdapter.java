package com.xiaoniu.cleanking.ui.main.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.activity.PreviewImageActivity;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.utils.CleanAllFileScanUtil;
import com.xiaoniu.cleanking.utils.DeviceUtils;
import com.xiaoniu.cleanking.utils.ExtraConstant;

import java.util.ArrayList;
import java.util.List;

public class PhoneAccessAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<FileEntity> listImage = new ArrayList<>();



    public void setIsCheckAll(boolean isCheckAll) {

        for (int i = 0; i < this.listImage.size(); i++) {
            listImage.get(i).setIsSelect(isCheckAll);
        }
        notifyDataSetChanged();
    }

    public void deleteData(List<FileEntity> tempList) {
        listImage.removeAll(tempList);
        Log.e("gfd", "删除后：" + listImage.size());
        notifyDataSetChanged();
    }

    public List<FileEntity> getListImage() {
        return listImage;
    }

    Activity mActivity;
    onCheckListener mOnCheckListener;


    public PhoneAccessAdapter(Activity mActivity, List<FileEntity> listImage) {
        super();
        this.mActivity = mActivity;
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
            String path = listImage.get(position).getPath();
            Glide.with(mActivity)
                    .load(path)
                    .into(((ImageViewHolder) holder).iv_photo_filelist_pic);

            ((ImageViewHolder) holder).tv_select.setBackgroundResource(listImage.get(position).getIsSelect() ? R.drawable.icon_select : R.drawable.icon_unselect);
            ((ImageViewHolder) holder).tv_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listImage.get(position).setIsSelect(!listImage.get(position).getIsSelect());
                    ((ImageViewHolder) holder).tv_select.setBackgroundResource(listImage.get(position).getIsSelect() ? R.drawable.icon_select : R.drawable.icon_unselect);
                    if (mOnCheckListener != null)
                        mOnCheckListener.onCheck(listImage, position);
                }
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

        public ImageViewHolder(View itemView) {
            super(itemView);
            iv_photo_filelist_pic = (ImageView) itemView.findViewById(R.id.iv_photo_filelist_pic);
            tv_select = (TextView) itemView.findViewById(R.id.tv_select);
        }
    }


    public interface onCheckListener {
        public void onCheck(List<FileEntity> listFile, int pos);
    }

    public void setmOnCheckListener(onCheckListener mOnCheckListener) {
        this.mOnCheckListener = mOnCheckListener;
    }
}
