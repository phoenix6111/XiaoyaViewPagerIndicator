package com.wanghaisheng.view.viewpagerindicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wanghaisheng.utils.DesityUtils;
import com.wanghaisheng.xiaoyaviewpagerindicator.R;

import java.util.List;

/**
 * Author: sheng on 2016/10/4 20:05
 * Email: 1392100700@qq.com
 */

/**
 * 实现原理：继承自LinnerLayout，在hight的tab横向中心处绘制三角形，根据与ViewPager的联动，scrollTo移动三角形的位置
 */
public class XiaoyaViewPagerIndicator extends LinearLayout {

    //绘制三角形的画笔
    private Paint mPaint;
    //path构成一个三角形
    private Path mTrianglePath;

    //三角形的宽度
    private int mTriangleWidth;
    //三角形的高度
    private int mTriangleHeight;

    //三角形的宽度为单个Tab的1/6
    private static final float TRIANGLE_RADIUS = 1.0f/6;
    //三角形的底边最大宽度
    private int TRIANGLE_MAX_WIDTH;

    //初始时，三角形指示器的偏移量
    private int mInitTranslationX;
    //手指移动时，三角形指示器的偏移量
    private float mTranslationX;

    //可见tab的数量
    private int mVisibleItemCount;
    //默认可见的tab数量
    private static final int DEFAULT_TAB_COUNT = 4;

    //默认文本颜色
    private static final int DEFAULT_NORMAL_COLOR = 0x77ffffff;
    //默认高亮文本颜色
    private static final int DEFAULT_HIGHT_COLOR = 0xffffffff;

    private ViewPager mViewPager;
    private OnPageChangeListener mPageChangeListener;

    /**
     * 设置ViewPager点击事件
     * @param pageChangeListener
     */
    public void setPageChangeListener(OnPageChangeListener pageChangeListener) {
        mPageChangeListener = pageChangeListener;
    }

    public XiaoyaViewPagerIndicator(Context context) {
        this(context,null);
    }

    public XiaoyaViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public XiaoyaViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.XiaoyaViewPagerIndicator);

        mVisibleItemCount = ta.getInt(R.styleable.XiaoyaViewPagerIndicator_visible_item_count,DEFAULT_TAB_COUNT);

        ta.recycle();

        TRIANGLE_MAX_WIDTH = (int) (DesityUtils.getScreenWidth(context)/3*TRIANGLE_RADIUS);

        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#ffffff"));
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.d("tag","onsize changed  "+w);

        mTriangleWidth = (int) (w/mVisibleItemCount*TRIANGLE_RADIUS);
        mTriangleWidth = Math.min(TRIANGLE_MAX_WIDTH,mTriangleWidth);
        mTriangleHeight = mTriangleWidth/2;

        mInitTranslationX = w/mVisibleItemCount/2 - mTriangleWidth/2;

        initTriangle();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if(childCount == 0) {
            return;
        }

        for(int i=0; i<childCount; i++) {
            View child = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) child.getLayoutParams();
            lp.weight = 0;
            lp.width = DesityUtils.getScreenWidth(getContext())/mVisibleItemCount;
            child.setLayoutParams(lp);
        }

        setOnTabClickEvent();
    }

    /**
     * 初始化三角形指示器
     */
    private void initTriangle()
    {
        mTrianglePath = new Path();
        mTrianglePath.moveTo(0, 0);
        mTrianglePath.lineTo(mTriangleWidth, 0);
        mTrianglePath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mTrianglePath.close();
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {

        canvas.save();

        canvas.translate(mInitTranslationX+mTranslationX,getHeight()+2);
        canvas.drawPath(mTrianglePath,mPaint);

        canvas.restore();

        super.dispatchDraw(canvas);
    }

    /**
     * 移动
     * @param position
     * @param offset
     */
    public void scroll(int position,float offset) {
        int tabWidth = getWidth()/mVisibleItemCount;
        mTranslationX = (position+offset)*tabWidth;

        if(position >= mVisibleItemCount-2 && offset>0 && getChildCount()>mVisibleItemCount) {

            if(mVisibleItemCount == 1) {
                scrollTo((int) ((position+offset)*tabWidth),0);
            } else {
                scrollTo((int) ((position-(mVisibleItemCount-2))*tabWidth + offset*tabWidth),0);
            }

        }

        invalidate();
    }

    public void setVisibleItemCount(int itemCount) {
        this.mVisibleItemCount = itemCount;
    }

    /**
     * 设置tab的标题
     * @param titles
     */
    public void setTabTitles(List<String> titles) {

        this.removeAllViews();
        for(String title : titles) {
            TextView tv = generateTextView(title);
            addView(tv);
        }

    }

    private TextView generateTextView(String title) {

        TextView tv = new TextView(getContext());
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = DesityUtils.getScreenWidth(getContext())/mVisibleItemCount;
        tv.setText(title);
        tv.setTextColor(DEFAULT_NORMAL_COLOR);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        tv.setLayoutParams(lp);
        tv.setGravity(Gravity.CENTER);

        return tv;
    }

    /**
     * 设置ViewPager，使Indicator与ViewPager联动
     * @param viewPager
     * @param currentItem
     */
    public void setViewPager(ViewPager viewPager,int currentItem) {
        this.mViewPager = viewPager;

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scroll(position,positionOffset);
                if(mPageChangeListener != null) {
                    mPageChangeListener.onPageScrolled(position,positionOffset,positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if(mPageChangeListener != null) {
                    mPageChangeListener.onPageSelected(position);
                }
                hightTabTextColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(mPageChangeListener != null) {
                    mPageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });

        //设置当前选中的item
        mViewPager.setCurrentItem(currentItem);
        hightTabTextColor(currentItem);
        setOnTabClickEvent();
    }

    /**
     * 重置所有tab 文本的颜色
     */
    private void resetTabTextColor() {
        int childCount = getChildCount();
        for(int i=0; i< childCount; i++) {
            View view = getChildAt(i);
            if(view instanceof TextView) {
                ((TextView) view).setTextColor(DEFAULT_NORMAL_COLOR);
            }
        }
    }

    /**
     * 高亮指定tab的字体颜色
     * @param position
     */
    private void hightTabTextColor(int position) {

        //高度之前先重置文本的颜色
        resetTabTextColor();

        View view = getChildAt(position);
        if(view instanceof TextView) {
            ((TextView) view).setTextColor(DEFAULT_HIGHT_COLOR);
        }
    }

    /**
     * 设置tab点击事件
     */
    private void setOnTabClickEvent() {
        int childCount = getChildCount();
        for(int i=0; i<childCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mViewPager != null) {
                        mViewPager.setCurrentItem(j);
                    }
                }
            });
        }
    }

    public interface OnPageChangeListener {

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }
}
