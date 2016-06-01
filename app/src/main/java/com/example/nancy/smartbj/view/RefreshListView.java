package com.example.nancy.smartbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nancy.smartbj.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 带有下拉刷新和上拉加载更多的listView
 * Created by Nancy on 2016/5/31.
 */
public class RefreshListView extends ListView {

    private LinearLayout headerContainer;  //头部容器
    private LinearLayout ll_refresh_header_root; //下拉刷新的头部,放在容器中
    private TextView tv_state;
    private TextView tv_time;
    private ImageView iv_arrow;
    private ProgressBar pb_loading;
    private int ll_refresh_header_root_height;
    private View footer;
    private int ll_refresh_footer_height;
    private RotateAnimation ra_up;
    private RotateAnimation ra_down;
    private boolean isLoadingMore = false;

    private final int PULL_DOWN = 1;//显示下拉刷新
    private final int RELEASE_TO_REFRESH = 2;//显示松开刷新
    private final int REFRESHING = 3;//显示正在刷新
    private int currentState = PULL_DOWN;
    private int listViewOnScreenY;
    private View lunbotu;
    private float downY;
    private onRefreshDataListener listener;

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
        initAnimation();
        initEvent();
    }

    private void initEvent() {
        //添加当前ListView的滑动事件处理

        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //状态停止，如果ListView显示最后一条数据，并且当前没有正在加载，就加载更多

                if (getLastVisiblePosition() == getAdapter().getCount() - 1 && !isLoadingMore) {
                    isLoadingMore = true;

                    //显示加载更多
                    footer.setPadding(0, 0, 0, 0);
                    setSelection(getAdapter().getCount() - 1);

                    //加载更多数据
                    if (listener != null) {
                        listener.loadingMore();
                    }


                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }


    private void initAnimation() {
        ra_up = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra_up.setDuration(500);
        ra_up.setFillAfter(true);//停留在动画结束的状态

        ra_down = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra_down.setDuration(500);
        ra_down.setFillAfter(true);//停留在动画结束的状态

    }

    private void initView() {
        //初始化头部
        initHeader();
        //初始化底部
        iniitFooter();

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        //需要我们的功能，则屏蔽掉父类的touch时间
        //下拉拖动（当listView显示第一条数据）
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:

                //是否正在刷新数据
                if (currentState == REFRESHING) {
                    break;
                } else if (!isLunboFullShow()) {
                    //轮播图没有完全显示
                    break;
                } else {

                    //todo 点击图片下拉不会响应actionDown事件?
                    if (downY == 0) {
                        downY = ev.getY();
                    }
                    float moveY = ev.getY();

                    //移动的间距
                    float dy = moveY - downY;

                    System.out.println("moveY:" + moveY + ",downY:" + downY);

                    //下拉拖动 不让listView拦截事件,
                    if (dy > 0 && getFirstVisiblePosition() == 0) {
                        //当前paddingTop的参数值

                        float scrollY = -ll_refresh_header_root_height + dy;
                        System.out.println("scrollY:" + scrollY);
                        if (scrollY < 0 && currentState != PULL_DOWN) {
                            //如果刷新头没有完全显示，且不是下拉刷新（为了只执行一次）
                            currentState = PULL_DOWN;
                            refreshState();
                        } else if (scrollY >= 0 && currentState != RELEASE_TO_REFRESH) {
                            //如果刷新头完全显示了，并且不是松开刷新状态（为了只执行一次）
                            currentState = RELEASE_TO_REFRESH;
                            refreshState();
                        }

                        ll_refresh_header_root.setPadding(0, (int) scrollY, 0, 0);

                        return true;
                    }

                }

                break;
            case MotionEvent.ACTION_UP:
                //判断状态
                //如果是PULL_DOWN，则总开回复原状
                if (currentState == PULL_DOWN) {
                    ll_refresh_header_root.setPadding(0, -ll_refresh_header_root_height, 0, 0);

                } else if (currentState == RELEASE_TO_REFRESH) {
                    //如果是松开刷新状态，则改为刷新数据
                    ll_refresh_header_root.setPadding(0, 0, 0, 0);
                    currentState = REFRESHING;//改变状态为正在刷新状态
                    refreshState();//刷新界面

                    //调用回调更新数据
                    if (listener != null) {
                        listener.refreshData();
                    }
                }
                break;
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 根据状态设置文字和动画
     */
    private void refreshState() {
        switch (currentState) {
            case PULL_DOWN:
                tv_state.setText("下拉刷新");
                iv_arrow.startAnimation(ra_down);
                break;
            case RELEASE_TO_REFRESH:
                tv_state.setText("松开刷新");
                iv_arrow.startAnimation(ra_up);
                break;
            case REFRESHING:
                iv_arrow.clearAnimation();//清除动画
                iv_arrow.setVisibility(GONE);//隐藏箭头
                pb_loading.setVisibility(VISIBLE);//显示进度条
                tv_state.setText("正在刷新数据");
                break;
        }
    }

    /**
     * @return 判断轮播图是否完全显示
     */
    private boolean isLunboFullShow() {

        int[] location = new int[2];
        //如果轮播图没有完全显示，相应的是ListView的事件
        //判断轮播图是否完全显示
        //取listView在屏幕中的坐标和轮播图的坐标
        if (listViewOnScreenY == 0) {//如果还没获取过
            //获取listView在屏幕中的坐标
            this.getLocationOnScreen(location);
            listViewOnScreenY = location[1];
        }

        //判断轮播图在屏幕中的坐标
        lunbotu.getLocationOnScreen(location);
        //判断

//        System.out.println("轮播图;"+location[1]+"，listView："+listViewOnScreenY);
        //todo 轮播图的y坐标包括隐藏的部分，是最上面的y坐标。而listView的Y坐标始终是页签的正下方那个y坐标
        if (location[1] < listViewOnScreenY) {
            //轮播图没有完全显示
            //继续响应listView的事件
            return false;
        } else {
            return true;
        }


    }

    private void iniitFooter() {
        //listView的尾部
        footer = View.inflate(getContext(), R.layout.listview_refresh_footer, null);
        //测量尾部的高度
        footer.measure(0, 0);

        //listView尾部组件的高度
        ll_refresh_footer_height = footer.getMeasuredHeight();

        footer.setPadding(0, -ll_refresh_footer_height, 0, 0);
        //加载ListView到中
        addFooterView(footer);
    }

    private void initHeader() {
        headerContainer = (LinearLayout) View.inflate(getContext(), R.layout.listview_header_container, null);
        //listview 下拉刷新的根布局
        ll_refresh_header_root = ((LinearLayout) headerContainer.findViewById(R.id.ll_listview_header_root));

        //获取刷新头布局的子组件
        //刷新状态的文件描述
        tv_state = ((TextView) headerContainer.findViewById(R.id.tv_listview_header_state_describe));
        //最新的刷新时间
        tv_time = (TextView) headerContainer.findViewById(R.id.tv_listview_header_refresh_time);
        //下拉刷新的箭头
        iv_arrow = (ImageView) headerContainer.findViewById(R.id.iv_listview_header_arrow);
        //下拉刷新的进度
        pb_loading = (ProgressBar) headerContainer.findViewById(R.id.pb_listview_header_loading);

        //隐藏刷新头的跟布局，轮播图还要显示!

        //todo 没有下面这句，就无法获得高度（当然也有其他办法可以获得，参考印象笔记）
        ll_refresh_header_root.measure(0, 0);
        //获取测量的高度
        ll_refresh_header_root_height = ll_refresh_header_root.getMeasuredHeight();

        //todo 隐藏一个view的一个简单办法，设置他的padding为负的自己的高度
        ll_refresh_header_root.setPadding(0, -ll_refresh_header_root_height, 0, 0);

// 也可以        headerContainer.setPadding(0, -ll_refresh_header_root_height, 0, 0);

        //todo 先加入的头部在后加入头部的上面，而不是下面!
        super.addHeaderView(headerContainer);

    }

    /**
     * @param v 只能添加轮播图，为了让这个类能够得到轮播图的实例
     */
    @Override
    public void addHeaderView(View v) {
        lunbotu = v;
        headerContainer.addView(v);
    }

    /**
     * refreshData 是下拉刷新的时候需要做的事情
     * loadingMore 是滑动到最后一条时候需要做的事情
     */
    public interface onRefreshDataListener {
        void refreshData();

        void loadingMore();
    }

    public void setOnRefreshDataListener(onRefreshDataListener listener) {
        this.listener = listener;
    }

    public void refreshStateFinish() {
        //下拉刷新
        if (isLoadingMore) {
            //加载更多数据
            isLoadingMore = true;
            //隐藏加载更多数据的组件
            footer.setPadding(0, -ll_refresh_footer_height, 0, 0);
        } else {
            //改变下拉刷新
            tv_state.setText("下拉刷新");
            iv_arrow.setVisibility(VISIBLE);//显示箭头
            pb_loading.setVisibility(INVISIBLE);//隐藏进度条
            //设置刷新时间为当前时间
            tv_time.setText(getCurrentFormatDate());
            //隐藏刷新的头布局
            ll_refresh_header_root.setPadding(0, -ll_refresh_header_root_height, 0, 0);

            currentState = PULL_DOWN;
        }
    }

    private String getCurrentFormatDate() {
        SimpleDateFormat dateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        return dateFormat.format(new Date());
    }
}
