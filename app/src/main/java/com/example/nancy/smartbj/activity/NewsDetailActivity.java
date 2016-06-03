package com.example.nancy.smartbj.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.nancy.smartbj.R;
import com.example.nancy.smartbj.utils.ShareAppUtils;

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
        //创建三个按钮公共的监听器
        View.OnClickListener listener = new View.OnClickListener() {
            int textSizeIndex = 2;

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ib_base_content_back://返回键
                        //关闭当前新闻页面
                        finish();
                        break;
                    case R.id.ib_base_content_textsize://修改字体大小
                        //通过对话框来修改字体大小的五种
                        showChangeTextSizeDialog();
                        break;
                    case R.id.ib_base_content_share://分享
                        ShareAppUtils.showShare(getApplication());
                        break;
                }
            }

            private void showChangeTextSizeDialog() {
                AlertDialog dialog = new AlertDialog.Builder(NewsDetailActivity.this).setTitle("改变字体大小")
                        .setSingleChoiceItems(new String[]{"超大号", "大号", "正常", "小号", "超小号"}, textSizeIndex, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                textSizeIndex = which;
                                setTextSize();
                                dialog.dismiss();
                            }

                        }).create();
                dialog.show();
            }

            private void setTextSize() {
                switch (textSizeIndex) {
                    case 0://超大号
                        wv_setting.setTextZoom(200);
                        break;
                    case 1://大号
                        wv_setting.setTextZoom(150);
                        break;
                    case 2://正常
                        wv_setting.setTextZoom(100);
                        break;
                    case 3://小号
                        wv_setting.setTextZoom(75);
                        break;
                    case 4://超小号
                        wv_setting.setTextZoom(50);
                        break;

                }

            }
        };

        //给三个键添加监听器
        ib_back.setOnClickListener(listener);
        ib_share.setOnClickListener(listener);
        ib_setTextSize.setOnClickListener(listener);

        //给webView添加一个新闻加载完成的监听事件
        wv_news.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                //页面加载完成时 隐藏进度条
                pb_loadingNews.setVisibility(View.GONE);
            }
        });

    }


    private void initData() {
        String url = getIntent().getStringExtra("newsurl");
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "链接错误", Toast.LENGTH_SHORT).show();
        } else {
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

    @Override
    protected void onStop() {
        wv_news.destroy();
        super.onStop();
    }
}
