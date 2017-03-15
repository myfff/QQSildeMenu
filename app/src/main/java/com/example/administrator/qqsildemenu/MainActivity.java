package com.example.administrator.qqsildemenu;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ListView lvMain, lvMenu;
    private ImageView imagemenu,imagemain;
    private QQDraglayout qqDraglayout;
    private  MyLinearlayut myLinearlayut;
    private static final String TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initUi();
        initData();
        System.out.print("你好付芳1");
        qqDraglayout.setMenuChangedStateLister(new QQDraglayout.MenuViewChangedStateLister() {
            @Override
            public void open() {
                //打开侧边栏时
                Log.i(TAG, "open: ");
                lvMenu.smoothScrollToPosition(new Random().nextInt(lvMenu.getCount()));
            }
            @Override
            public void close() {
                //让main的image滑动
                Log.e("tag", "onClose");
                ViewPropertyAnimator.animate(imagemain).translationXBy(15)
                        .setInterpolator(new CycleInterpolator(4))
                        .setDuration(500)
                        .start();
            }

            @Override
            public void onDraging(float fraction) {
                Log.e("tag", "onDraging fraction:"+fraction);
                ViewHelper.setAlpha(imagemain,1-fraction);

            }
        });
        myLinearlayut.setSlideMenu(qqDraglayout);//j将侧边栏传给main，让main根据其变化   让main不响应事件
    }

    private void initData() {
        lvMain.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, Constant.NAMES) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = null == convertView ? (TextView) super.getView(position, convertView, parent) : convertView;


                //先缩小view
                ViewHelper.setScaleX(view, 0.5f);
                ViewHelper.setScaleY(view, 0.5f);

                //以属性动画放大
                ViewPropertyAnimator.animate(view).scaleX(1).setDuration(350).start();
                ViewPropertyAnimator.animate(view).scaleY(1).setDuration(350).start();
                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                return textView;
            }
        });
        lvMenu.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });

    }

    private void initUi() {
        lvMain = (ListView) findViewById(R.id.main_lv);
        lvMenu = (ListView) findViewById(R.id.menu_lv);
        imagemain= (ImageView) findViewById(R.id.iv_main);
        imagemenu=(ImageView)findViewById(R.id.iv_mennu) ;
        qqDraglayout= (QQDraglayout) findViewById(R.id.activity_main);
        myLinearlayut=(MyLinearlayut)findViewById(R.id.myll);
    }

    //设置当前主界面的监听


}
