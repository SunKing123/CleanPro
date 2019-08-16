package com.xiaoniu.cleanking.ui.main.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 加速白名单管理
 * Created by lang.chen on 2019/7/2
 */
public class WhiteListSpeedAdapter extends RecyclerView.Adapter {


    private List<AppInfoBean> mLists = new ArrayList<>();

    private LayoutInflater mInflater;


    private Context mContext;

    private OnCheckListener onCheckListener;

    public WhiteListSpeedAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_white_list_speed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AppInfoBean appInfoBean = getLists().get(position);

        if (holder.getClass() == ViewHolder.class) {
            ViewHolder viewHolder = (ViewHolder) holder;
            Glide.with(mContext).load(appInfoBean.icon).into(viewHolder.mImgIcon);
            viewHolder.mTxtName.setText(appInfoBean.name);
            viewHolder.mBtnRemove.setOnClickListener(v -> {
                if (null != onCheckListener) {
                    onCheckListener.onCheck(appInfoBean.packageName);
                }
            });

        }
    }


    public void clear() {
        mLists.clear();
    }

    public List<AppInfoBean> getLists() {
        return mLists;
    }

    public void modifyList(List<AppInfoBean> appInfoBeans) {
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
        void onCheck(String id);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImgIcon;
        //名称
        private TextView mTxtName;
        //移除
        private Button mBtnRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            mImgIcon = itemView.findViewById(R.id.img_icon);
            mTxtName = itemView.findViewById(R.id.txt_name);
            mBtnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }

    public void setOnCheckListener(WhiteListSpeedAdapter.OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

}
