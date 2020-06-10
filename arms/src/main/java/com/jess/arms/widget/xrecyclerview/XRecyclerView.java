package com.jess.arms.widget.xrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoniu.cleaning.arms.R;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.List;

/*添加HeaderView,FooterView,自动加载更多，优化Adapter代码，自定义ViewHolder*/
public class XRecyclerView extends RecyclerView {
    /*第一：headerview和footerview相关*/
    private WrapAdapter mWrapAdapter;
    private AdapterDataObserver mDataObserver;
    private View mHeaderView;
    private View mFooterView;

    /*第二：设置点击，长按事件*/
    private OnItemClickLitener mOnItemClickLitener;

    /*第三：自动加载更多相关*/
    private OnLoadListener mLoadListener;// 下拉刷新和加载更多的监听器
    private View mAutoLoadingLayout;//用于滑到底部自动加载的Footer
    private boolean hasMoreData = true;
    private ProgressBar mProgressBar;
    private TextView mHintView;
    private State mCurState = State.NORMAL;//当前的状态
    private State mPreState = State.NORMAL;//前一个状态
    private boolean loadingMoreEnabled;
    private GridLayoutManager.SpanSizeLookup mSpanSizeLookup;

    public XRecyclerView(Context context) {
        this(context, null);
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);// 调用基类构造方法，初始化一些配置方法
        mDataObserver = new DataObserver();
        setLayoutManager(new LinearLayoutManager(context));//默认值
        //设置Item增加、移除动画
        setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        addItemDecoration(new DividerItemDecoration(
//                context, DividerItemDecoration.VERTICAL_LIST));
    }

    /*自动加载更多相关*/
    public void setAutoLoadingEnabled(boolean enabled) {
        loadingMoreEnabled = enabled;
        if (enabled) {
            if (mAutoLoadingLayout == null) {
                mAutoLoadingLayout = LayoutInflater.from(getContext()).inflate(
                        R.layout.common_layout_load_more_footer, null);
                LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                mAutoLoadingLayout.setLayoutParams(params);
                mProgressBar = (ProgressBar) mAutoLoadingLayout
                        .findViewById(R.id.loadProgress);
                mHintView = (TextView) mAutoLoadingLayout.findViewById(R.id.loadHint);
                addOnScrollListener(new OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (isLastItemVisible() && loadingMoreEnabled) {
                            if (hasMoreData && mCurState != State.LOADING
                                    && mCurState != State.FAILED
                                    && mCurState != State.UNAVAILABLE) {
                                startLoading();// 开始加载
                            }
                        }

                    }

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView,
                                                     int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }
                });
            }
            onStateChanged(State.NORMAL);// 首次进入一个界面是刷新，加载更多设置为不可用，需要隐藏footer
            setFooterView(mAutoLoadingLayout);
//            mFooterView = mAutoLoadingLayout;
//            requestLayout();
        } else {
            removeFooterView();
        }
    }

    /*以下是HeaderView和FooterView的逻辑*/
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        requestLayout();
    }

    public void removeHeaderView() {
        mHeaderView = null;
        requestLayout();
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        requestLayout();
    }

    public void removeFooterView() {
        mFooterView = null;
        requestLayout();
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public interface OnItemClickLitener {
        void onItemClick(View itemView, int position);

        boolean onItemLongClick(View itemView, int position);
    }

    //mWrapAdapter才是正真的Adapter
    @Override
    public void setAdapter(Adapter adapter) {
        mWrapAdapter = new WrapAdapter(adapter);
        super.setAdapter(mWrapAdapter);
        adapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
    }

    private class DataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            mWrapAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    }

    public void setSpanSizeLookup(GridLayoutManager.SpanSizeLookup spanSizeLookup) {
        mSpanSizeLookup = spanSizeLookup;
    }

    private class WrapAdapter extends Adapter<ViewHolder> {
        private final static int TYPE_HEADER = -1;// 头部--支持头部增加1个headerView
        private final static int TYPE_FOOTER = -2;// 底部--支持底部增加1个footerView
        private Adapter mOutAdapter;

        public WrapAdapter(Adapter adapter) {
            this.mOutAdapter = adapter;
        }

        public Adapter getOriginalAdapter() {
            return this.mOutAdapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                return new CommonViewHolder(mHeaderView) {
                };
            }
            if (viewType == TYPE_FOOTER) {
                return new CommonViewHolder(mFooterView) {
                };
            }
            ViewHolder viewHolder = mOutAdapter.onCreateViewHolder(parent, viewType);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            onBindViewHolder(holder, position, null);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position, List<Object> payloads) {
            int type = getItemViewType(position);
            if (type == TYPE_FOOTER || type == TYPE_HEADER) {
                return;
            }

            final int realPosition = getRealPosition(position);
            if (payloads.isEmpty()) {
                mOutAdapter.onBindViewHolder(holder, realPosition);
            } else {
                mOutAdapter.onBindViewHolder(holder, realPosition, payloads);
            }

            // 如果设置了回调，则设置点击事�?
            if (mOnItemClickLitener != null) {
                View itemView;
                if (holder.itemView instanceof SlidingItemView) {
                    itemView = ((SlidingItemView) holder.itemView).getContentView();
                } else {
                    itemView = holder.itemView;
                }

                itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickLitener.onItemClick(holder.itemView, realPosition);
                    }
                });

                itemView.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return mOnItemClickLitener.onItemLongClick(holder.itemView,
                                realPosition);
                    }
                });
            }
        }

        public int getRealPosition(int position) {
            return mHeaderView == null ? position : position - 1;
        }

        @Override
        public int getItemCount() {
            int size = mOutAdapter.getItemCount();
            if (mFooterView != null) {
                size++;
            }
            if (mHeaderView != null) {
                size++;
            }
            return size;
        }

        @Override
        public int getItemViewType(int position) {
            int startPosition = 0;
            int endPosition = getItemCount() - 1;
            if (position == startPosition && mHeaderView != null) {
                return TYPE_HEADER;
            } else if (position == endPosition && mFooterView != null) {
                return TYPE_FOOTER;
            }
            return mOutAdapter.getItemViewType(getRealPosition(position));//重写该方法分组扩
        }

        @Override
        public long getItemId(int position) {
            return mOutAdapter.getItemId(getRealPosition(position));
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            mOutAdapter.onAttachedToRecyclerView(recyclerView);
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int type = getItemViewType(position);
                        if (type == TYPE_HEADER || type == TYPE_FOOTER) {
                            return gridManager.getSpanCount();
                        }
                        if (mSpanSizeLookup != null) {
                            return mSpanSizeLookup.getSpanSize(position);
                        }
                        return 1;
                    }
                });
            }

        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            mOutAdapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) lp;
                int type = getItemViewType(holder.getLayoutPosition());
                if (type == TYPE_HEADER || type == TYPE_FOOTER) {
                    params.setFullSpan(true);
                    return;
                }

            }
            mOutAdapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            mOutAdapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            mOutAdapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            return mOutAdapter.onFailedToRecycleView(holder);
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            mOutAdapter.unregisterAdapterDataObserver(observer);
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mOutAdapter.registerAdapterDataObserver(observer);
        }

    }

    /*第四：以下是上拉自动加载更多的逻辑*/
    protected void startLoading() {
        if (null != mAutoLoadingLayout && mLoadListener != null) {
            onStateChanged(State.LOADING);// 设置底部状态，正在加载
            mLoadListener.onLoad(this);
        }
    }

    // 加载成功后，调用改变footer状态
    public void onLoadSucess(boolean hasMoreData) {
        if (mAutoLoadingLayout == null) {
            return;
        }
        if (hasMoreData) {
            onStateChanged(State.NORMAL);// 设置底部状态，完成后隐藏
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (isLastItemVisible()) {
                        if (mCurState != State.LOADING
                                && mCurState != State.FAILED
                                && mCurState != State.UNAVAILABLE) {
                            startLoading();// 开始加载
                        }
                    }
                }
            }, 0);

        } else {
            onStateChanged(State.NO_MORE_DATA);// 设置底部状态，完成后隐藏
        }
    }

    // 加载失败后，调用改变footer状态
    public void onLoadFailed() {
        this.hasMoreData = true;
        onStateChanged(State.FAILED);// 设置底部状态，完成后隐藏
    }

    // 没有数据时，隐藏footer，并设置为不可用
    public void onLoadUnavailable() {
        this.hasMoreData = true;
        onStateChanged(State.UNAVAILABLE);// 设置底部状态，完成后隐藏
    }

    // 设置刷新的监听器
    public void setOnLoadListener(OnLoadListener loadListener) {
        mLoadListener = loadListener;
    }

    /**
     * 判断最后一个child是否完全显示出来 判断listview是否滑动到底部
     *
     * @return true完全显示出来，否则false
     */
    private boolean isLastItemVisible() {
        LayoutManager layoutManager = getLayoutManager();
        int lastVisibleItemPosition = getLastVisibleItemPosition();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        if (totalItemCount == 0) {// 這裡主要判断数据还没有加载进来时，不可以上拉，只能下拉刷新
            return false;
        }
//        Log.i("123", lastVisibleItemPosition+"-"+visibleItemCount+"-"+totalItemCount);
        if (lastVisibleItemPosition == totalItemCount - 1) {// 当最后一项可见时
            int index = visibleItemCount - 1;
            View lastVisibleChild = this.getChildAt(index);
//			Log.i("123", lastVisibleChild.getBottom()+"-"+this.getBottom());
            if (lastVisibleChild != null) {
                return lastVisibleChild.getBottom() <= this.getBottom();// 仅试验有可能小于一个像素值，就是下线条所占据的。每个item都有一个下线条，没有上线条
            }
        }
        return false;
    }

    private int getLastVisibleItemPosition() {
        LayoutManager layoutManager = getLayoutManager();
        int lastVisibleItemPosition = -1;
        if (layoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                    .findLastVisibleItemPosition();

        } else if (layoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                    .findLastVisibleItemPosition();

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastScrollPositions = new int[staggeredGridLayoutManager
                    .getSpanCount()];
            staggeredGridLayoutManager
                    .findLastVisibleItemPositions(lastScrollPositions);
            int max = Integer.MIN_VALUE;
            for (int value : lastScrollPositions) {
                if (value > max)
                    max = value;
            }
            lastVisibleItemPosition = max;
        }

        return lastVisibleItemPosition;
    }

    /**
     * 当状态改变时调用
     * <p>
     * 当前状态 老的状态
     */
    protected void onStateChanged(State newState) {
        mAutoLoadingLayout.setVisibility(View.VISIBLE);
        mAutoLoadingLayout.setOnClickListener(null);
        mCurState = newState;
        hasMoreData = true;
        switch (newState) {
            case NORMAL:
                mProgressBar.setVisibility(View.GONE);
                mHintView.setText("上拉可以加载更多");
                break;
            case LOADING:
                mProgressBar.setVisibility(View.VISIBLE);
                mHintView.setText("正在加载更多...");
                break;
            case NO_MORE_DATA:
                hasMoreData = false;
                mProgressBar.setVisibility(View.GONE);
                mHintView.setText("没有更多了");
                break;
            case UNAVAILABLE:
                mAutoLoadingLayout.setVisibility(View.INVISIBLE);
                break;
            case FAILED:
                mProgressBar.setVisibility(View.GONE);
                mHintView.setText("加载更多失败，点击重新加载");
                mAutoLoadingLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startLoading();
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 当前的状态
     */
    public enum State {

        /**
         * No more data
         */
        NO_MORE_DATA,

        /**
         * 正常状态，或者初始化的状态
         */
        NORMAL,

        /**
         * 加载中
         */
        LOADING,
        /**
         * 失败后的状态
         */
        FAILED,
        /**
         * 不可用的状态
         */
        UNAVAILABLE
    }

    public interface OnLoadListener {

        /**
         * 加载更多时会被调用或上拉时调用，子类实现具体的业务逻
         */
        void onLoad(XRecyclerView recyclerView);
    }
}
