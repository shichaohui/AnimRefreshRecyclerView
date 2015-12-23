package com.sch.rfview.example.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sch.rfview.AnimRFRecyclerView;
import com.sch.rfview.example.R;
import com.sch.rfview.example.utils.DimensionConvert;
import com.sch.rfview.manager.AnimRFLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shichaohui on 2015/8/4 0004.
 */
public class LinearFragment extends Fragment {

    private AnimRFRecyclerView mRecyclerView;
    private View headerView;
    private View footerView;
    private List<String> datas;
    private Handler mHandler = new Handler();

    public LinearFragment() {
//        addData();
        datas = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (mRecyclerView == null) {
            // 自定义的RecyclerView, 也可以在布局文件中正常使用
            mRecyclerView = new AnimRFRecyclerView(getActivity());
            // 头部
            headerView = LayoutInflater.from(getActivity()).inflate(R.layout.header_view, null);
            // 脚部
            footerView = LayoutInflater.from(getActivity()).inflate(R.layout.footer_view, null);

            // 使用重写后的线性布局管理器
            mRecyclerView.setLayoutManager(new AnimRFLinearLayoutManager(getActivity()));
//            // 添加头部和脚部，如果不添加就使用默认的头部和脚部
//            mRecyclerView.addHeaderView(headerView);
//            // 设置头部的最大拉伸倍率，默认1.5f，必须写在setHeaderImage()之前
//            mRecyclerView.setScaleRatio(1.7f);
//            // 设置下拉时拉伸的图片，不设置就使用默认的
//            mRecyclerView.setHeaderImage((ImageView) headerView.findViewById(R.id.iv_hander));
//            mRecyclerView.addFootView(footerView);
            // 设置刷新动画的颜色
            mRecyclerView.setColor(Color.RED, Color.BLUE);
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

            // 刷新
            mRecyclerView.setRefresh(true);

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
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isRefresh) {
                        newData();
                        refreshComplate();
                        // 刷新完成后调用，必须在UI线程中
                        mRecyclerView.refreshComplate();
                    } else {
                        addData();
                        loadMoreComplate();
                        // 加载更多完成后调用，必须在UI线程中
                        mRecyclerView.loadMoreComplate();
                    }
                }
            }, 2000);
        }
    }

    public void refreshComplate() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    public void loadMoreComplate() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     * 添加数据
     */
    private void addData() {
        for (int i = 0; i < 13; i++) {
            datas.add("条目  " + (datas.size() + 1));
        }
    }

    public void newData() {
        datas.clear();
        for (int i = 0; i < 13; i++) {
            datas.add("刷新后条目  " + (datas.size() + 1));
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView view = new TextView(getActivity());
            view.setGravity(Gravity.CENTER);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    DimensionConvert.dip2px(getActivity(), 50)));
            MyViewHolder mViewHolder = new MyViewHolder(view);
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.mTextView.setText(datas.get(position));
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
