package com.xiaoniu.cleanking.ui.main.adapter;

import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.FileEntity;
import com.xiaoniu.cleanking.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageShowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<FileEntity> listImage = new ArrayList<>();

    public void setIsCheckAll(boolean isCheckAll) {
        for(int i=0;i<this.listImage.size();i++){
            this.listImage.get(i).setCheck(isCheckAll);
        }
        notifyDataSetChanged();
    }

    public void deleteData(List<FileEntity> tempList) {
        listImage.removeAll(tempList);
        Log.e("gfd","删除后："+listImage.size());
        notifyDataSetChanged();
    }

    public List<FileEntity> getListImage() {
        return listImage;
    }

    Activity mActivity;
    onCheckListener mOnCheckListener;


    public ImageShowAdapter(Activity mActivity, List<FileEntity> listImage) {
        super();
        this.mActivity = mActivity;
        this.listImage = listImage;
        Log.e("gfd","初始："+listImage.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_list, parent, false);
        return new ImageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ImageViewHolder) {
            String path = listImage.get(position).getPath();
            Glide.with(mActivity)
                    .load(path)
                    .into(((ImageViewHolder) holder).iv_photo_filelist_pic);
            ConstraintLayout.LayoutParams llp = (ConstraintLayout.LayoutParams) ((ImageViewHolder) holder).iv_photo_filelist_pic.getLayoutParams();
            llp.width = (DeviceUtils.getScreenWidth() - DeviceUtils.dip2px(48)) / 3;
            llp.height = llp.width;
            ((ImageViewHolder) holder).iv_photo_filelist_pic.setLayoutParams(llp);
            ((ImageViewHolder) holder).cb_choose.setChecked((boolean)listImage.get(position).getIsCheck());
            ((ImageViewHolder) holder).cb_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listImage.get(position).setCheck(isChecked);
                    if (mOnCheckListener != null)
                        mOnCheckListener.onCheck(listImage,position);
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
        public CheckBox cb_choose;

        public ImageViewHolder(View itemView) {
            super(itemView);
            iv_photo_filelist_pic = (ImageView) itemView.findViewById(R.id.iv_photo_filelist_pic);
            cb_choose = (CheckBox) itemView.findViewById(R.id.cb_choose);
        }
    }





    public interface onCheckListener {
        public void onCheck(List<FileEntity> listFile,int pos);
    }

    public void setmOnCheckListener(onCheckListener mOnCheckListener) {
        this.mOnCheckListener = mOnCheckListener;
    }
}
