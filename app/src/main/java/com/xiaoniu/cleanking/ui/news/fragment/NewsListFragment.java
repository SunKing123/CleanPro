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
import com.xiaoniu.cleanking.ui.main.bean.NewsItemInfoRuishi;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.news.adapter.NewsListAdapter;
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
import java.util.List;

/**
 * 单个新闻列表页面
 */
public class NewsListFragment extends BaseFragment {
    private static final String KEY_TYPE = "TYPE";
    private XRecyclerView mRecyclerView;
    private NewsListAdapter mNewsAdapter;
    private LinearLayout mLlNoNet;
    private NewsType mType;

    private static final int PAGE_VIDEO_NUM = 20;//每一页数据_视频类型用到的

    /*分页，或下拉刷新次数。用户上滑，可依次传入1,2,3等。
    用户下拉刷新，可传入page=-1。第二次下拉刷新，可传入page=-2。以此类推。
    注意，不要传入page=0。*/
    private int page_index = 1;
    private int page_ref_index = -1;
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
        String type = mType.getName();
        String url = SpCacheConfig.RUISHI_BASEURL + "bd/news/list?&category=" + type  + "&page=" + (mIsRefresh ? page_ref_index : page_index);
        EHttp.get(this, url, new ApiCallback<List<NewsItemInfoRuishi>>(null) {
            @Override
            public void onFailure(Throwable e) {
            }

            @Override
            public void onSuccess(List<NewsItemInfoRuishi> result) {
                if (result != null  && result.size() > 0) {
                    if (mLlNoNet.getVisibility() == View.VISIBLE)
                        mLlNoNet.setVisibility(View.GONE);
                    if (mIsRefresh) {
                        page_ref_index--;
                        mNewsAdapter.setData(result);
                    } else {
                        page_index ++;
                        mNewsAdapter.addData(result);
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

    //加载视频信息
    private void loadVideoData() {
        //请求参数设置：比如一个json字符串
        JSONObject jsonObject = new JSONObject();
        try {
            String lastId = SPUtil.getLastNewsID(mType.getName());
            jsonObject.put("pageSize", PAGE_VIDEO_NUM);
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
