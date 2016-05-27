package com.example.nancy.smartbj.domain;

import java.util.List;

/**
 * 页签对应的新闻数据
 * Created by Nancy on 2016/5/26.
 */
public class TPINewsData {
    public int retcode;
    public TPINewsData_Data data;

    public class TPINewsData_Data {
        public String countcommenturl;
        public String more;
        public String title;
        public List<TPINewsData_Data_ListNewsData> news;
        public List<TPINewsData_Data_TopicData> topic;
        public List<TPINewsData_Data_LunboData> topnews;

        public class TPINewsData_Data_ListNewsData {

            public String commentlist;
            public String commenturl;
            public String id;
            public String listimage;
            public String pubdate;
            public String title;
            public String type;
            public String url;


        }

        public class TPINewsData_Data_TopicData {
            public String description;
            public String listimage;
            public String id;
            public String sort;
            public String title;
            public String url;
        }

        public class TPINewsData_Data_LunboData {
            public String commnet;
            public String commentlist;
            public String commenturl;
            public String id;
            public String pubdate;
            public String title;
            public String topimage;
            public String type;
            public String url;
        }
    }
}
