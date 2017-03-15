package com.example.administrator.qqsildemenu;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/3/14.
 */
/*自定义当侧边栏个开时，不让main相应触摸事件，将事件拦截
* 要让侧边栏传进来并且它的状态也闯进来
* 我们根据侧边栏不为空和其状态为打开时进行拦截*/

public class MyLinearlayut  extends LinearLayout{

    public MyLinearlayut(Context context) {
        super(context);
    }

    public MyLinearlayut(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyLinearlayut(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    private  QQDraglayout qqDraglayout;
    public void setSlideMenu(QQDraglayout qqDraglayout){
     this.qqDraglayout=qqDraglayout;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(qqDraglayout!=null && qqDraglayout.getCurrentState()== QQDraglayout.DragState.Open){
            //如果slideMenu打开则应该拦截并消费掉事件
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(qqDraglayout!=null && qqDraglayout.getCurrentState()== QQDraglayout.DragState.Open){
            if(event.getAction()==MotionEvent.ACTION_UP){
                //抬起则应该关闭slideMenu
                qqDraglayout.close();
            }

            //如果slideMenu打开则应该拦截并消费掉事件
            return true;
        }
        return super.onTouchEvent(event);

    }
}
