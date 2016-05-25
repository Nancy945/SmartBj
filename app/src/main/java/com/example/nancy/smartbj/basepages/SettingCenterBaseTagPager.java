package com.example.nancy.smartbj.basepages;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.nancy.smartbj.activity.MainActivity;

/**
 * Created by Nancy on 2016/5/23.
 */
public class SettingCenterBaseTagPager extends BaseTagPage {


    public SettingCenterBaseTagPager(MainActivity mainActivity) {
        super(mainActivity);
    }

    public void initData() {
        ib_menu.setVisibility(View.GONE);
        tv_title.setText("设置中心");

        TextView tv = new TextView(mainActivity);
        tv.setText("设置中心内容");
        tv.setTextSize(25);
        tv.setGravity(Gravity.CENTER);

        fl_content.addView(tv);//把内容画道白纸上

    }


}
