package com.example.nancy.smartbj.view;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.nancy.smartbj.R;
import com.example.nancy.smartbj.basepages.BaseTagPage;
import com.example.nancy.smartbj.basepages.GovAffairsBaseTagPager;
import com.example.nancy.smartbj.basepages.HomeBaseTagPager;
import com.example.nancy.smartbj.basepages.NewsCenterBaseTagPager;
import com.example.nancy.smartbj.basepages.SettingCenterBaseTagPager;
import com.example.nancy.smartbj.basepages.SmartServiceBaseTagPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面的fragment
 * Created by Nancy on 2016/5/23.
 */
public class MainContentFragment extends BaseFragment {

    @ViewInject(R.id.vp_main_content_pages)
    private NoScrollViewPager viewPager;

    @ViewInject(R.id.rg_content_radiogroup)
    private RadioGroup rg_radios;

    private List<BaseTagPage> pages = new ArrayList<>();
    private int selectIndex; //设置当前选择的页面编号

    @Override
    public View initView() {
        View root = View.inflate(mainActivity, R.layout.fragment_content_view, null);
        //通过 xutils 动态注入view
        ViewUtils.inject(this, root);

        return root;
    }

    @Override
    public void initData() {
        // 首页
        pages.add(new HomeBaseTagPager(mainActivity));
        // 新闻中心
        pages.add(new NewsCenterBaseTagPager(mainActivity));
        // 智慧服务
        pages.add(new SmartServiceBaseTagPager(mainActivity));
        // 政务
        pages.add(new GovAffairsBaseTagPager(mainActivity));
        // 设置中心
        pages.add(new SettingCenterBaseTagPager(mainActivity));

        MyAdapter adapter = new MyAdapter();
        viewPager.setAdapter(adapter);

        //设置默认选择首页
        switchPage();
        //radioGroup默认选择第一个（首页）
        rg_radios.check(R.id.rb_main_content_home);


    }

    @Override
    public void initEvent() {
        //添加自己的事件

        //单选按钮的切换事件

        rg_radios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //五个单选按钮
                switch (checkedId) {//是哪个单选按钮点击的
                    case R.id.rb_main_content_home://主页面
                        selectIndex = 0;
                        break;
                    case R.id.rb_main_content_newscenter://新闻中心界面
                        selectIndex = 1;
                        break;
                    case R.id.rb_main_content_smartservice://智慧服务
                        selectIndex = 2;
                        break;
                    case R.id.rb_main_content_govaffairs://政务界面
                        selectIndex = 3;
                        break;
                    case R.id.rb_main_content_settingcenter://设置中心
                        selectIndex = 4;
                        break;

                }
                switchPage();
            }
        });

    }

    private void switchPage() {
        viewPager.setCurrentItem(selectIndex);//设置viewPager显示页面

        //如果是第一个或者是最后一个 不让左侧菜单滑动出来
        if (selectIndex == 0 || selectIndex == pages.size() - 1) {
            //不让左侧菜单滑出来
            mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        } else {
            //可以滑动出左侧菜单
            mainActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }

    }

    public void leftMenuClickSwitchPage(int selectPosition) {
        BaseTagPage baseTagPage = pages.get(selectPosition);
        baseTagPage.switchPage(selectPosition);

    }


    private class MyAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseTagPage baseTagPage = pages.get(position);
            View root = baseTagPage.getRoot();
            container.addView(root);

            //加载数据
            baseTagPage.initData();//因为在实例化的时候取消了initData操作（否则会消耗更多流量）

            return root;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((View) object);
        }

    }
}
