package com.example.nancy.smartbj.basepages;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.nancy.smartbj.activity.MainActivity;
import com.example.nancy.smartbj.domain.NewsCenterData;
import com.example.nancy.smartbj.newscenterpage.BaseNewsCenterPage;
import com.example.nancy.smartbj.newscenterpage.InteractBaseNewsCenterPage;
import com.example.nancy.smartbj.newscenterpage.NewsBaseNewsCenterPage;
import com.example.nancy.smartbj.newscenterpage.PhotosBaseNewsCenterPage;
import com.example.nancy.smartbj.newscenterpage.TopicBaseNewsCenterPage;
import com.example.nancy.smartbj.utils.MyConstants;
import com.example.nancy.smartbj.utils.SpTools;
import com.example.nancy.smartbj.view.LeftMenuFragment;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻中心
 * Created by Nancy on 2016/5/23.
 */
public class NewsCenterBaseTagPager extends BaseTagPage {


    private List<BaseNewsCenterPage> newsCenterPages = new ArrayList<>();
    private NewsCenterData newsCenterData;
    private Gson gson;

    public NewsCenterBaseTagPager(MainActivity context) {
        super(context);
    }

    public void initData() {

        //1.获取本地数据
        String jsonCache = SpTools.getString(mainActivity, MyConstants.NEWSCENTER_URL, "");
        if (!TextUtils.isEmpty(jsonCache)) {
            //有本地数据
            //从本地取数据显示
            parseData(jsonCache);
        }

        //2. 获取网络数据
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, MyConstants.NEWSCENTER_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //访问数据成功
                String jsonData = responseInfo.result;

                //保存数据到本地一份
                SpTools.putString(mainActivity, MyConstants.NEWSCENTER_URL, jsonData);

                Log.e("NewsCenterBaseTagPager", jsonData);
                //2.解析数据
                parseData(jsonData);

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                //访问数据失败
                System.out.println("网络请求失败,错误原因:" + error.toString());
            }
        });

    }

    private void parseData(String jsonData) {
        if (gson == null) {
            gson = new Gson();
        }
        newsCenterData = gson.fromJson(jsonData, NewsCenterData.class);

        //3.数据的处理

        //在这里给左侧菜单设置数据
        mainActivity.getLeftMenuFragment().setLeftMenuData(newsCenterData.data);

        //设置左侧菜单的监听回调
        mainActivity.getLeftMenuFragment().setOnSwitchPageListener(new LeftMenuFragment.OnSwitchPageListener() {
            @Override
            public void switchPage(int selectionIndex) {
                NewsCenterBaseTagPager.this.switchPage(selectionIndex);
            }
        });

        //读取的数据封装到界面容器中，通过左侧菜单点击，显示不同的界面
        //根据服务的数据创建四个页面（按顺序）
        for (NewsCenterData.NewsData newsData : newsCenterData.data) {

            BaseNewsCenterPage newsPage = null;

            switch (newsData.type) {
                case 1://新闻页面
                    newsPage = new NewsBaseNewsCenterPage(mainActivity, newsData.children);
                    break;
                case 10://专题页面
                    newsPage = new TopicBaseNewsCenterPage(mainActivity);
                    break;
                case 2://组图页面
                    newsPage = new PhotosBaseNewsCenterPage(mainActivity);
                    break;
                case 3://互动页面
                    newsPage = new InteractBaseNewsCenterPage(mainActivity);
                    break;
            }

            //添加新闻中心的页面到容器中
            newsCenterPages.add(newsPage);
        }

        //控制四个页面的显示，默认选择第一个新闻页面
        switchPage(0);

    }

    /**
     * 根据位置，动态显示不同的新闻中心页面
     *
     * @param selectPosition 位置
     */
    @Override
    public void switchPage(int selectPosition) {
        BaseNewsCenterPage baseNewsCenterPage = newsCenterPages.get(selectPosition);
        //显示数据
        //设置本page的标题
        tv_title.setText(newsCenterData.data.get(selectPosition).title);

        //移除掉原来的画的内容
        fl_content.removeAllViews();

        //初始化数据
        baseNewsCenterPage.initData();

        //判断 如果是组图listGrid 切换的按钮显示
        if (baseNewsCenterPage instanceof PhotosBaseNewsCenterPage) {
            //如果是组图
            //显示listgrid切换的按钮
            ib_listOrGrid.setVisibility(View.VISIBLE);
            //给时间，点击做list和grid切换
            ib_listOrGrid.setTag(baseNewsCenterPage);
            ib_listOrGrid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((PhotosBaseNewsCenterPage) ib_listOrGrid.getTag()).switchListViewOrGridView(ib_listOrGrid);
                }
            });
        } else {
            //隐藏listgrid切换的按钮显示
            ib_listOrGrid.setVisibility(View.GONE);
        }

        //替换掉白纸
        fl_content.addView(baseNewsCenterPage.getRoot());


    }
}
