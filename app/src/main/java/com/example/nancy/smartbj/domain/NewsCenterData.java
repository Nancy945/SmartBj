package com.example.nancy.smartbj.domain;

import java.util.List;

/**
 * 新闻数据bean类 用来存放JSON解析的字符串
 * Created by Nancy on 2016/5/24.
 */
public class NewsCenterData {
    public List<NewsData> data;
    public List<Integer> extend;
    public int retcode;

    public class NewsData {
        public List<ViewTagData> children;
        public int id;
        public String title;
        public int type;
        public String url;
        public String url1;
        public String dayurl;
        public String excurl;
        public String weekurl;

        public class ViewTagData {
            public int id;
            public String title;
            public int type;
            public String url;
        }
    }


}
