package com.example.nancy.smartbj.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.example.nancy.smartbj.R;

/**
 * 带有下拉刷新和上拉加载更多的listView
 * Created by Nancy on 2016/5/31.
 */
public class RefreshListView extends ListView {
    public RefreshListView(Context context) {
        this(context,null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
        initAnimation();
        initEvent();
    }

    private void initEvent() {

    }

    private void initAnimation() {

    }

    private void initView() {
        //初始化头部
        initHeader();
        //初始化底部
        iniitFooter();

    }

    private void iniitFooter() {

    }

    private void initHeader() {
        View.inflate(getContext(), R.layout.listview_header_container, null);

    }


}
