package com.example.nancy.smartbj.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.nancy.smartbj.R;
import com.example.nancy.smartbj.view.LeftMenuFragment;
import com.example.nancy.smartbj.view.MainContentFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * 智慧北京的主界面
 */
public class MainActivity extends SlidingFragmentActivity {

    private static final String LEFT_MENU_TAG = "leftMenuTag";
    private static final String MAIN_CONTENT_TAG = "mainContentTag";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(); //初始化界面
        initData(); //初始化界面
    }

    private void initData() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        //1.获取事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //2.完成替换
        transaction.replace(R.id.fl_left_menu, new LeftMenuFragment(), LEFT_MENU_TAG);
        transaction.replace(R.id.fl_main_content, new MainContentFragment(), MAIN_CONTENT_TAG);
        //3.提交事务
        transaction.commit();

    }

    private void initView() {
        //设置主界面 里面是一个空的FrameLayout
        setContentView(R.layout.fragment_content_tag);

        //设置左侧菜单界面 里面是一个空的FrameLayout
        setBehindContentView(R.layout.fragment_left);

        //设置滑动模式
        SlidingMenu menu = getSlidingMenu();
        menu.setMode(SlidingMenu.LEFT); //只允许左侧滑出

        //设置滑动位置为全屏
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        //设置左侧滑动出来后，界面剩余的大小
        menu.setBehindOffset(200);

    }

    /**
     * @return 返回左侧菜单的fragment
     */
    public LeftMenuFragment getLeftMenuFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return (LeftMenuFragment) fragmentManager.findFragmentByTag(LEFT_MENU_TAG);
    }

    /**
     * @return 返回主内容的fragment
     */
    public MainContentFragment getMainContentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        return (MainContentFragment) fragmentManager.findFragmentByTag(MAIN_CONTENT_TAG);
    }
}
