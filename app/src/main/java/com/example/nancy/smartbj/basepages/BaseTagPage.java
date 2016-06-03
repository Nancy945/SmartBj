package com.example.nancy.smartbj.basepages;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nancy.smartbj.R;
import com.example.nancy.smartbj.activity.MainActivity;

/**
 * Created by Nancy on 2016/5/23.
 */
public class BaseTagPage {

    protected final MainActivity mainActivity;
    protected View ib_menu;
    protected TextView tv_title;
    protected FrameLayout fl_content;
    protected View root;
    protected ImageButton ib_listOrGrid;

    public BaseTagPage(MainActivity context) {
        this.mainActivity = context;

        initView();
//        initData();  为了防止每次实例化子类就加载数据（有的时候需要禁用此功能以节省流量）,所以将此行注释掉
        initEvent();

    }


    public void initView() {
        //界面的根布局

        root = View.inflate(mainActivity, R.layout.fragment_content_base_content, null);
        ib_menu = root.findViewById(R.id.ib_base_content_menu);
        tv_title = (TextView) root.findViewById(R.id.tv_base_content_title);
        fl_content = (FrameLayout) root.findViewById(R.id.fl_base_content_tag);

        ib_listOrGrid = (ImageButton)root.findViewById(R.id.ib_base_content_listorgrid);
    }

    //子类根据需要选择是否复写
    public void initEvent() {
        ib_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.getSlidingMenu().toggle();//点击imageButton切换左侧显示
            }
        });
    }
    //子类根据需要选择是否复写

    public void initData() {
    }

    /**
     * @return 返回基类的布局对象
     */
    public View getRoot() {
        return this.root;
    }

    public void switchPage(int selectPosition) {

    }
}
