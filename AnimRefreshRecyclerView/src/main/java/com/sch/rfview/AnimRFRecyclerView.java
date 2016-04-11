package com.sch.rfview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sch.rfview.decoration.DividerGridItemDecoration;
import com.sch.rfview.decoration.DividerItemDecoration;
import com.sch.rfview.listener.OverScrollListener;
import com.sch.rfview.manager.AnimRFGridLayoutManager;
import com.sch.rfview.manager.AnimRFLinearLayoutManager;
import com.sch.rfview.manager.AnimRFStaggeredGridLayoutManager;

import java.util.ArrayList;

/**
 * Created by shichaohui on 2015/8/3 0003.
 * <br/>
 * 可以添加HanderView、FooterView，并且HanderView的背景可以伸缩的RecyclerView
 */
public class AnimRFRecyclerView extends RecyclerView implements Runnable {

    private Context mContext;

    private static AnimRFRecyclerView rfView;

    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFootViews = new ArrayList<>();
    private Adapter mAdapter;

    private int dp1;

    private ImageView headerImage;
    private int headerImageHeight = -1; // 默认高度
    private int headerImageMaxHeight = -1; // 最大高度
    private int headerImageScaleHeight = -1; // 被拉伸的高度
    private float scaleRatio = 1.5f; // 最大拉伸比例
    private float headerImageMinAlpha = 0.5f; // 拉伸到最高时头部的透明度
    private long durationMillis = 300; // 头部恢复动画的执行时间

    private Handler mHandler = new MyHandler();

    private boolean isTouching = false; // 是否正在手指触摸的标识
    private boolean isLoadingData = false; // 是否正在加载数据

    private LoadDataListener mLoadDataListener;

    private AnimView rfAnimView; // 正在刷新状态的View
    private int progressColor = Color.WHITE;
    private int bgColor = Color.WHITE; // 刷新View的颜色

    private boolean isEnable = true;

    private OverScrollListener mOverScrollListener = new OverScrollListener() {
        @Override
        public void overScrollBy(int dy) {
            // dy为拉伸过度时每毫秒拉伸的距离，正数表示向上拉伸多度，负数表示向下拉伸过度
            if (isEnable && !isLoadingData && isTouching
                    && ((dy < 0 && headerImage.getLayoutParams().height < headerImageMaxHeight)
                    || (dy > 0 && headerImage.getLayoutParams().height > headerImageHeight))) {
                mHandler.obtainMessage(0, dy, 0, null).sendToTarget();
                onScrollChanged(0, 0, 0, 0);
            }
        }
    };

    public AnimRFRecyclerView(Context context) {
        this(context, null);
    }

    public AnimRFRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimRFRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        dp1 = AnimView.dip2px(context, 1);
        setOverScrollMode(OVER_SCROLL_NEVER);
        post(this);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            rfView = this;
        }
    }

    /**
     * 添加头部视图，可以添加多个
     *
     * @param view 头部视图
     */
    public void addHeaderView(View view) {
        mHeaderViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof WrapAdapter)) {
                mAdapter = new WrapAdapter(mHeaderViews, mFootViews, mAdapter);
            }
        }
    }

    /**
     * 添加脚部视图，此视图只能添加一个，添加多个时，默认最后添加的一个。
     *
     * @param view 底部视图
     */
    public void addFootView(final View view) {
        mFootViews.clear();
        mFootViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof WrapAdapter)) {
                mAdapter = new WrapAdapter(mHeaderViews, mFootViews, mAdapter);
            }
        }
    }

    /**
     * 设置头部拉伸图片
     *
     * @param headerImage 头部中的背景ImageView
     */
    public void setHeaderImage(ImageView headerImage) {
        this.headerImage = headerImage;
        headerImageHeight = AnimRFRecyclerView.this.headerImage.getHeight();
        // 防止第一次拉伸的时候headerImage.getLayoutParams().height = 0
        if (headerImageHeight <= 0) {
            headerImageHeight = AnimRFRecyclerView.this.headerImage.getLayoutParams().height;
        } else {
            this.headerImage.getLayoutParams().height = headerImageHeight;
        }
        headerImageMaxHeight = (int) (headerImageHeight * scaleRatio);
    }

    /**
     * 设置头部的最大拉伸倍率，默认1.5f，必须写在setHeaderImage()之前
     *
     * @param scaleRatio 头部的最大拉伸倍率，必须大于1，小于1则默认为1.5f
     */
    public void setScaleRatio(float scaleRatio) {
        this.scaleRatio = scaleRatio;
    }

    /**
     * 设置拉伸到最高时头部的透明度，默认0.5f
     *
     * @param headerImageMinAlpha 拉伸到最高时头部的透明度，0.0~1.0
     */
    public void setHeaderImageMinAlpha(float headerImageMinAlpha) {
        this.headerImageMinAlpha = headerImageMinAlpha;
    }

    /**
     * 设置头部恢复动画的执行时间，默认1000毫秒
     *
     * @param durationMillis 头部恢复动画的执行时间，单位：毫秒
     */
    public void setHeaderImageDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    /**
     * 设置刷新相关颜色
     *
     * @param progressColor 进度颜色
     * @param bgColor       背景颜色
     */
    public void setColor(int progressColor, int bgColor) {
        this.progressColor = progressColor;
        this.bgColor = bgColor;
    }

    /**
     * 设置刷新和加载更多数据的监听
     *
     * @param listener {@link com.sch.rfview.AnimRFRecyclerView.LoadDataListener}
     */
    public void setLoadDataListener(LoadDataListener listener) {
        mLoadDataListener = listener;
    }

    /**
     * 加载更多数据完成后调用，必须在UI线程中
     */
    public void loadMoreComplate() {
        isLoadingData = false;
        if (mFootViews.size() > 0) {
            mFootViews.get(0).setVisibility(GONE);
        }
    }

    /**
     * 刷新数据完成后调用，必须在UI线程中
     */
    public void refreshComplate() {
        isLoadingData = false;
        rfAnimView.setVisibility(GONE);
        // 内容不能充满一页时，刷新完自动获取下一页
        smoothScrollBy(0, 1);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mHeaderViews.isEmpty() || headerImage == null && isEnable) {
            // 新建头部
            RelativeLayout headerLayout = new RelativeLayout(mContext);
            headerLayout.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            headerImage = new AnimImageView(mContext);
            ((AnimImageView) headerImage).setColor(progressColor, bgColor);
            headerImage.setMaxHeight(dp1 * 130);
            headerLayout.addView(headerImage, RelativeLayout.LayoutParams.MATCH_PARENT, dp1);
            setScaleRatio(130);
            setHeaderImage(headerImage);

            mHeaderViews.add(0, headerLayout);
        }
        if (mFootViews.isEmpty()) {
            // 新建脚部
            LinearLayout footerLayout = new LinearLayout(mContext);
            footerLayout.setGravity(Gravity.CENTER);
            footerLayout.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mFootViews.add(footerLayout);

            footerLayout.addView(new ProgressBar(mContext, null, android.R.attr.progressBarStyleSmall));

            TextView text = new TextView(mContext);
            text.setText("正在加载...");
            footerLayout.addView(text);
        }
        // 使用包装了头部和脚部的适配器
        adapter = new WrapAdapter(mHeaderViews, mFootViews, adapter);
        super.setAdapter(adapter);
        // 根据是否有头部/脚部视图选择适配器
        // if (mHeaderViews.isEmpty() && mFootViews.isEmpty()) {
        //     super.setAdapter(adapter);
        // } else {
        //     adapter = new WrapAdapter(mHeaderViews, mFootViews, adapter);
        //     super.setAdapter(adapter);
        // }
        mAdapter = adapter;
    }

    @Override
    public void run() {
        LayoutManager manager = getLayoutManager();
        if (manager instanceof AnimRFLinearLayoutManager) {
            // ListView布局
            ((AnimRFLinearLayoutManager) manager).setOverScrollListener(mOverScrollListener);
        } else if (manager instanceof AnimRFGridLayoutManager) {
            layoutGridAttach((AnimRFGridLayoutManager) manager);
        } else if (manager instanceof AnimRFStaggeredGridLayoutManager) {
            layoutStaggeredGridHeadAttach((AnimRFStaggeredGridLayoutManager) manager);
        }
        if (((WrapAdapter) mAdapter).getFootersCount() > 0) {
            // 脚部先隐藏
            mFootViews.get(0).setVisibility(GONE);
        }
    }

    /**
     * 给StaggeredGridLayoutManager附加头部和滑动过度监听
     *
     * @param manager {@link AnimRFStaggeredGridLayoutManager}
     */
    private void layoutStaggeredGridHeadAttach(AnimRFStaggeredGridLayoutManager manager) {
        manager.setOverScrollListener(mOverScrollListener);
        // 从前向后查找Header并设置为充满一行
        View view;
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            if (((WrapAdapter) mAdapter).isHeader(i)) {
                view = getChildAt(i);
                ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams())
                        .setFullSpan(true);
                view.requestLayout();
            } else {
                break;
            }
        }
    }

    /**
     * 给{@link StaggeredGridLayoutManager}附加脚部
     *
     * @param view 底部视图
     */
    private void layoutStaggeredGridFootAttach(View view) {
        // Footer设置为充满一行
        ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams())
                .setFullSpan(true);
        // view.requestLayout();
    }

    /**
     * 给{@link AnimRFGridLayoutManager}附加头部脚部和滑动过度监听
     *
     * @param manager {@link AnimRFGridLayoutManager}
     */
    private void layoutGridAttach(final AnimRFGridLayoutManager manager) {
        // GridView布局
        manager.setOverScrollListener(mOverScrollListener);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return ((WrapAdapter) mAdapter).isHeader(position) ||
                        ((WrapAdapter) mAdapter).isFooter(position) ? manager.getSpanCount() : 1;
            }
        });
        requestLayout();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (headerImage == null) return;
        View view = (View) headerImage.getParent();
        // 上推的时候减小高度至默认高度
        if (view.getTop() < 0 && headerImage.getLayoutParams().height > headerImageHeight) {
            headerImage.getLayoutParams().height += view.getTop();
            mHandler.obtainMessage(0, view.getTop(), 0, view).sendToTarget();
        }

        updateHeaderAlpha();

    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        // 当前不滚动，且不是正在刷新或加载数据
        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadDataListener != null && !isLoadingData) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;
            // 获取最后一个正在显示的Item的位置
            if (layoutManager instanceof AnimRFGridLayoutManager) {
                lastVisibleItemPosition = ((AnimRFGridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof AnimRFStaggeredGridLayoutManager) {
                int[] into = new int[((AnimRFStaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((AnimRFStaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((AnimRFLinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }

            if (layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1) {
                if (mFootViews.size() > 0) {
                    mFootViews.get(0).setVisibility(VISIBLE);
                }
                // 加载更多
                isLoadingData = true;
                mLoadDataListener.onLoadMore();
            }
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                isTouching = false;
                if (headerImage.getLayoutParams().height > headerImageHeight) {
                    if (headerImage.getLayoutParams().height >= headerImageMaxHeight
                            && mLoadDataListener != null && !isLoadingData) {
                        refresh();
                    }
                    headerImageHint();
                    return true;
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 设置是否执行刷新
     *
     * @param isRefrsh
     */
    public void setRefresh(boolean isRefrsh) {
        if (isRefrsh) {
            refresh();
        } else {
            refreshComplate();
        }
    }

    /**
     * 设置是否可刷新
     * @param isEnable
     */
    public void setRefreshEnable(boolean isEnable) {
        this.isEnable = isEnable;
    }

    // 刷新
    private void refresh() {
        isLoadingData = true;
        mLoadDataListener.onRefresh();
        if (rfAnimView == null) {
            // 设置刷新动画
            rfAnimView = new AnimView(mContext);
            rfAnimView.setColor(progressColor, bgColor);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    AnimView.dip2px(mContext, 33), AnimView.dip2px(mContext, 50));
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.setMargins(0, AnimView.dip2px(mContext, 5), 0, 0);
            ((ViewGroup) mHeaderViews.get(0)).addView(rfAnimView, params);
        } else {
            rfAnimView.setVisibility(VISIBLE);
        }
    }

    /**
     * 隐藏{@link #headerImage}
     */
    private void headerImageHint() {
        ValueAnimator animator = ValueAnimator.ofInt(
                headerImage.getLayoutParams().height, headerImageHeight);
        animator.setDuration(durationMillis);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                headerImage.getLayoutParams().height = (int) animation.getAnimatedValue();
                updateHeaderAlpha();
                headerImage.requestLayout();
            }
        });
        animator.start();
    }

    /**
     * 更新头部的透明度
     */
    private void updateHeaderAlpha() {
        // 当前拉伸高度
        int scallHeight = headerImage.getLayoutParams().height - headerImageHeight;
        if (scallHeight > 0) {
            mHandler.obtainMessage(1, scallHeight, 0, null).sendToTarget();
        }
    }

    /**
     * 自定义Handler刷新数据
     */
    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    updateViewSize(msg);
                    break;
                case 1:
                    // 新的透明度(1 - 当前拉伸高度 / 最大拉伸高度 * (1 - 目标透明度))
                    rfView.headerImage.setAlpha(1 - (float) msg.arg1 /
                            (rfView.headerImageMaxHeight - rfView.headerImageHeight) *
                            (1 - rfView.headerImageMinAlpha));
                    break;
            }
        }

        private void updateViewSize(Message msg) {
            // 重新设置View的宽高
            if (msg.obj != null) {
                rfView.headerImage.getLayoutParams().height += msg.arg1;
                View view = ((View) msg.obj);
                view.layout(view.getLeft(), 0, view.getRight(), view.getBottom());
            } else {
                // 实现类似弹簧的阻力效果，拉的越长就越难拉的动
                rfView.headerImageScaleHeight = rfView.headerImage.getLayoutParams().height
                        - rfView.headerImageHeight;
                if (rfView.headerImageScaleHeight < (rfView.headerImageMaxHeight - rfView.headerImageHeight) / 3) {
                    rfView.headerImage.getLayoutParams().height -= msg.arg1;
                } else if (rfView.headerImageScaleHeight > (rfView.headerImageMaxHeight - rfView.headerImageHeight) / 3 * 2) {
                    rfView.headerImage.getLayoutParams().height -= msg.arg1 / 3 * 2;
                } else {
                    rfView.headerImage.getLayoutParams().height -= msg.arg1 / 3 * 1.5;
                }
            }
            rfView.headerImage.requestLayout();
        }
    }

    /**
     * 自定义带有头部/脚部的适配器
     */
    private class WrapAdapter extends RecyclerView.Adapter<ViewHolder> {

        private RecyclerView.Adapter mAdapter;
        private ArrayList<View> mHeaderViews;
        private ArrayList<View> mFootViews;
        final ArrayList<View> EMPTY_INFO_LIST = new ArrayList<>();
        private int headerPosition = 0;

        public WrapAdapter(ArrayList<View> mHeaderViews, ArrayList<View> mFootViews, RecyclerView.Adapter mAdapter) {
            this.mAdapter = mAdapter;
            if (mHeaderViews == null) {
                this.mHeaderViews = EMPTY_INFO_LIST;
            } else {
                this.mHeaderViews = mHeaderViews;
            }
            if (mFootViews == null) {
                this.mFootViews = EMPTY_INFO_LIST;
            } else {
                this.mFootViews = mFootViews;
            }
        }

        /**
         * @param position 位置
         * @return 当前布局是否为Header
         */
        public boolean isHeader(int position) {
            return position >= 0 && position < mHeaderViews.size();
        }

        /**
         * @param position 位置
         * @return 当前布局是否为Footer
         */
        public boolean isFooter(int position) {
            return position < getItemCount() && position >= getItemCount() - mFootViews.size();
        }

        /**
         * @return Header的数量
         */
        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        /**
         * @return Footer的数量
         */
        public int getFootersCount() {
            return mFootViews.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == RecyclerView.INVALID_TYPE) {
                return new HeaderViewHolder(mHeaderViews.get(headerPosition++));
            } else if (viewType == RecyclerView.INVALID_TYPE - 1) {
                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
                        StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT, StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
                params.setFullSpan(true);
                mFootViews.get(0).setLayoutParams(params);
                return new HeaderViewHolder(mFootViews.get(0));
            }
            return mAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return;
            }
            int adjPosition = position - numHeaders;
            int adapterCount;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    mAdapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }

        @Override
        public int getItemCount() {
            if (mAdapter != null) {
                return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
            } else {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return RecyclerView.INVALID_TYPE;
            }
            int adjPosition = position - numHeaders;
            int adapterCount;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemViewType(adjPosition);
                }
            }
            return RecyclerView.INVALID_TYPE - 1;
        }

        @Override
        public long getItemId(int position) {
            int numHeaders = getHeadersCount();
            if (mAdapter != null && position >= numHeaders) {
                int adjPosition = position - numHeaders;
                int adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        private class HeaderViewHolder extends RecyclerView.ViewHolder {
            public HeaderViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    /**
     * 刷新和加载更多数据的监听接口
     */
    public interface LoadDataListener {

        /**
         * 执行刷新
         */
        void onRefresh();

        /**
         * 执行加载更多
         */
        void onLoadMore();

    }

}
