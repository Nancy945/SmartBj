package com.example.nancy.smartbj.newstpipage;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
 * Created by Nancy on 2016/5/26.
 */
public class TPINewsNewsCenterPager {
    private final MainActivity mainActivity;
    private final NewsCenterData.NewsData.ViewTagData viewTagData;//页签对应的数据
    private View root;

    //所有组件
    @ViewInject(R.id.vp_tpi_news_lunbo_pic)
    private ViewPager vp_lunbo;//轮播图显示的viewPager
    @ViewInject(R.id.tv_tpi_news_pic_desc)
    private TextView tv_pic_desc;//图片的描述信息
    @ViewInject(R.id.ll_tpi_news_pic_points)
    private LinearLayout ll_points;//轮播图对应的点的容器

    private LunboAdapter lunboAdapter;   //轮播图适配器

    private List<TPINewsData.TPINewsData_Data.TPINewsData_Data_LunboData> lunboDatas = new ArrayList<>();
    private final BitmapUtils bitmapUtils;
    private Gson gson;
    private int picSelectIndex ;

    public TPINewsNewsCenterPager(MainActivity mainActivity, NewsCenterData.NewsData.ViewTagData viewTagData) {
        this.mainActivity = mainActivity;
        this.viewTagData = viewTagData;

        gson = new Gson();

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
        //5.新闻列表的数据
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
        //页签页面中的根布局，由ViewPager+ListView组成
        root = View.inflate(mainActivity, R.layout.tpi_new_lunbopic, null);

        ViewUtils.inject(this, root);
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


            container.addView(iv_lunbo_pic);
            return iv_lunbo_pic;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View) object);
        }
    }
}
