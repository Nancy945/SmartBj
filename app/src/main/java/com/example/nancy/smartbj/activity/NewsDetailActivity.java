package com.example.nancy.smartbj.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nancy.smartbj.R;

public class NewsDetailActivity extends AppCompatActivity {

    private ImageButton ib_back;
    private ImageButton ib_setTextSize;
    private ImageButton ib_share;
    private WebView wv_news;
    private WebSettings wv_setting;
    private ProgressBar pb_loadingNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化界面
        initView();
        //初始化数据
        initData();
        //初始化事件
        initEvent();
    }

    private void initEvent() {

    }

    private void initData() {
        String url = getIntent().getStringExtra("newsurl");
        if(TextUtils.isEmpty(url)){
            Toast.makeText(this, "链接错误", Toast.LENGTH_SHORT).show();
        }else{
            //有新闻 就加载新闻
            wv_news.loadUrl(url);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_news_detail);

        //隐藏菜单按钮不可见
        findViewById(R.id.ib_base_content_menu).setVisibility(View.GONE);
        //返回按钮可见
        ib_back = (ImageButton) findViewById(R.id.ib_base_content_back);
        ib_back.setVisibility(View.VISIBLE);
        //修改新闻的字体按钮
        ib_setTextSize = (ImageButton) findViewById(R.id.ib_base_content_textsize);
        ib_setTextSize.setVisibility(View.VISIBLE);
        //分享
        ib_share = (ImageButton) findViewById(R.id.ib_base_content_share);
        ib_share.setVisibility(View.VISIBLE);

        //显示新闻
        wv_news = (WebView) findViewById(R.id.wv_newscenter_newsdetail);
        //控制webView的显示设置
        wv_setting = wv_news.getSettings();
        //设置可以放大和缩小(右下角有个+和-的按钮)
        wv_setting.setBuiltInZoomControls(true);
        //设置可以编译javeScript脚本
        wv_setting.setJavaScriptEnabled(true);
        //设置双击可以放大和缩小
        wv_setting.setUseWideViewPort(true);

        //加载新闻的进度条
        pb_loadingNews = (ProgressBar) findViewById(R.id.pb_newscenter_newsdetail_loading);


    }
}
