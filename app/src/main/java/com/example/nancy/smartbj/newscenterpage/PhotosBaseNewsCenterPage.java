package com.example.nancy.smartbj.newscenterpage;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nancy.smartbj.R;
import com.example.nancy.smartbj.activity.MainActivity;
import com.example.nancy.smartbj.domain.PhotosData;
import com.example.nancy.smartbj.utils.BitmapCacheUtil;
import com.example.nancy.smartbj.utils.MyConstants;
import com.example.nancy.smartbj.utils.SpTools;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 组图
 * Created by Nancy on 2016/5/24.
 */
public class PhotosBaseNewsCenterPage extends BaseNewsCenterPage {

    private final BitmapCacheUtil bitmapCacheUtil;

    @ViewInject(R.id.lv_newscenter_photos)
    private ListView lv_photos;

    @ViewInject(R.id.gv_newscenter_photos)
    private GridView gv_photos;
    private MyAdapter adapter;
    private boolean isShowList = true;
    private List<PhotosData.PhotosData_Data.PhotosNews> photosNews = new ArrayList<>();

    public PhotosBaseNewsCenterPage(MainActivity mainActivity) {
        super(mainActivity);
        bitmapCacheUtil = new BitmapCacheUtil(mainActivity);
    }

    @Override
    public View initView() {
        View photos_root = View.inflate(mainActivity, R.layout.newscenter_photos, null);

        ViewUtils.inject(this, photos_root);
        return photos_root;
    }

    @Override
    public void initData() {
        if (adapter == null) {
            //创建适配器
            adapter = new MyAdapter();
            lv_photos.setAdapter(adapter);
            gv_photos.setAdapter(adapter);
        }

        if (isShowList) {
            lv_photos.setVisibility(View.VISIBLE);
            gv_photos.setVisibility(View.GONE);
        } else {
            gv_photos.setVisibility(View.VISIBLE);
            lv_photos.setVisibility(View.GONE);
        }

        //本地取数据(缓存)
        String photoJsonData = SpTools.getString(mainActivity, MyConstants.PHOTOSURL, null);
        if (!TextUtils.isEmpty(photoJsonData)) {
            //有数据 ,则解析数据
            PhotosData photosData = parsePhotosJson(photoJsonData);

            //处理组图数据
            processPhotosData(photosData);
        }

        //网络取数据，解析数据，处理组图数据
        getDataFromNet();
    }

    public void switchListViewOrGridView(ImageButton ib_listOrGrid) {
        if (isShowList) {
            //按钮的背景设置成list
            ib_listOrGrid.setImageResource(R.drawable.icon_pic_list_type);
            //隐藏listView
            lv_photos.setVisibility(View.GONE);
            //显示gridView
            gv_photos.setVisibility(View.VISIBLE);

        } else {
            //按钮的背景设置成grid
            ib_listOrGrid.setImageResource(R.drawable.icon_pic_grid_type);
            //显示listView
            lv_photos.setVisibility(View.VISIBLE);
            //隐藏gridView
            gv_photos.setVisibility(View.GONE);
        }

        isShowList = !isShowList;
    }

    private void getDataFromNet() {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.GET, MyConstants.PHOTOSURL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                //请求数据成功
                //获取组图的json数据
                String jsonData = responseInfo.result;

                //缓存
                SpTools.putString(mainActivity, MyConstants.PHOTOSURL, jsonData);

                //解析json数据
                PhotosData photosData = parsePhotosJson(jsonData);

                //处理组图数据
                processPhotosData(photosData);

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                System.out.println("请求网络失败:" + error.toString());
            }
        });
    }

    private void processPhotosData(PhotosData photosData) {
        //获取组图的所有数据
        photosNews = photosData.data.news;
        adapter.notifyDataSetChanged();//通知界面更新数据
    }

    private PhotosData parsePhotosJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, PhotosData.class);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return photosNews.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            //判断是否存在view缓存
            if (convertView == null) {
                convertView = View.inflate(mainActivity, R.layout.photoslist_item, null);
                holder = new ViewHolder();
                holder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_photos_list_item_pic);
                holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_photos_list_item_desc);

                convertView.setTag(holder);
            } else {
                //有界面缓存
                holder = (ViewHolder) convertView.getTag();
            }

            //赋值
            //取当前数据
            PhotosData.PhotosData_Data.PhotosNews pn = photosNews.get(position);
            //设置名字
            holder.tv_desc.setText(pn.title);
            //设置图片
            bitmapCacheUtil.display(holder.iv_pic, pn.listimage);

            return convertView;
        }

        private class ViewHolder {
            ImageView iv_pic;
            TextView tv_desc;
        }
    }
}
