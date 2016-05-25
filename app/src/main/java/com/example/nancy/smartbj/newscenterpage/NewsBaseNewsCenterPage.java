package com.example.nancy.smartbj.newscenterpage;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nancy.smartbj.R;
import com.example.nancy.smartbj.activity.MainActivity;
import com.example.nancy.smartbj.domain.NewsCenterData;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
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
            TextView tv = new TextView(mainActivity);
            tv.setText(viewTagDatas.get(position).title);
            tv.setTextSize(25);
            tv.setGravity(Gravity.CENTER);

            container.addView(tv);

            return tv;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
