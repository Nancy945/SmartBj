package com.example.nancy.smartbj.newscenterpage;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.nancy.smartbj.activity.MainActivity;

/**
 * Created by Nancy on 2016/5/24.
 */
public class PhotosBaseNewsCenterPage extends BaseNewsCenterPage {
    public PhotosBaseNewsCenterPage(MainActivity mainActivity) {
        super(mainActivity);
    }

    @Override
    public View initView() {
        TextView tv = new TextView(mainActivity);
        tv.setText("组图界面");
        tv.setTextSize(25);
        tv.setGravity(Gravity.CENTER);
        return tv;
    }
}
