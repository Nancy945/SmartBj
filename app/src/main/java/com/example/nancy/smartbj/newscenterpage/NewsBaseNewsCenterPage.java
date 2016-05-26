package com.example.nancy.smartbj.newscenterpage;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.example.nancy.smartbj.R;
import com.example.nancy.smartbj.activity.MainActivity;
import com.example.nancy.smartbj.domain.NewsCenterData;
import com.example.nancy.smartbj.newstpipage.TPINewsNewsCenterPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻中心
 * Created by Nancy on 2016/5/24.
 */
public class NewsBaseNewsCenterPage extends BaseNewsCenterPage {
    private List<NewsCenterData.NewsData.ViewTagData> viewTagDatas = new ArrayList<>();

    @ViewInject(R.id.vp_newscenter_content)
    private ViewPager vp_newscenter;
    @ViewInject(R.id.tpi_newscenter_title)
    private TabPageIndicator tpi_newscenter;

    @OnClick(R.id.newscenter_ib_nextpage)
    public void next(View v) {
        //切换到下一个界面
        //此处不会越界，因为在设置的时候源码中有这样一句
        /**
         if (item < 0) {
         ** item = 0;
         } else if (item >= mAdapter.getCount()) {
         **item = mAdapter.getCount() - 1;
         }
         */
        vp_newscenter.setCurrentItem(vp_newscenter.getCurrentItem() + 1);
    }

    @Override
    public void initEvent() {
        //要调用tpi的setOnPageChangeListener，而不是viewPage的.否则会产生冲突。实际上是因为tpi的setViewPager方法取消了viewPager的监听器
        /**
         * public void setViewPager(ViewPager view) {
         ...............
         ***if (mViewPager != null) {
         ***mViewPager.setOnPageChangeListener(null);
         }
         */
        tpi_newscenter.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //当页面位于第一个 则可以滑出左侧菜单
                if(position==0){
                    //第一个可以滑出
                    mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                }else{
                    //不可以滑出
                    mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public NewsBaseNewsCenterPage(MainActivity mainActivity, List<NewsCenterData.NewsData.ViewTagData> children) {
        super(mainActivity);
        this.viewTagDatas = children;
    }

    @Override
    public View initView() {
        View newsCenterRoot = View.inflate(mainActivity, R.layout.newscenterpage_content, null);
        //注入View
        ViewUtils.inject(this, newsCenterRoot);
        return newsCenterRoot;
    }

    @Override
    public void initData() {
        //设置数据
        MyAdapter adapter = new MyAdapter();

        //设置ViewPager的适配器
        vp_newscenter.setAdapter(adapter);

        //将ViewPager和TabPageIndicator关联
        tpi_newscenter.setViewPager(vp_newscenter);

    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return viewTagDatas.size();
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        /**
         * @param position 位置
         * @return 对应位置的标题
         * TabPageIndicator调用此方法来获得标签数据
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return viewTagDatas.get(position).title;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //要展示的内容
            TPINewsNewsCenterPager tpiPager = new TPINewsNewsCenterPager(mainActivity, viewTagDatas.get(position));

            View rootView = tpiPager.getRootView();
            container.addView(rootView);

            return rootView;

//            TextView tv = new TextView(mainActivity);
//            tv.setText(viewTagDatas.get(position).title);
//            tv.setTextSize(25);
//            tv.setGravity(Gravity.CENTER);
//            container.addView(tv);
//            return tv;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
