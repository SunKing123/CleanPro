package com.xiaoniu.cleanking.ui.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.bean.FileChildEntity;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.utils.FileSizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lang.chen on 2019/8/2
 */
public class WXImgChatAdapter extends BaseExpandableListAdapter {


    private Context mContext;

    private List<FileTitleEntity> mLists = new ArrayList<>();

    private ViewHolderParent mViewParent;
    public ViewHolderChild mViewChild;

    private WXImgAdapter mWXImgAdapter;

    private OnCheckListener onCheckListener;

    public WXImgChatAdapter(Context context) {
        this.mContext = context;
    }

    public void modifyData(List<FileTitleEntity> lists) {
        if (null == lists) {
            return;
        }

        for (int i = 0; i < lists.size(); i++) {
            FileTitleEntity entity = lists.get(i);
            if (entity == null || entity.lists == null || entity.lists.size() <= 0) {
                lists.remove(i);
                i--;
            }
        }

        if (lists.size() > 0) {
            mLists.addAll(lists);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getGroupCount() {
        return mLists.size();
    }

    public void clear() {
        mLists.clear();
    }

    public List<FileTitleEntity> getList() {
        return mLists;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return mLists.get(groupPosition).lists.size() > 0 ? 1 : 0;
    }


    @Override
    public Object getGroup(int groupPosition) {
        return mLists.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mLists.get(groupPosition).lists;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_wx_img_chat_parent, null);
            mViewParent = new ViewHolderParent(convertView);
            convertView.setTag(mViewParent);
        } else {
            mViewParent = (ViewHolderParent) convertView.getTag();
        }
        try {
            //solve umeng error -> java.lang.IndexOutOfBoundsException: Invalid index 0, size is 0
            FileTitleEntity fileTitleEntity = mLists.get(groupPosition);

            if (fileTitleEntity.size == 0) {
                mViewParent.mTxtSize.setText("");
            } else {
                mViewParent.mTxtSize.setText(FileSizeUtils.formatFileSize(fileTitleEntity.size));
            }
            if (isExpanded) {
                mViewParent.mImgArrow.setBackgroundResource(R.mipmap.arrow_up);
            } else {
                mViewParent.mImgArrow.setBackgroundResource(R.mipmap.arrow_down);
            }
            mViewParent.mTxtTitle.setText(fileTitleEntity.title);

            mViewParent.mImgSelect.setSelected(fileTitleEntity.isSelect);
            mViewParent.mImgSelect.setOnClickListener(v -> {
                if (fileTitleEntity.isSelect) {
                    fileTitleEntity.isSelect = false;
                } else {
                    fileTitleEntity.isSelect = true;
                }
                mViewParent.mImgSelect.setSelected(fileTitleEntity.isSelect);
                List<FileChildEntity> listChild = fileTitleEntity.lists;
                for (FileChildEntity childEntity : listChild) {
                    childEntity.isSelect = fileTitleEntity.isSelect;
                }
                if (null != onCheckListener) {
                    onCheckListener.onCheckAll(groupPosition, -1, fileTitleEntity.isSelect);
                }
                //notifyDataSetChanged();
            });
        } catch (Exception e) {
        }
        return convertView;
    }

    private int mSelectPosition = 0;

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_wx_img_chat_child, null);
            mViewChild = new ViewHolderChild(convertView);
            convertView.setTag(mViewChild);
        } else {
            mViewChild = (ViewHolderChild) convertView.getTag();
        }
        List<FileChildEntity> lists = mLists.get(groupPosition).lists;

        mWXImgAdapter = new WXImgAdapter(mContext, lists);
        mWXImgAdapter.setOnSelectListener(new WXImgAdapter.OnSelectListener() {
            @Override
            public void select(int position, boolean isSelect) {
                if (null != onCheckListener) {
                    mSelectPosition = position;
                    onCheckListener.onCheck(groupPosition, childPosition, isSelect);
                }
            }

            @Override
            public void onClickImg(int position) {
                if (null != onCheckListener) {
                    onCheckListener.onCheckImg(groupPosition, position);
                }
            }
        });
        mViewChild.mRecyclerView.setAdapter(mWXImgAdapter);
        mViewChild.mRecyclerView.setItemAnimator(null);

        return convertView;
    }

    public WXImgAdapter getWXImgAdapter() {
        return this.mWXImgAdapter;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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


            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 3) {

                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            mRecyclerView.setLayoutManager(layoutManager);

            ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        }
    }

    public void setOnCheckListener(OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }

    public interface OnCheckListener {
        void onCheck(int groupPosition, int position, boolean isCheck);

        void onCheckAll(int groupPosition, int position, boolean isCheck);

        void onCheckImg(int groupPosition, int position);
    }

}
