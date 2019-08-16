package com.xiaoniu.cleanking.ui.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.VideoInfoBean;
import com.xiaoniu.cleanking.utils.FileSizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 安装包管理
 * Created by lang.chen on 2019/7/2
 */
public class CleanVideoManageAdapter extends RecyclerView.Adapter {


    private List<VideoInfoBean> mLists = new ArrayList<>();

    private LayoutInflater mInflater;


    private Context mContext;

    private OnCheckListener onCheckListener;

    public CleanVideoManageAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getItemViewType(int position) {
        VideoInfoBean videoInfoBean = getLists().get(position);
        return videoInfoBean.itemType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = mInflater.inflate(R.layout.item_clean_video_title, parent, false);
            return new ViewHolderTitle(view);
        }
        View view = mInflater.inflate(R.layout.item_video_file_manage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VideoInfoBean appInfoBean = getLists().get(position);

        if (holder.getClass() == ViewHolderTitle.class) {
            ViewHolderTitle viewHolderTitle = (ViewHolderTitle) holder;
            viewHolderTitle.mTxtTitle.setText(appInfoBean.date);
        } else if (holder.getClass() == ViewHolder.class) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.mTxtSize.setText(FileSizeUtils.formatFileSize(appInfoBean.packageSize));
            setImgFrame(viewHolder.mImgFrame, appInfoBean.path);
            if (appInfoBean.isSelect) {
                viewHolder.mCheckSelect.setSelected(true);
            } else {
                viewHolder.mCheckSelect.setSelected(false);
            }
            viewHolder.mCheckSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onCheckListener) {
                        onCheckListener.onCheck(appInfoBean.path, !appInfoBean.isSelect);
                    }
                }
            });

            viewHolder.mLLPlay.setOnClickListener(v -> {
                if(null!=onCheckListener){
                    onCheckListener.play(appInfoBean);
                }

            });
        }
    }






    /**
     * 设置视频第一帧图片
     */
    private void setImgFrame(ImageView imgFrame, String path) {

        RequestOptions options = new RequestOptions()
                .placeholder(R.color.color_666666)// 正在加载中的图片
                .diskCacheStrategy(DiskCacheStrategy.ALL); // 磁盘缓存策略

        Glide.with(mContext).load(path).apply(options).into(imgFrame);

    }

    public void playAudio(String audioPath) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("file:///" + audioPath);
            intent.setDataAndType(uri, "video/*");
            mContext.startActivity(intent);
        } catch (Exception e) {

        }
    }

    public void clear() {
        mLists.clear();
    }

    public List<VideoInfoBean> getLists() {
        return mLists;
    }

    public void modifyList(List<VideoInfoBean> appInfoBeans) {
        if (null != appInfoBeans) {
            mLists.addAll(appInfoBeans);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mLists.size();
    }


    public interface OnCheckListener {
        void onCheck(String id, boolean isChecked);

        void play(VideoInfoBean videoInfoBean);
    }

    class ViewHolderTitle extends RecyclerView.ViewHolder {

        private TextView mTxtTitle;

        public ViewHolderTitle(View itemView) {
            super(itemView);
            mTxtTitle = itemView.findViewById(R.id.txt_title);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * 播放时长
         **/
        private TextView mTxtSize;
        private ImageButton mCheckSelect;
        private ImageView mImgFrame;
        private LinearLayout mLLPlay;

        public ViewHolder(View itemView) {
            super(itemView);
            mTxtSize = itemView.findViewById(R.id.txt_size_file);
            mCheckSelect = itemView.findViewById(R.id.check_select);
            mImgFrame = itemView.findViewById(R.id.img);
            mLLPlay=itemView.findViewById(R.id.ll_play);
        }
    }

    public void setOnCheckListener(CleanVideoManageAdapter.OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

}
