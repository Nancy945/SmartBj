package com.example.nancy.smartbj.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 每个页签里面都用了这个ViewPager，用来实现轮播图。
 * 主要是为了能够正确拦截事件。
 * Created by Nancy on 2016/5/27.
 */
public class InterceptScrollViewPager extends ViewPager {

    private float downX;
    private float downY;

    public InterceptScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptScrollViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //true 申请父控件不拦截我的touch事件，false默认父类先拦截事件

        //事件完全由自己处理
        //如果在第一个页面，并且是从左往右滑动，让父控件拦截我 用来显示侧滑菜单
        //如果在最后一个页面，并且是从右往左滑动，让父控件拦截我 为了让tpi正常工作能够跳到下一个页签

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                //记录按下的位置
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = ev.getX();
                float moveY = ev.getY();


                float dx = moveX - downX;
                float dy = moveY - downY;
                //如果横向移动
                if(Math.abs(dx) > Math.abs(dy)){
                    //如果在第一个页面，并且是从左往右滑动
                    if (getCurrentItem() == 0 && dx > 0){
                        //让父组件拦截
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else if(getCurrentItem() == getAdapter().getCount()-1 && dx<0){
                        //如果在最后一个页面，并且是从右往左滑动
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else{
                        //不要拦截
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }else{
                    //默认让父组件拦截
                    getParent().requestDisallowInterceptTouchEvent(false);

                }
                break;

        }


        return super.dispatchTouchEvent(ev);
    }
}
