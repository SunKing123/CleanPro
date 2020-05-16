package com.xiaoniu.cleanking.ui.news.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.news.adapter.NewsListAdapter;
import com.xiaoniu.cleanking.ui.main.bean.NewsListInfo;
import com.xiaoniu.cleanking.ui.main.bean.NewsType;
import com.xiaoniu.cleanking.ui.news.listener.OnClickNewsItemListener;
import com.xiaoniu.cleanking.ui.news.utils.NewsUtils;
import com.xiaoniu.common.base.BaseFragment;
import com.xiaoniu.common.http.EHttp;
import com.xiaoniu.common.http.callback.ApiCallback;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.ToastUtils;


public class NewsListFragment extends BaseFragment {

    private static final String TAG = "NewsListFragment";

    private static final String KEY_TYPE = "TYPE";
    private static final String PAGE_POSITION = "PAGE_POSITION";
    private XRecyclerView mRecyclerView;
    private NewsListAdapter mNewsAdapter;
    private LinearLayout mLlNoNet;
    private NewsType mType;
    private String pagePosition;

    private boolean mIsRefresh = true;

    private OnClickNewsItemListener mOnClickItemListener;  // XD added 20200514

    public static NewsListFragment getInstance(NewsType type,String pagePosition) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TYPE, type);
        bundle.putString(PAGE_POSITION,pagePosition);
        NewsListFragment newsListFragment = new NewsListFragment();
        newsListFragment.setArguments(bundle);
        return newsListFragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_news_list;
    }

    @Override
    protected void initVariable(Bundle arguments) {


        if (arguments != null) {
            mType = (NewsType) arguments.getSerializable(KEY_TYPE);
            pagePosition=arguments.getString(PAGE_POSITION);
        }

        mNewsAdapter = new NewsListAdapter(getContext(),pagePosition);
        if (mOnClickItemListener != null) {
            mNewsAdapter.setOnClickItemListener(mOnClickItemListener);
        }

        setSupportLazy(true);
    }

    @Override
    protected void initViews(View contentView, Bundle savedInstanceState) {
        mLlNoNet = contentView.findViewById(R.id.layout_not_net);
        mRecyclerView = contentView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setLimitNumberToCallLoadMore(2);
        mRecyclerView.setAdapter(mNewsAdapter);

        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
    }

    @Override
    protected void setListener() {
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mIsRefresh = true;
                startLoadData();
            }

            @Override
            public void onLoadMore() {
                mIsRefresh = false;
                startLoadData();
            }
        });

        mLlNoNet.setOnClickListener(v -> {
            if (!NetworkUtils.isNetConnected())
                ToastUtils.showShort(getString(R.string.tool_no_net_hint));

            mIsRefresh = true;
            startLoadData();
        });
    }

    @Override
    protected void loadData() {
        mRecyclerView.refresh();
    }

    public void startLoadData() {
        if (!NetworkUtils.isNetConnected()) {
            if (mLlNoNet != null)
                mLlNoNet.setVisibility(View.VISIBLE);
            return;
        }

        if (mType != null) {
            loadNewsData();
        }
    }

    private void loadNewsData() {
        String type = mType.getValue();
        String lastId = SPUtil.getLastNewsID(mType.getName());
        if (mIsRefresh) {
            lastId = "";
        }
        String url = SpCacheConfig.NEWS_BASEURL + "&type=" + type + "&startkey=" + lastId + "&num=" + NewsUtils.NEWS_PAGE_SIZE;
        EHttp.get(this, url, new ApiCallback<NewsListInfo>(null) {
            @Override
            public void onFailure(Throwable e) {
            }

            @Override
            public void onSuccess(NewsListInfo result) {
                if (result != null && result.data != null && result.data.size() > 0) {
                    if (mLlNoNet.getVisibility() == View.VISIBLE)
                        mLlNoNet.setVisibility(View.GONE);
                    SPUtil.setLastNewsID(mType.getName(), result.data.get(result.data.size() - 1).rowkey);
                    if (mIsRefresh) {
                        mNewsAdapter.setData(result.data);
                    } else {
                        mNewsAdapter.addData(result.data);
                    }
                }
            }

            @Override
            public void onComplete() {
                if (mIsRefresh) {
                    mRecyclerView.refreshComplete();
                } else {
                    mRecyclerView.loadMoreComplete();
                }
            }
        });
    }

    public void setIsRefresh(boolean isRefresh) {
        this.mIsRefresh = isRefresh;
    }

    public void returnTop() {
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    /**
     * @param onClickItemListener
     * @author xd.he
     */
    public void setOnClickItemListener(OnClickNewsItemListener onClickItemListener) {
        this.mOnClickItemListener = onClickItemListener;
    }

}
