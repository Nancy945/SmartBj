package com.example.nancy.smartbj.basepages;

import android.view.Gravity;
import android.widget.TextView;

import com.example.nancy.smartbj.activity.MainActivity;

/**
 * Created by Nancy on 2016/5/23.
 */
public class SmartServiceBaseTagPager extends BaseTagPage {


    public SmartServiceBaseTagPager(MainActivity mainActivity) {
        super(mainActivity);
    }

    public void initData() {
        tv_title.setText("智慧服务");

        TextView tv = new TextView(mainActivity);
        tv.setText("智慧服务的内容");
        tv.setTextSize(25);
        tv.setGravity(Gravity.CENTER);

        fl_content.addView(tv);//把内容画道白纸上

    }


}
