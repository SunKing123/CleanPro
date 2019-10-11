package com.xiaoniu.cleanking.ui.news.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.news.adapter.NewsListAdapter;
import com.xiaoniu.cleanking.ui.main.bean.NewsListInfo;
import com.xiaoniu.cleanking.ui.main.bean.NewsType;
import com.xiaoniu.cleanking.ui.main.bean.VideoItemInfo;
import com.xiaoniu.common.base.BaseFragment;
import com.xiaoniu.common.http.EHttp;
import com.xiaoniu.common.http.callback.ApiCallback;
import com.xiaoniu.common.http.request.HttpRequest;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsListFragment extends BaseFragment {
    private static final String KEY_TYPE = "TYPE";
    private XRecyclerView mRecyclerView;
    private NewsListAdapter mNewsAdapter;
    private LinearLayout mLlNoNet;
    private NewsType mType;

    private static final int PAGE_NUM = 20;//每一页数据
    private boolean mIsRefresh = true;

    public static NewsListFragment getInstance(NewsType type) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_TYPE, type);
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
        mNewsAdapter = new NewsListAdapter(getContext());
        if (arguments != null) {
            mType = (NewsType) arguments.getSerializable(KEY_TYPE);

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
            if (mType == NewsType.VIDEO) {
                loadVideoData();
            } else {
                loadNewsData();
            }
        }
    }

    private void loadNewsData() {
        String type = mType.getValue();
        String lastId = SPUtil.getLastNewsID(mType.getName());
        if (mIsRefresh) {
            lastId = "";
        }
        String url = SpCacheConfig.NEWS_BASEURL + "&type=" + type + "&startkey=" + lastId + "&num=" + PAGE_NUM;
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

    private void loadVideoData() {
        //请求参数设置：比如一个json字符串
        JSONObject jsonObject = new JSONObject();
        try {
            String lastId = SPUtil.getLastNewsID(mType.getName());
            jsonObject.put("pageSize", PAGE_NUM);
            jsonObject.put("lastId", lastId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpRequest request = new HttpRequest.Builder()
                .addBodyParams(jsonObject.toString())
                .build();

        EHttp.post(this, BuildConfig.VIDEO_BASE_URL, request, new ApiCallback<ArrayList<VideoItemInfo>>() {

            @Override
            public void onComplete() {
                if (mIsRefresh) {
                    mRecyclerView.refreshComplete();
                } else {
                    mRecyclerView.loadMoreComplete();
                }
            }

            @Override
            public void onFailure(Throwable e) {
                Log.i("123", e.toString());
            }

            @Override
            public void onSuccess(ArrayList<VideoItemInfo> result) {
                if (result != null && result.size() > 0) {
                    if (mLlNoNet.getVisibility() == View.VISIBLE)
                        mLlNoNet.setVisibility(View.GONE);
                    SPUtil.setLastNewsID(mType.getName(), result.get(result.size() - 1).videoId);
                    if (mIsRefresh) {
                        mNewsAdapter.setData(result);
                    } else {
                        mNewsAdapter.addData(result);
                    }
                }
            }
        });
    }
}
