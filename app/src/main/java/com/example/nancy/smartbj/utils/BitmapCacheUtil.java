package com.example.nancy.smartbj.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 位图工具类
 * Created by Nancy on 2016/6/2.
 */
public class BitmapCacheUtil {

    private Activity activity;
    private final File cacheDir;//缓存目录
    private Map<ImageView, String> imageViewAndUrlMap = new HashMap<>();//用来存放最后一次访问的url信息
    private ExecutorService threadPool;//线程池，来复用线程。避免线程的新建和销毁。
    //动态获取jvm的内存来分配大小。
    private int maxSize = (int) (Runtime.getRuntime().freeMemory() / 2);

    private LruCache<String, Bitmap> memCache = new LruCache<String, Bitmap>(maxSize) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };


    public BitmapCacheUtil(Activity activity) {
        this.activity = activity;
        //获取当前app的cache目录
        cacheDir = activity.getCacheDir();
        //线程池 最大个数根据情况自定
        threadPool = Executors.newFixedThreadPool(6);

    }

    public void display(ImageView iv, String ivUrl) {
        //1 先从内存取
        Bitmap bitmap = memCache.get(ivUrl);
        if (bitmap != null) {
            System.out.println("从内存获取到数据");
            iv.setImageBitmap(bitmap);
            return;
        }

        //2 再从本地文件中获取数据，/data/data/<PackageName>/cache/文件名
        bitmap = getCacheFile(ivUrl);
        if (bitmap != null) {
            System.out.println("从文件获取到数据");
            //往内存中写
            memCache.put(ivUrl, bitmap);
            iv.setImageBitmap(bitmap);
            return;
        }

        //3 从网络取数据
        imageViewAndUrlMap.put(iv, ivUrl);
        getBitmapFromNet(iv, ivUrl);
    }

    /**
     * 从网络获取数据
     *
     * @param iv    加载到哪个imageView
     * @param ivUrl 图片的url
     */
    private void getBitmapFromNet(ImageView iv, String ivUrl) {
        //线程池
        threadPool.submit(new DownLoadRunnable(iv, ivUrl));
    }

    /**
     * @param ivUrl 图片url 当作缓存图片的名字
     * @return 缓存目录的Bitmap
     */
    public Bitmap getCacheFile(String ivUrl) {
        //把ivUrl转成成MD5值 ，再把MD5值作为文件名
        File file = new File(cacheDir, MD5Utils.getMD5(ivUrl));
        if (file.exists()) {
            //文件存在
            //把文件转换成bitmap
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            //不存在
            return null;
        }
    }

    private class DownLoadRunnable implements Runnable {
        private String ivUrl;
        private ImageView iv;

        public DownLoadRunnable(ImageView iv, String ivUrl) {
            this.iv = iv;
            this.ivUrl = ivUrl;
        }

        @Override
        public void run() {
            //访问网络
            try {
                URL url = new URL(ivUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(6000);//设置连接超时时间
                conn.setRequestMethod("GET");
                int code = conn.getResponseCode();

                if (code == 200) {
                    //请求成功
                    InputStream is = conn.getInputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(is);
                    //1.往内存中添加
                    memCache.put(ivUrl, bitmap);
                    //2.往文件中添加
                    saveBitmapToCacheDir(bitmap, ivUrl);
                    //3.显示数据
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("从网络获取到数据");
                            //显示图片前先判断是不是最新的
                            if(ivUrl.equals(imageViewAndUrlMap.get(iv))){
                                //是自己的数据
                                iv.setImageBitmap(bitmap);
                            }
                        }
                    });

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * @param bitmap 需要压缩并存储的图片
         * @param ivUrl 图片的url（需要根据此设置文件名）
         */
        private void saveBitmapToCacheDir(Bitmap bitmap, String ivUrl) {
            File file = new File(cacheDir, MD5Utils.getMD5(ivUrl));
            try {
                //压缩图片，并到文件中
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
