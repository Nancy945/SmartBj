package com.example.nancy.smartbj.newscenterpage;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.nancy.smartbj.activity.MainActivity;

/**
 * Created by Nancy on 2016/5/24.
 */
public class NewsBaseNewsCenterPage extends BaseNewsCenterPage {
    public NewsBaseNewsCenterPage(MainActivity mainActivity) {
        super(mainActivity);
    }

    @Override
    public View initView() {
        TextView tv = new TextView(mainActivity);
        tv.setText("新闻页面");
        tv.setTextSize(25);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }
}
