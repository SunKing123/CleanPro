package com.xiaoniu.cleanking.ui.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ExpandableListView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.adapter.WXImgChatAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.ui.main.presenter.CleanMainPresenter;
import com.xiaoniu.cleanking.ui.main.presenter.WXCleanImgPresenter;
import com.xiaoniu.cleanking.ui.main.presenter.WXCleanSaveListPresenter;
import java.util.List;

import butterknife.BindView;

/**
 * 微信图片保存图片
 * Created by lang.chen on 2019/8/1
 */
public class WXImgSaveListFragment extends BaseFragment<WXCleanSaveListPresenter> {

    @BindView(R.id.list_view_save_list)
    ExpandableListView mListView;

    private WXImgChatAdapter mAdapter;

    public static WXImgSaveListFragment newInstance() {
        WXImgSaveListFragment instance = new WXImgSaveListFragment();
        return instance;
    }


    @Override
    protected void inject(FragmentComponent fragmentComponent) {
        fragmentComponent.inject(this);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.wx_img_save_list;
    }

    @Override
    protected void initView() {
        mAdapter=new WXImgChatAdapter(getContext());
        mListView.setAdapter(mAdapter);
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                List<FileTitleEntity> lists=mAdapter.getList();
                for(int i=0;i<lists.size();i++){
                    if(i==groupPosition){
                        FileTitleEntity fileTitleEntity=lists.get(groupPosition);
                        if(fileTitleEntity.isExpand){
                            fileTitleEntity.isExpand=false;
                        }else {
                            fileTitleEntity.isExpand=true;
                        }
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();

                return false;
            }
        });
        mPresenter.init(mContext);
        mPresenter.start();
    }


    public void updateImgSaveList(List<FileTitleEntity> lists) {

        mAdapter.modifyData(lists);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
