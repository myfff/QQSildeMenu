package com.example.administrator.qqsildemenu.Test;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.administrator.qqsildemenu.R;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by Administrator on 2017/3/13.
 */
public class DragLayout extends ViewGroup {
    private View blackView;
    private View blueView;
    private ViewDragHelper viewDragHelper;
    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public DragLayout(Context context) {
        super(context);
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**用于判断是否捕获并解析当前触摸view的事件
         * @param child
         * @param pointerId
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //如果只想让某个孩子移动，就child==blackView
            return true;//返回true表示此View可以捕获相应触摸事件
        }

        /**某个View被捕获和解析的回调
         * @param capturedChild
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            Log.i("tag", "onViewCaptured: " + capturedChild + capturedChild);
        }

        /**此方法是用来控制view移动的范围(最大的左边界)
         * 但实际还未能限制边际
         * 还是要重写，返回的值用于当手指抬的时候计算动画缓慢移动的时间
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        /**并在此处做滑动范围的处理
         * @param child   滑动的view
         * @param left     表示Viewdraghelper认为你想让当前的viewchild移动的距离  left=left+dx
         * @param dx   移动的距离
         * @return 表示最终viewchild的left变得值
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (left < 0) {
                left = 0;
            }
            if (left > getMeasuredWidth() - child.getMeasuredWidth()) {
                left = getMeasuredWidth() - child.getMeasuredWidth();
            }
            return left;//如果不想让其移动就返回left-dx
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (top < 0) {
                top = 0;
            }
            if (top > getMeasuredHeight() - child.getMeasuredHeight()) {
                top = getMeasuredHeight() - child.getMeasuredHeight();
            }
            return top;
        }

        /**当一个View移动的时候让另一个也赶着移动
         * @param changedView  移动的哪个
         * @param left 移动的view的最新left
         * @param top
         * @param dx  移动的x方向的距离
         * @param dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (blueView == changedView) {
                blackView.layout(blackView.getLeft() + dx, blackView.getTop() + dy,
                        blackView.getRight() + dx, blackView.getBottom() + dy);
            } else if (blackView == changedView) {
                blueView.layout(blueView.getLeft() + dx, blueView.getTop() + dy,
                        blueView.getRight() + dx, blueView.getBottom() + dy);
            }

            //在移动的过程中计算出一个偏移量(0 到1)
            float fraction=changedView.getLeft()*1f/(getMeasuredWidth()-changedView.getMeasuredWidth());
            Log.i("fff", "onViewPositionChanged: "+fraction);
            executeAnim(fraction);
        }

        /**手指抬起时的方法
         * @param releasedChild
         * @param xvel   x方向的速度
         * @param yvel
         *
         * 拿中心和left比较，left<center  向左滑动
         *
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int center = getMeasuredWidth() / 2 - releasedChild.getMeasuredWidth();
            if (releasedChild.getLeft() < center) {
                viewDragHelper.smoothSlideViewTo(releasedChild,0,releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);//移动draaglayout里面所有的子对象
            }else {
                viewDragHelper.smoothSlideViewTo(releasedChild,getMeasuredWidth()-releasedChild.getWidth(),releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);//移动draaglayout里面所有的子对象
            }
        }


    };



    /**
     * ViewDragHelper里面封装了Scoller
     */
    @Override
    public void computeScroll() {
        if(viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    }

    /**执行伴随动画
     * @param fraction
     */
    private  void executeAnim(float fraction){
       /* //缩放
        ViewHelper.setScaleY(blackView, 1+0.5f*fraction);
       ViewHelper.setScaleX(blackView,1+0.5f*fraction);*/

        //旋转
        // ViewHelper.setRotation(blackView,360*fraction);
       ViewHelper.setRotationX(blackView,360*fraction);
       ViewHelper.setRotationX(blueView,360*fraction);
        //ViewHelper.setRotationY(blackView,360*fraction);


        //平移
        ViewHelper.setTranslationX(blackView,80*fraction);

        //透明动画
        ViewHelper.setAlpha(blackView,1-fraction);
        ViewHelper.setAlpha(blueView,1-fraction);

        //设置view及背景的颜色变化(使用了一个工具类)
       blackView.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK,Color.RED));
        //设置当前DragViewlayout的
      //  setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction,  Color.GREEN,Color.BLUE));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //让Viewdraghelper判断是否进行拦截
        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);//将触摸事件传给Viewdraghelper进行解析处理
        return true;//消费此事件，不返回给上层
    }

    /**
     * 加载Xml布局结束后，就会知道自己有几个子view
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        blackView = getChildAt(0);
        blueView = getChildAt(1);
    }

    /**
     * 测父控件的子view
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec 当对宽高无特殊要求，我们可以继承已经 实现好的Viewgroup  如framelayout,可以不用重写onmeasure（）方法
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //要测量自己的子view
        //测量规则
        int witdh = (int) getResources().getDimension(R.dimen.width);//得到宽度，也可以在资源文件中红定义

        int measureSque = MeasureSpec.makeMeasureSpec(witdh, MeasureSpec.EXACTLY);//第一个参数blackView.getLayoutParams().width
        blackView.measure(measureSque, measureSque);//设置子View的宽和高
        blueView.measure(measureSque, measureSque);
/*
//当没有特殊的需求可用此方法
        measureChild(blackView,widthMeasureSpec,heightMeasureSpec);
        measureChild(blueView,widthMeasureSpec,heightMeasureSpec);
*/
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();//有可能设置了padding
        int top = getPaddingTop();
        blackView.layout(left, top, left + blackView.getMeasuredWidth(), top + blackView.getMeasuredHeight());
        blueView.layout(left, blackView.getBottom(), left + blueView.getMeasuredWidth(), blackView.getBottom() + blueView.getMeasuredHeight());
    }
}
