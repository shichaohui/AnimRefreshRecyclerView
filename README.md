# AnimRefreshRecyclerViewDemo
下拉刷新和上拉加载更多的RecyclerView，具有下拉和刷新动画。
效果：

嗯...看起来有点卡，截图软件的问题：

![下拉刷新RecyclerView](http://img.blog.csdn.net/20150807174055895)

图中普通列表是ListView样式，没有设置Header和Footer时使用默认的下拉刷新和上拉加载。而宫格列表和瀑布流列表使用的是自定义Header和Footer的下拉刷新和上拉上拉加载。

**用法：**
Gradle:
```xml
dependencies {
    compile 'com.sch.rfview:AnimRefreshRecyclerView:1.0.0'
}
```
Eclipse的同学们可以自己下载源码拷贝java文件到自己的工程（别忘了引用RecyclerView的包哦）。

代码中的配置参考下面的用法代码片段，除了RecyclerView自带的方法，其他方法都是可选的。

项目地址：https://github.com/shichaohui/AnimRefreshRecyclerViewDemo
项目中包含一个demo（普通Android工程）和Android Library，感兴趣的同学可以自己下载源码和Demo。

**用法代码片段：**
```java
// 自定义的RecyclerView, 也可以在布局文件中正常使用
mRecyclerView = new AnimRFRecyclerView(getActivity());
// 头部
headerView = LayoutInflater.from(getActivity()).inflate(R.layout.header_view, null);
// 脚部
footerView = LayoutInflater.from(getActivity()).inflate(R.layout.footer_view, null);

// 根据列表的不同效果选择不同的布局管理器
// 使用重写后的线性布局管理器
mRecyclerView.setLayoutManager(new AnimRFLinearLayoutManager(getActivity()));
// 使用重写后的格子布局管理器
// mRecyclerView.setLayoutManager(new AnimRFGridLayoutManager(getActivity(), 2));
// 使用重写后的瀑布流布局管理器
// mRecyclerView.setLayoutManager(new AnimRFStaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

// 添加头部和脚部，如果不添加就使用默认的头部和脚部，addHeaderView()和setHeaderImage()必须同时使用
// mRecyclerView.addHeaderView(headerView);
// 设置头部的最大拉伸倍率，默认1.5f，必须写在setHeaderImage()之前
// mRecyclerView.setScaleRatio(2.0f);
// mRecyclerView.setHeaderImage((ImageView) headerView.findViewById(R.id.iv_hander));
// mRecyclerView.addFootView(footerView);

// 设置刷新动画的颜色
mRecyclerView.setColor(Color.RED, Color.WHITE);
// 设置头部恢复动画的执行时间，默认1000毫秒
mRecyclerView.setHeaderImageDurationMillis(1200);
// 设置拉伸到最高时头部的透明度，默认0.5f
mRecyclerView.setHeaderImageMinAlpha(0.6f);

// 设置适配器
mRecyclerView.setAdapter(new MyAdapter());

// 设置刷新和加载更多数据的监听，分别在onRefresh()和onLoadMore()方法中执行刷新和加载更多操作
mRecyclerView.setLoadDataListener(new AnimRFRecyclerView.LoadDataListener() {
    @Override
    public void onRefresh() {
	    // 开启线程刷新数据
        new Thread(new MyRunnable()).start();
    }

    @Override
    public void onLoadMore() {
	    // 开启线加载更多数据
        new Thread(new MyRunnable()).start();
    }
});
```
在刷新和加载过更多完成之后调用代码停止动画：
```java
// 刷新完成后调用，必须在UI线程中
mRecyclerView.refreshComplate();

// 加载更多完成后调用，必须在UI线程中
mRecyclerView.loadMoreComplate();
```
