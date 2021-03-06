package com.example.nancy.smartbj.utils;

/**
 * 接口定义前的public不能少，否则外面的包还是不能访问这里的变量.
 * 常量
 * Created by Nancy on 2016/5/21.
 */

public interface MyConstants {
    //apk 发布的s时候修改ip 或者 用域名来代替 可以省略修改IP的步骤
    String SERVER_URL = "http://10.0.2.2:8080/zhbj";
    String NEWSCENTER_URL = SERVER_URL + "/categories.json";
    String PHOTOSURL = SERVER_URL + "/photos/photos_1.json";
    String CONFIG_FILE_NAME = "configFileName";//sp的文件名
    String IS_SETUP = "isSetUp";//是否设置过向导界面
    String READNEWS_IDS = "readNewsIds";//保存读过的新闻ID
}
