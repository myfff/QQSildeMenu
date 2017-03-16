package com.example.administrator.qqsildemenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.administrator.qqsildemenu.Test.ColorUtil;
import com.example.administrator.qqsildemenu.Test.DragLayout;
import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;
/*在此里面我们只关注两个view的拖拽动画，并不关心实际的view里面的控件的动画，
如果要根据一个view的状态来改变另一个view里面的动画，我们需要用到回调机制*/
/*侧边栏开关*/

/**
 * Created by Administrator on 2017/3/13.
 */

public class QQDraglayout extends FrameLayout {
    private ViewDragHelper viewDragHelper;//对触摸滑动事件的处理
    private View menuview;
    private View mainview;
    private float range;//拖拽的范围
    private float width;


    private FloatEvaluator floatEvaluator;//float的计算器      //执行计算
    private IntEvaluator intEvaluator;//int的计算器

    public QQDraglayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public QQDraglayout(Context context, AttributeSet attrs) {

        super(context, attrs);
        init();
    }

    public QQDraglayout(Context context) {
        super(context);
        init();
    }

    /**
     * 获取当前的侧边栏的状态
     *
     * @return
     */
    public DragState getCurrentState() {
        return currentState;
    }

    /**
     * 此方法会在加载完xml文件后执行，便了得知有几个子view
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //限定只能加载另个子view，否则会报异常
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("此方法只能加载两个子view");
        }
        menuview = getChildAt(0);
        mainview = getChildAt(1);
    }

    /**
     * 执行完onmersure（）后执行此方法
     * 在次里面可以测量自己和子view的宽高
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        range = (float) (width * 0.6);
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
        floatEvaluator = new FloatEvaluator();
        intEvaluator = new IntEvaluator();
    }

    public MenuViewChangedStateLister lister;

    //[1]
    interface MenuViewChangedStateLister {
        void open();

        void close();

        void onDraging(float fraction);
    }

    /**
     * [2]
     * 给外界提供一个设置监听的接口
     *
     * @param lister
     */
    public void setMenuChangedStateLister(MenuViewChangedStateLister lister) {
        this.lister = lister;
    }

    // 通过枚举定义状态常量
    enum DragState {
        Open, Close;
    }

    private DragState currentState = DragState.Close;//当前SlideMenu的状态默认是关闭的


    //重写ViewDragHelper.Callback的回调方法，对触摸事件进行相应的解析及执行
    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mainview || child == menuview;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) range;
        }


        /**控制范围,让其移动，并限制边界
         * @param child
         * @param left
         * @param dx
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mainview) {
                if (left < 0) {
                    left = 0;
                }
                if (left > range) {
                    left = (int) range;
                }
            }
       /* if (child==menuview){
            left=left-dx;//滑动menuview时让其不变
        }*/
            return left;
        }

        /**位置改变，根据动的view去改变其他的
         * @param changedView
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        //此处我们要 固定menuview不变，但是还要让其滑动有效
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == menuview) {
                //前面让menuview 不滑动，所以此时mainview也不会动，我们要在做一次处理

                //固定住让menu不动
                menuview.layout(0, menuview.getTop(), menuview.getRight(), menuview.getBottom());
                //要让mainview移动
                int newleft = mainview.getLeft() + dx;
                //要限制边界
                if (newleft < 0) newleft = 0;
                if (newleft > range) newleft = (int) range;
                mainview.layout(newleft, mainview.getTop() + dy, mainview.getRight() + newleft, mainview.getBottom() + dy);
            }
            if (changedView == mainview) {
                menuview.layout(0, menuview.getTop(), menuview.getRight(), menuview.getBottom());
            }
            //得到一个fraction执行伴随动画
            float fraction = mainview.getLeft() / range;//计算滑动的百分比
            execuanim(fraction);

            //[3]在适当的时候调用
            if (fraction == 0 && currentState != DragState.Close) {
                //为0状态不为关，我们要关闭，更改状态，且回调
                currentState = DragState.Close;
                lister.close();
            }
            if (fraction == 1 && currentState != DragState.Open) {
                currentState = DragState.Open;
                lister.open();
            }
            if (lister != null) {
                lister.onDraging(fraction);
            }

        }


        /**手指抬起做main的滑动方向
         * @param releasedChild
         * @param xvel
         * @param yvel
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            float center = range / 2;
            if (center > mainview.getLeft()) {
                //  mainview.layout(0,mainview.getTop(),mainview.getRight(),mainview.getBottom());
                close();//关闭菜单栏
            } else {
                // mainview.layout((int) range,mainview.getTop(),mainview.getRight(),mainview.getBottom());
                open();
            }
            //处理用户的稍微滑动
            if (xvel > 300 && currentState != DragState.Open) {
                open();
            } else if (xvel < -300 && currentState != DragState.Close) {
                close();
            }
        }
    };

    /**
     * ViewDragHelper里面封装了Scoller
     */
    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(QQDraglayout.this);
        }
    }

    /**
     * //打开菜单栏
     */
    private void open() {
        viewDragHelper.smoothSlideViewTo(mainview, (int) range, mainview.getTop());
        ViewCompat.postInvalidateOnAnimation(QQDraglayout.this);
    }

    /**
     * //关闭菜单栏
     */
    void close() {
        viewDragHelper.smoothSlideViewTo(mainview, 0, mainview.getTop());
        ViewCompat.postInvalidateOnAnimation(QQDraglayout.this);
    }


    /**
     * 根据fraction执行伴随动画
     *
     * @param fraction
     */
    private void execuanim(float fraction) {
        //mainview缩放的大小     此处通过一个类 FloatEvaluator  IntEvaluato完成了缩放，移动，透明度随着滑动比例的变化
        ViewHelper.setScaleX(mainview, floatEvaluator.evaluate(fraction, 1.0f, 0.8f));
        ViewHelper.setScaleY(mainview, floatEvaluator.evaluate(fraction, 1.0f, 0.8f));

        //设置menuview的移动
        ViewHelper.setTranslationX(menuview, intEvaluator.evaluate(fraction, -menuview.getMeasuredWidth() / 2, 0));

        //设置menuview的缩放
        ViewHelper.setScaleX(menuview, floatEvaluator.evaluate(fraction, 0.5f, 1.0f));
        ViewHelper.setScaleY(menuview, floatEvaluator.evaluate(fraction, 0.5f, 1.0f));
        //设置menuview的透明度
        ViewHelper.setAlpha(menuview, floatEvaluator.evaluate(fraction, (int) 0.1f, (int) 1.0f));


        //还有QQDraglayout的背景变化
        //给SlideMenu的背景添加黑色的遮罩效果
        getBackground().setColorFilter(
                (Integer) ColorUtil.evaluateColor(fraction, Color.BLACK, Color.TRANSPARENT),
                PorterDuff.Mode.SRC_OVER);
    }


    //一定要重写此方法

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);//给Viewdraghelper进行处理
        return true;
    }

    /**
     * 让viewdraghelper判断是否拦截
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);//让viewDragHelper判断是否进行拦截
        return result;
    }
}
