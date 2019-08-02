package com.xiaoniu.cleanking.ui.main.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.FragmentComponent;
import com.xiaoniu.cleanking.base.BaseFragment;
import com.xiaoniu.cleanking.ui.main.adapter.WXImgChatAdapter;
import com.xiaoniu.cleanking.ui.main.bean.FileTitleEntity;
import com.xiaoniu.cleanking.ui.main.event.WXImgCameraEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;

/**
 * 微信拍摄图片清理
 * Created by lang.chen on 2019/8/1
 */
public class WXImgCameraFragment extends BaseFragment {


    @BindView(R.id.list_view_camera)
    ExpandableListView mListView;
    private WXImgChatAdapter mAdapter;

    public static WXImgCameraFragment newInstance() {
        WXImgCameraFragment instance = new WXImgCameraFragment();

        return instance;
    }

    @Override
    protected void inject(FragmentComponent fragmentComponent) {

    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.wx_img_camera;
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
    }

    @Subscribe
    public void onEnvent(WXImgCameraEvent wxImgCameraEvent){
        List<FileTitleEntity> lists=(List<FileTitleEntity>)wxImgCameraEvent.object;
        mAdapter.modifyData(lists);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
