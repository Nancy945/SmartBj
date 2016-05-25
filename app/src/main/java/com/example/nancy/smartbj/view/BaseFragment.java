package com.example.nancy.smartbj.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nancy.smartbj.activity.MainActivity;

/**
 * 基类fragment
 * Created by Nancy on 2016/5/23.
 */
public abstract class BaseFragment extends android.support.v4.app.Fragment {
    protected MainActivity mainActivity;//上下文

    //只会调用一次
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取fragment所在的Activity
        this.mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return initView();
    }

    /**
     * 必须覆盖此方法来完成界面的显示
     *
     * @return 需要加载的界面view
     */
    public abstract View initView();


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化数据和事件

        initData();
        initEvent();
    }


    /**
     * 子类可以根据需要来选择是否覆盖此方法来初始化事件
     */
    public void initEvent() {
    }

    /**
     * 子类可以根据需要来选择是否覆盖此方法来初始化数据
     */
    public void initData() {
    }
}
