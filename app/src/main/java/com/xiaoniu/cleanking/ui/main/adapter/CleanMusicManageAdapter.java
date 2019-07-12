package com.xiaoniu.cleanking.ui.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.bean.MusciInfoBean;
import com.xiaoniu.cleanking.ui.main.bean.VideoInfoBean;
import com.xiaoniu.cleanking.utils.DateUtils;
import com.xiaoniu.cleanking.utils.FileSizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 安装包管理
 * Created by lang.chen on 2019/7/2
 */
public class CleanMusicManageAdapter extends RecyclerView.Adapter {


    private List<MusciInfoBean> mLists = new ArrayList<>();

    private LayoutInflater mInflater;


    private Context mContext;

    private OnCheckListener onCheckListener;

    public CleanMusicManageAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_music_file_manage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MusciInfoBean appInfoBean = getLists().get(position);

        if (holder.getClass() == ViewHolder.class) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.mTxtName.setText(appInfoBean.name);
            viewHolder.mTxtSize.setText(FileSizeUtils.formatFileSize(appInfoBean.packageSize));
            viewHolder.mTxtTime.setText(String.format("时长:%s",appInfoBean.time));
            if (appInfoBean.isSelect) {
                viewHolder.mCheckSelect.setSelected(true);
            } else {
                viewHolder.mCheckSelect.setSelected(false);
            }
            viewHolder.mLLCheckSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onCheckListener) {
                        onCheckListener.onCheck(appInfoBean.path, !appInfoBean.isSelect);
                    }
                }
            });
            viewHolder.mLLContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=onCheckListener){
                        onCheckListener.play(appInfoBean);
                    }
                }
            });
        }
    }


    public void playAudio(String audioPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file:///" + audioPath);
        intent.setDataAndType(uri, "audio/mp3");
        mContext.startActivity(intent);
    }

    public void clear() {
        mLists.clear();
    }

    public List<MusciInfoBean> getLists() {
        return mLists;
    }

    public void modifyList(List<MusciInfoBean> appInfoBeans) {
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

        void play(MusciInfoBean musciInfoBean);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        //名称
        private TextView mTxtName;
        /**
         * 播放时长
         **/
        private TextView mTxtTime;
        private TextView mTxtSize;
        private ImageButton mCheckSelect;
        private LinearLayout mLLContent;
        private LinearLayout mLLCheckSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            mTxtTime = itemView.findViewById(R.id.txt_time);
            mTxtName = itemView.findViewById(R.id.txt_name);
            mTxtSize = itemView.findViewById(R.id.txt_size);
            mCheckSelect = itemView.findViewById(R.id.check_select);
            mLLContent = itemView.findViewById(R.id.ll_content);
            mLLCheckSelect=itemView.findViewById(R.id.ll_check_select);
        }
    }

    public void setOnCheckListener(CleanMusicManageAdapter.OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

}
