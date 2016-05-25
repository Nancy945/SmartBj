package com.example.nancy.smartbj.newscenterpage;

import android.view.View;

import com.example.nancy.smartbj.activity.MainActivity;

/**
 * 新闻 专题 组图 互动 的基类
 * Created by Nancy on 2016/5/24.
 */
public abstract class BaseNewsCenterPage {
    protected MainActivity mainActivity;
    protected View root;//跟布局

    public BaseNewsCenterPage(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        root = initView();
        initEvent();
    }

    /**
     * 子类覆盖此方法完成数据的显示
     */
    public void initData() {

    }

    /**
     * 子类覆盖此方法完成事件的处理
     */
    public void initEvent() {

    }

    /**
     * @return 子类覆盖此方法来显示自定义View
     */
    public abstract View initView();

    /**
     * @return 返回根布局
     */
    public View getRoot() {
        return root;
    }


}
