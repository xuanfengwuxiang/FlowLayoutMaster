package com.xuanfeng.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 流式布局
 */
public class FLowLayout extends ViewGroup {

    public FLowLayout(Context context) {
        this(context, null);
    }

    public FLowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FLowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        super.generateLayoutParams(p);
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int totalWidth = 0;
        int totalHeight = 0;
        int lineHeightMax = 0;
        int lineWidth = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            int childHeight = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;

            if ((lineWidth + childWidth) > widthSize) {
                //新起一行之前更新总宽高
                totalWidth = Math.max(totalWidth, lineWidth);
                totalHeight += lineHeightMax;

                //新起一行
                lineWidth = childWidth;
                lineHeightMax = childHeight;
            } else {
                //同行宽累加、高对比。
                lineWidth += childWidth;
                lineHeightMax = Math.max(lineHeightMax, childHeight);
            }

            //最后一行计算
            if (i == getChildCount() - 1) {
                totalWidth = Math.max(totalWidth, lineWidth);
                totalHeight += lineHeightMax;
            }
        }
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : totalWidth,
                heightMode == MeasureSpec.EXACTLY ? heightSize : totalHeight);
    }

    private List<List<View>> mAllViews = new ArrayList<>();
    private List<Integer> mLineHeightMax = new ArrayList<>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeightMax.clear();

        int lineHeightMax = 0;
        int lineWidth = 0;
        List<View> lineViews = new ArrayList<>();

        for (int i = 0; i < getChildCount(); i++) {

            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (lineWidth + childWidth > getWidth()) {
                mAllViews.add(lineViews);
                mLineHeightMax.add(lineHeightMax);

                //新起一行
                lineWidth = 0;
                lineHeightMax = 0;
                lineViews = new ArrayList<>();
            }

            lineWidth += childWidth;
            lineHeightMax = Math.max(lineHeightMax, childHeight);
            lineViews.add(child);


            //最后一行计算
            if (i == getChildCount() - 1) {
                mAllViews.add(lineViews);
                mLineHeightMax.add(lineHeightMax);
            }
        }


        int left = 0;
        int top = 0;
        for (int i = 0; i < mAllViews.size(); i++) {

            lineViews = mAllViews.get(i);
            for (int j = 0; j < lineViews.size(); j++) {

                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                int childLeft = left + lp.leftMargin;
                int childTop = top + lp.topMargin;
                int childRight = childLeft + child.getMeasuredWidth();
                int childBottom = childTop + child.getMeasuredHeight();
                child.layout(childLeft, childTop, childRight, childBottom);
                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }

            left = 0;
            top += mLineHeightMax.get(i);
        }

    }
}
