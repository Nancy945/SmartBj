package com.example.nancy.smartbj.newstpipage;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nancy.smartbj.R;
import com.example.nancy.smartbj.activity.MainActivity;
import com.example.nancy.smartbj.domain.NewsCenterData;
import com.example.nancy.smartbj.domain.TPINewsData;
import com.example.nancy.smartbj.utils.DensityUtil;
import com.example.nancy.smartbj.utils.MyConstants;
import com.example.nancy.smartbj.utils.SpTools;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 具体的页签数据 每个页签都公用这个类，数据是实时网络加载的.
 * Created by Nancy on 2016/5/26.
 */
public class TPINewsNewsCenterPager {
    private final MainActivity mainActivity;
    private final NewsCenterData.NewsData.ViewTagData viewTagData;//页签对应的数据
    private final LunboTask lunboTask;
    private View root;

    //所有组件
    @ViewInject(R.id.vp_tpi_news_lunbo_pic)
    private ViewPager vp_lunbo;//轮播图显示的viewPager
    @ViewInject(R.id.tv_tpi_news_pic_desc)
    private TextView tv_pic_desc;//图片的描述信息
    @ViewInject(R.id.ll_tpi_news_pic_points)
    private LinearLayout ll_points;//轮播图对应的点的容器
    @ViewInject(R.id.lv_tpi_news_listnews)
    private ListView lv_listnews;

    private LunboAdapter lunboAdapter;   //轮播图适配器

    private List<TPINewsData.TPINewsData_Data.TPINewsData_Data_LunboData> lunboDatas = new ArrayList<>();
    private final BitmapUtils bitmapUtils;
    private Gson gson;
    private int picSelectIndex;
    private List<TPINewsData.TPINewsData_Data.TPINewsData_Data_ListNewsData> listNews = new ArrayList<>();
    private ListNewsAdapter listNewsAdapter;

    public TPINewsNewsCenterPager(MainActivity mainActivity, NewsCenterData.NewsData.ViewTagData viewTagData) {
        this.mainActivity = mainActivity;
        this.viewTagData = viewTagData;

        gson = new Gson();
        lunboTask = new LunboTask();

        //轮播的任务
//        lunboTask = new LunBoTask();

        //xutils bitmap组件
        bitmapUtils = new BitmapUtils(mainActivity);
        bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.ARGB_4444);

        initView();//初始化界面
        initData();//初始化数据
        initEvent();//初始化事件
    }

    private void initEvent() {
        //给轮播图添加页面切换事件
        vp_lunbo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                picSelectIndex = position;//记录当前的点击页面
                setPicDescAndPointSelect(position);//根据页面更新点的样子
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setPicDescAndPointSelect(int picSelectIndex) {
        //设置描述信息
        tv_pic_desc.setText(lunboDatas.get(picSelectIndex).title);
        //设置点是否选中
        for (int i = 0; i < lunboDatas.size(); i++) {
            ll_points.getChildAt(i).setEnabled(i == picSelectIndex);
        }
    }

    private void initData() {
        //轮播图的适配器
        lunboAdapter = new LunboAdapter();
        //给轮播图设置设配齐
        vp_lunbo.setAdapter(lunboAdapter);

        //新闻列表的适配器
        listNewsAdapter = new ListNewsAdapter();
        //设置新闻列表适配
        lv_listnews.setAdapter(listNewsAdapter);

        //从本地获取数据
        String jsonCache = SpTools.getString(mainActivity, viewTagData.url, "");
        if (!TextUtils.isEmpty(jsonCache)) {
            //有数据,即诶下数据
            TPINewsData newsData = parseJson(jsonCache);
            //处理数据
            processData(newsData);
        }

        //从网络获取数据
        getDataFromNet();


    }

    private void getDataFromNet() {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, MyConstants.SERVER_URL + viewTagData.url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //请求数据成功
                String jsonData = responseInfo.result;
                //保存数据到本地
                SpTools.putString(mainActivity, viewTagData.url, jsonData);

                //解析数据
                TPINewsData newsData = parseJson(jsonData);

                //处理数据
                processData(newsData);

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                //请求数据失败
                System.out.println("网络访问失败:" + error.toString());
            }
        });
    }

    private void processData(TPINewsData newsData) {
        //完成数据的处理
        //1.设置轮播图的数据
        setLunboData(newsData);
        //2.轮播图对应的点处理
        initPoints();//初始化轮播图的点
        //3.设置图片描述和点的选中效果
        setPicDescAndPointSelect(picSelectIndex);
        //4.开始轮播图
        lunboTask.startLunboTask();
        //5.新闻列表的数据
        setListViewNews(newsData);
    }

    /**
     * 设置新闻列表数据
     *
     * @param newsData 新闻数据
     */
    private void setListViewNews(TPINewsData newsData) {
        listNews = newsData.data.news;
        //更新界面
        listNewsAdapter.notifyDataSetChanged();
    }


    private class LunboTask {
        private Handler handler = new Handler();

        public void stopLunbo() {
            //移除所有callback和消息
            handler.removeCallbacksAndMessages(null);
        }

        public void startLunboTask() {
            stopLunbo();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //控制轮播图的显示
                    vp_lunbo.setCurrentItem((vp_lunbo.getCurrentItem() + 1) % vp_lunbo.getAdapter().getCount());

                    handler.postDelayed(this, 2000);
                }
            }, 2000);

        }
    }


    private void initPoints() {
        ll_points.removeAllViews();//清空以前存在的点
        //轮播图有几张，就加几个点

        for (int i = 0; i < lunboDatas.size(); i++) {
            View v_point = new View(mainActivity);
            //设置点的背景选择器
            v_point.setBackgroundResource(R.drawable.point_selector);
            v_point.setEnabled(false);//默认是灰色的点

            int pointSizePx = DensityUtil.dip2px(mainActivity, 5);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(pointSizePx, pointSizePx);
            params.leftMargin = DensityUtil.dip2px(mainActivity, 10);
            //设置参数
            v_point.setLayoutParams(params);
            ll_points.addView(v_point);
        }
    }

    private void setLunboData(TPINewsData newsData) {
        //获取轮播图的数据
        lunboDatas = newsData.data.topnews;

        lunboAdapter.notifyDataSetChanged();

    }

    private TPINewsData parseJson(String jsonCache) {
        //解析json数据
        return gson.fromJson(jsonCache, TPINewsData.class);
    }

    private void initView() {
        //页签页面中的根布局，只有一个ListView （本来还有一个还有一个ViewPager，现在作为listView的头显示）
        //这样最大的好处在于，可以整体滚动，而不用外套scrollView
        root = View.inflate(mainActivity, R.layout.tpi_new_content, null);
        ViewUtils.inject(this, root);

        View lunboPic = View.inflate(mainActivity, R.layout.tpi_new_lunbopic, null);
        ViewUtils.inject(this, lunboPic);
        //todo 把轮播图加到listView中去
        lv_listnews.addHeaderView(lunboPic);
    }


    public View getRootView() {
        return root;
    }

    private class LunboAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return lunboDatas.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView iv_lunbo_pic = new ImageView(mainActivity);
            iv_lunbo_pic.setScaleType(ImageView.ScaleType.FIT_XY);

            //设备默认的图片，网络缓慢的时候使用这个
            iv_lunbo_pic.setImageResource(R.drawable.home_scroll_default);

            //给图片添加数据
            TPINewsData.TPINewsData_Data.TPINewsData_Data_LunboData tpiLunboData = lunboDatas.get(position);
            //图片url
            String topImageUrl = tpiLunboData.topimage;
            //把url的图片给iv_lunbo_pic
            //异步加载加载图片,并且显示到组件中
            bitmapUtils.display(iv_lunbo_pic, topImageUrl);

            //给图片添加触摸事件，使其在被点击的状态下停止轮播
            iv_lunbo_pic.setOnTouchListener(new View.OnTouchListener() {

                private float downX;
                private float downY;
                private long downTime;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downX = event.getX();
                            downY = event.getY();
                            downTime = System.currentTimeMillis();
                            lunboTask.stopLunbo();//停止定时轮播
                            break;
                        case MotionEvent.ACTION_UP:
                            float upX = event.getX();
                            float upY = event.getY();

                            //如果是单击（500ms内按下且在同一个地方松起）
                            if (upX == downX && upY == downY && System.currentTimeMillis() - downTime < 500) {
                                System.out.println("被单击了");
                            }

                            lunboTask.startLunboTask();//开始轮播

                            break;
                        case MotionEvent.ACTION_CANCEL:
                            lunboTask.startLunboTask();//开始轮播

                            break;

                    }
                    return true;
                }
            });


            container.addView(iv_lunbo_pic);
            return iv_lunbo_pic;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View) object);
        }
    }

    private class ListNewsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return listNews.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mainActivity, R.layout.tpi_news_listview_item, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_tpi_news_listview_item_icon);
                holder.iv_newspic = (ImageView) convertView.findViewById(R.id.iv_tpi_news_listview_item_pic);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_tpi_news_listview_item_title);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_tpi_news_listview_item_time);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //设置数据
            TPINewsData.TPINewsData_Data.TPINewsData_Data_ListNewsData newsData = listNews.get(position);
            //设置标题

            holder.tv_title.setText(newsData.title);
            //设置时间
            holder.tv_time.setText(newsData.pubdate);
            //设置图片
            bitmapUtils.display(holder.iv_newspic, newsData.listimage);
            return convertView;
        }


        private class ViewHolder {
            ImageView iv_newspic;
            TextView tv_title;
            TextView tv_time;
            ImageView iv_icon;
        }
    }
}
