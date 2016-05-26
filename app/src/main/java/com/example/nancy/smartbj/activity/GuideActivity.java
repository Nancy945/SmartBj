package com.example.nancy.smartbj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.nancy.smartbj.R;
import com.example.nancy.smartbj.utils.DensityUtil;
import com.example.nancy.smartbj.utils.MyConstants;
import com.example.nancy.smartbj.utils.SpTools;

import java.util.ArrayList;

/**
 *  第一次启动才有的介绍或者广告轮播界面
 */
public class GuideActivity extends Activity {

    private ViewPager vp_guids;
    private LinearLayout ll_points;
    private View v_redpoint;
    private Button bt_startExp;
    private ArrayList<ImageView> guideImgList;
    private MyAdapter adapter;
    private int pointsDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化界面
        initData();//初始化数据
        initEvent();//d初始化组件事件

    }

    private void initEvent() {
        //监听布局完成，触发的结果
        v_redpoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //因为会多次监听，没有必要。所以在第一次回调就取消注册就行
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    //API16 才有这个方法
                    v_redpoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }else {
                    //老版本就用过时方法就行
                    v_redpoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                //两个点间的距离等于 自身的宽度+marginLeft
//                System.out.println("两个点间的距离"+(ll_points.getChildAt(1).getLeft() - ll_points.getChildAt(0).getLeft()));
                pointsDistance = ll_points.getChildAt(1).getLeft() - ll_points.getChildAt(0).getLeft();



            }
        });

        //给按钮添加点击事件
        bt_startExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存设置的状态
                SpTools.putBoolean(GuideActivity.this, MyConstants.IS_SETUP, true);

                //进入主界面
                Intent main = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(main);

                //关闭自己
                finish();
            }
        });

        vp_guids.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * 滑动过程中触发的时间
             * @param position 当前的位置
             * @param positionOffset 偏移比例值
             * @param positionOffsetPixels 偏移的像素值
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //计算红点的左边距

                float leftMargin = pointsDistance * (position + positionOffset);
                //设置红点的左边距
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) v_redpoint.getLayoutParams();
                params.leftMargin = Math.round(leftMargin);

                //重新设置布局
                v_redpoint.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                //position是当前页 界面切换的时候调用

                //最后一页就显示button
                if (position == guideImgList.size() - 1) {
                    bt_startExp.setVisibility(View.VISIBLE);
                } else {
                    bt_startExp.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData() {
        //viewPager adapter list
        //图片数据
        int[] pics = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
        //定义ViewPager使用的容器
        guideImgList = new ArrayList<>();

        //初始化容器中的数据

        for (int i = 0; i < pics.length; i++) {
            ImageView iv_temp = new ImageView(this);
            iv_temp.setBackgroundResource(pics[i]);
            //添加界面的数据
            guideImgList.add(iv_temp);

            //给点的容器LinearLayout初始化灰色的点
            View v_point = new View(this);
            v_point.setBackgroundResource(R.drawable.gray_point);
            //添加灰色点到线性布局中

            //设置点的大小
            int px_10dp = DensityUtil.dip2px(this, 10);//10个dp对应的px
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(px_10dp,px_10dp);//todo 构造函数中的单位是px，xml中的单位是dp
            //设置点与点之间的间隙
            if(i!= 0){
                params.leftMargin = px_10dp;//单位也是px
            }
            v_point.setLayoutParams(params);

            ll_points.addView(v_point);

        }

        //此方法不可行，因为，点的位置是确定不了的。布局完成，才能求出left值
//        System.out.println("两个点间的距离"+(ll_points.getChildAt(1).getLeft() - ll_points.getChildAt(1).getLeft()))
        //所以改用getViewTreeObserver的方法，在initEvent中设置了时间

        //创建ViewPager的适配器
        adapter = new MyAdapter();
        //设置适配器
        vp_guids.setAdapter(adapter);


    }

    private class MyAdapter extends PagerAdapter {


        @Override
        public int getCount() {
            return guideImgList.size();//返回数据的个数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object; //过滤和缓存的作用
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //container 就是 viewPager
            View child = guideImgList.get(position);
            //添加view
            container.addView(child);

            return child;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //这里的object参数就是那个需要移除的view
            /**
             ImageView imageView = guideImgList.get(position);
             vp.removeView(imageView); container 就是vp
             */
            container.removeView((View) object);
        }

    }

    private void initView() {
        //不要标题 (似乎就是actionBar）
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);

        //ViewPager组件
        vp_guids = (ViewPager) findViewById(R.id.vp_guide_pages);
        // 动态加点容器
        ll_points = (LinearLayout) findViewById(R.id.ll_guide_points);
        //红点
        v_redpoint = findViewById(R.id.v_guide_redpoint);
        //开始体验按钮
        bt_startExp = (Button) findViewById(R.id.bt_guide_startexp);

    }
}
