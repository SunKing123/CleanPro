package com.xiaoniu.common.widget.xrecyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

/*支持多布局类型，侧滑菜单*/
public abstract class CommonRecyclerAdapter<Model> extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int mLayoutId;
    protected Context mContext;
    protected List<Model> mDatas = new ArrayList<>();
    protected LayoutInflater mInflater;
    private MultiItemTypeSupport<Model> mMultiTypeSupport;
    private SlidingMenuViewCreator mSlidingMenuCreator;

    /*单布局样式*/
    public CommonRecyclerAdapter(Context context, List<Model> datas, int layoutId) {
        this.mContext = context;
        if (datas != null) {
            this.mDatas = datas;
        }
        this.mInflater = LayoutInflater.from(mContext);
        this.mLayoutId = layoutId;
    }

    /**
     * 多布局支持
     */
    public CommonRecyclerAdapter(Context context, List<Model> datas, MultiItemTypeSupport<Model> multiTypeSupport) {
        this(context, datas, -1);
        this.mMultiTypeSupport = multiTypeSupport;
    }

    /*单布局样式*/
    public CommonRecyclerAdapter(Context context, int layoutId) {
        this(context, null, layoutId);
    }

    /**
     * 多布局支持
     */
    public CommonRecyclerAdapter(Context context, MultiItemTypeSupport<Model> multiTypeSupport) {
        this(context, null, -1);
        this.mMultiTypeSupport = multiTypeSupport;
    }

    /*侧滑菜单*/
    public void setSlidingMenuCreator(SlidingMenuViewCreator slidingMenuCreator) {
        this.mSlidingMenuCreator = slidingMenuCreator;
    }

    public Model getItemData(int position) {
        return mDatas.get(position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mMultiTypeSupport != null) {//重写该方法分组扩展
            return mMultiTypeSupport.getItemViewType(position, getItemData(position));
        }
        return 0;
    }

    /*
     * 也可以重写该方法，根据不同的类型，来自定义不同的Holder，这里统�?�?以的类型，都是同�?种Holder（每个item都有�?个自己的Holder对象
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View itemView;
        /*多布局支持*/
        if (mMultiTypeSupport != null) {
            itemView = mInflater.inflate(mMultiTypeSupport.getLayoutId(viewType), parent, false);
        } else {
            itemView = mInflater.inflate(mLayoutId, parent, false);
        }
        /*侧滑菜单*/
        if (mSlidingMenuCreator != null) {
            View menuView = mSlidingMenuCreator.onCreateItemMenuView(mContext, viewType);
            if (menuView != null) {
                SlidingItemView slidingItemView = new SlidingItemView(mContext);
                slidingItemView.addContentView(itemView);
                slidingItemView.addMenuView(menuView);
                itemView = slidingItemView;
            }
        }
        holder = attachToViewHolder(viewType, itemView);
        if (holder == null) {
            holder = new CommonViewHolder(itemView);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.itemView instanceof SlidingItemView) {
            SlidingItemView itemView = (SlidingItemView) holder.itemView;
            if (mSlidingMenuCreator != null) {
                mSlidingMenuCreator.onBindItemMenuView(itemView.getMenuView(), getItemData(position), position);
            }
        }
        convert(holder, getItemData(position), position);
    }

    public abstract void convert(RecyclerView.ViewHolder holder, Model itemData, int position);

    public abstract RecyclerView.ViewHolder attachToViewHolder(int viewType, View itemView);

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder
                    .itemView.getLayoutParams();
            if (mMultiTypeSupport != null) {
                params.setFullSpan(mMultiTypeSupport.isFullSpan(position));
            } else {
                params.setFullSpan(false);
            }
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView instanceof XRecyclerView) {
            XRecyclerView xRecyclerView = (XRecyclerView) recyclerView;
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            final int spanCount;
            if (manager instanceof GridLayoutManager) {
                GridLayoutManager gridManager = ((GridLayoutManager) manager);
                spanCount = gridManager.getSpanCount();
            } else {
                spanCount = 0;
            }
            xRecyclerView.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (mMultiTypeSupport != null) {
                        int spanSize = mMultiTypeSupport.getSpanSize(position, spanCount);
                        return spanSize;
                    }
                    return 1;
                }
            });
        }
    }

    public interface MultiItemTypeSupport<Model> {

        int getItemViewType(int position, Model itemData);

        int getLayoutId(int viewType);

        boolean isFullSpan(int position);

        int getSpanSize(int position, int spanCount);
    }

    public interface SlidingMenuViewCreator<Model> {
        /*创建侧滑菜单View*/
        View onCreateItemMenuView(Context context, int viewType);

        /*可以给不同position的菜单项添加不同的数据，或者加点击事件等*/
        void onBindItemMenuView(View menuView, Model itemData, int position);
    }

    public void setData(List<? extends Model> datas) {
        if (datas != null && datas.size() > 0) {
            mDatas.clear();
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void addData(List<? extends Model> datas) {
        if (datas != null && datas.size() > 0) {
            mDatas.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public List<Model> getData() {
        return mDatas;
    }

    /**
     * 插入1个数
     */
    public void insert(int position, Model model) {
        if (position < 0) {
            position = 0;
        }

        if (position > mDatas.size()) {
            position = mDatas.size() - 1;
        }
        mDatas.add(position, model);
        notifyItemInserted(position);
    }

    /**
     * 加入1个数据
     */
    public void append(Model model) {
        mDatas.add(model);
        notifyItemInserted(mDatas.size() - 1);
    }

    /**
     * 移除1个数
     *
     * @param position
     */
    public void remove(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 清除所有的数据
     */
    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }


}
