package com.xiaoniu.cleanking.ui.main.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.utils.FileSizeUtils;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by lang.chen on 2019/8/2
 */
public class WXImgChatAdapter2 extends BaseAdapter implements StickyListHeadersAdapter {


    private Context mContext;

    private List<FileTitleEntity> mLists = new ArrayList<>();

    private ViewHolderParent mViewParent;
    public ViewHolderChild mViewChild;

    private WXImgAdapter mWXImgAdapter;

    private  OnCheckListener onCheckListener;

    public WXImgChatAdapter2(Context context) {
        this.mContext = context;
    }

    public void modifyData(List<FileTitleEntity> lists) {
        if (null != lists && lists.size() > 0) {
            mLists.addAll(lists);
            notifyDataSetChanged();
        }
    }

    public void clear(){
        mLists.clear();
    }
    public List<FileTitleEntity> getList() {
        return mLists;
    }



    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int groupPosition, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_wx_img_chat_child, null);
            mViewChild = new ViewHolderChild(convertView);
            convertView.setTag(mViewChild);
        } else {
            mViewChild = (ViewHolderChild) convertView.getTag();
        }
        List<FileChildEntity> lists = mLists.get(groupPosition).lists;
        if(mWXImgAdapter==null){
            //mViewChild.mRecyclerView.addItemDecoration(new GrideWXImgManagerWrapper(DensityUtil.dp2px(4)));
        }
        mWXImgAdapter = new WXImgAdapter(mContext, lists);
        mWXImgAdapter.setOnSelectListener(new WXImgAdapter.OnSelectListener() {
            @Override
            public void select(int position, boolean isSelect) {
                if(null!=onCheckListener){
                    onCheckListener.onCheck(groupPosition,position,isSelect);
                }
            }

            @Override
            public void onClickImg(int position) {
                if(null!=onCheckListener){
                    onCheckListener.onCheckImg(groupPosition,position);
                }
            }
        });
        mViewChild.mRecyclerView.setAdapter(mWXImgAdapter);
        mViewChild.mRecyclerView.setItemAnimator(null);

        return convertView;
    }



    public WXImgAdapter getWXImgAdapter(){
        return  this.mWXImgAdapter;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_wx_img_chat_parent, null);
            mViewParent = new ViewHolderParent(convertView);
            convertView.setTag(mViewParent);
        } else {
            mViewParent = (ViewHolderParent) convertView.getTag();
        }
        FileTitleEntity fileTitleEntity = mLists.get(position);

        if(fileTitleEntity.size==0){
            mViewParent.mTxtSize.setText("");
        }else {
            mViewParent.mTxtSize.setText(FileSizeUtils.formatFileSize(fileTitleEntity.size));
        }
//        if (isExpanded) {
//            mViewParent.mImgArrow.setBackgroundResource(R.mipmap.arrow_up);
//        } else {
//            mViewParent.mImgArrow.setBackgroundResource(R.mipmap.arrow_down);
//        }
        mViewParent.mTxtTitle.setText(fileTitleEntity.title);

        mViewParent.mImgSelect.setSelected(fileTitleEntity.isSelect);
        mViewParent.mImgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileTitleEntity.isSelect) {
                    fileTitleEntity.isSelect = false;
                } else {
                    fileTitleEntity.isSelect = true;
                }
                List<FileChildEntity> listChild = fileTitleEntity.lists;
                for (FileChildEntity childEntity : listChild) {
                    childEntity.isSelect = fileTitleEntity.isSelect;
                }
                if(null!=onCheckListener){
                    onCheckListener.onCheck(position,-1,fileTitleEntity.isSelect);
                }
                //notifyDataSetChanged();
            }
        });

        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return position;
    }


    class ViewHolderParent {

        TextView mTxtTitle;
        //文件总大小
        TextView mTxtSize;
        ImageView mImgArrow;
        ImageView mImgSelect;

        public ViewHolderParent(View view) {
            mTxtTitle = view.findViewById(R.id.txt_title);
            mTxtSize = view.findViewById(R.id.txt_size);
            mImgArrow = view.findViewById(R.id.img_arrow);
            mImgSelect = view.findViewById(R.id.check_select);

        }

    }


   public class ViewHolderChild {

        public RecyclerView mRecyclerView;

        public ViewHolderChild(View view) {
            mRecyclerView = view.findViewById(R.id.recycle_view);


            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 3){

                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            mRecyclerView.setLayoutManager(layoutManager);

        }
    }

    public void setOnCheckListener(OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

    public interface OnCheckListener{
        void  onCheck(int groupPosition, int position, boolean isCheck);

        void onCheckImg(int groupPosition, int position);
    }

}
