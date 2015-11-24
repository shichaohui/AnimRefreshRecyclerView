package com.sch.rfview.example.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sch.rfview.AnimRFRecyclerView;
import com.sch.rfview.manager.AnimRFStaggeredGridLayoutManager;
import com.sch.rfview.example.R;
import com.sch.rfview.example.utils.DimensionConvert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shichaohui on 2015/8/4 0004.
 */
public class StaggeredGridFragment extends Fragment {

    private AnimRFRecyclerView mRecyclerView;
    private View headerView;
    private View footerView;
    private List<String> datas;
    private Handler mHandler = new Handler();
    private List<Integer> heights;
    private Context mContext;

    public StaggeredGridFragment() {
//        addData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mRecyclerView == null) {
            addData();
            mContext = getActivity();

            mRecyclerView = new AnimRFRecyclerView(mContext);
            headerView = LayoutInflater.from(mContext).inflate(R.layout.header_view, null);
            footerView = LayoutInflater.from(mContext).inflate(R.layout.footer_view, null);

            // 使用重写后的瀑布流布局管理器
            mRecyclerView.setLayoutManager(new AnimRFStaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
            // 添加头部和脚部，如果不添加就使用默认的头部和脚部
            mRecyclerView.addHeaderView(headerView);
            // 设置头部的最大拉伸倍率，默认1.5f，必须写在setHeaderImage()之前
            // mRecyclerView.setScaleRatio(1.7f);
            // // 设置下拉时拉伸的图片，不设置就使用默认的
            // mRecyclerView.setHeaderImage((ImageView) headerView.findViewById(R.id.iv_hander));
            mRecyclerView.addFootView(footerView);
            // 设置刷新动画的颜色
            mRecyclerView.setColor(Color.GREEN, Color.RED);
            // 设置头部恢复动画的执行时间，默认500毫秒
            mRecyclerView.setHeaderImageDurationMillis(300);
            // 设置拉伸到最高时头部的透明度，默认0.5f
            mRecyclerView.setHeaderImageMinAlpha(0.6f);
            // 设置适配器
            mRecyclerView.setAdapter(new MyAdapter());
            // 设置刷新和加载更多数据的监听，分别在onRefresh()和onLoadMore()方法中执行刷新和加载更多操作
            mRecyclerView.setLoadDataListener(new AnimRFRecyclerView.LoadDataListener() {
                @Override
                public void onRefresh() {
                    new Thread(new MyRunnable(true)).start();
                }

                @Override
                public void onLoadMore() {
                    new Thread(new MyRunnable(false)).start();
                }
            });

        }

        return mRecyclerView;
    }

    class MyRunnable implements Runnable {

        boolean isRefresh;

        public MyRunnable(boolean isRefresh) {
            this.isRefresh = isRefresh;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (isRefresh) {
                        newData();
                        refreshComplate();
                        mRecyclerView.refreshComplate();
                    } else {
                        addData();
                        loadMoreComplate();
                        mRecyclerView.loadMoreComplate();
                    }
                }
            });
        }
    }

    public void refreshComplate() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void loadMoreComplate() {
        /*
         * 用notifyDataSetChanged()的话，加载完重新滚动到顶部的时候会产生错位并自动调整布局，
         * 所以用requestLayout()刷新布局
         */
        // mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.requestLayout();
    }

    /**
     * 添加数据
     */
    private void addData() {
        if (datas == null) {
            datas = new ArrayList<>();
            heights = new ArrayList<>();
        }
        for (int i = 0; i < 20; i++) {
            datas.add("条目  " + (datas.size() + 1));
            calHeight();
        }
    }

    public void newData() {
        datas.clear();
        heights.clear();
        for (int i = 0; i < 20; i++) {
            datas.add("刷新后条目  " + (datas.size() + 1));
            calHeight();
        }
    }

    public void calHeight() {
        double d = Math.random();
        if (d < 0.25) {
            heights.add(DimensionConvert.dip2px(mContext == null ? getActivity() : mContext, 30));
        } else if (d < 0.5) {
            heights.add(DimensionConvert.dip2px(mContext == null ? getActivity() : mContext, 50));
        } else if (d < 0.75) {
            heights.add(DimensionConvert.dip2px(mContext == null ? getActivity() : mContext, 70));
        } else {
            heights.add(DimensionConvert.dip2px(mContext == null ? getActivity() : mContext, 100));
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView view = new TextView(getActivity());
            view.setGravity(Gravity.CENTER);
            view.setBackgroundColor(Color.argb(125, 255, 0, 0));
            MyViewHolder mViewHolder = new MyViewHolder(view);
            parent.addView(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.mTextView.setText(datas.get(position));
            holder.mTextView.getLayoutParams().height = heights.get(position);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }
    }

}
