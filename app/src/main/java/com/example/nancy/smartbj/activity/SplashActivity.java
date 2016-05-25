package com.example.nancy.smartbj.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.example.nancy.smartbj.R;
import com.example.nancy.smartbj.utils.MyConstants;
import com.example.nancy.smartbj.utils.SpTools;

public class SplashActivity extends Activity {

    private AnimationSet as;
    private ImageView iv_mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        initView();//初始化界面
        startAnimation();//开始播放动画

        initEvent();//初始化事件
    }

    private void initEvent() {
        //1. 监听动画播完的事件
        //编程规范：如果只有一处用到的时间，用匿名对象;如果是多处用到，声明成成员变量，多处重复使用就行。
        as.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画播完 判断进入向导界面还是主界面
                if(SpTools.getBoolean(getApplication(), MyConstants.IS_SETUP,false)){
                    //true 设置过，进入主界面
                    //进入主界面
                    Intent main = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(main);


                }else{
                    //false 进入设置向导界面
                    Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
                    startActivity(intent);
                }

                //关闭自己
                finish();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 开始播放动画 旋转，缩放，渐变
     */
    private void startAnimation() {
        //构造函数中 true代表 as中的动画都是用as的补间器 false代表使用各自动画自己的补间器
        as = new AnimationSet(false);

        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(2000);
        ra.setFillAfter(true);//动画播放完之后，停留在当前状态
        //添加到动画集
        as.addAnimation(ra);

        AlphaAnimation aa = new AlphaAnimation(0, 1);
        aa.setDuration(2000);
        aa.setFillAfter(true);//动画播放完之后，停留在当前状态
        //添加到动画集
        as.addAnimation(aa);

        ScaleAnimation sa = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(2000);
        sa.setFillAfter(true);//动画播放完之后，停留在当前状态
        //添加到动画集
        as.addAnimation(sa);

        //播放动画
        iv_mainView.startAnimation(as);

        //动画播放完进入下一个界面 向导界面和主界面






    }

    private void initView() {

        //去掉标题
//        requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.activity_splash);

        iv_mainView = (ImageView) findViewById(R.id.iv_splash_mainview);
    }
}
