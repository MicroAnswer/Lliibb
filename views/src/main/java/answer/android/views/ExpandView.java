package answer.android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 可伸缩View, 这是一个ViewGroup,只能容纳一个子View
 * Created by Microanswer on 2017/10/12.
 */

public class ExpandView extends ViewGroup {

    private boolean isExpan = false; // 标记是否展开
    private boolean closeing = false; // 是否正在关闭
    private boolean opening = false; // 是否正在打开
    private int unExpanHeight;
    private int childHeight;
    private int layoutHeight;
    private int animTime = 400; // ms

    private Scroller scroller;

    public ExpandView(Context context) {
        super(context);
        init(context, null);
    }

    public ExpandView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExpandView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 够着函数初始化
     *
     * @param context
     * @param attributeSet
     */
    private void init(Context context, AttributeSet attributeSet) {

        // 初始化未展开的时候View高度
        unExpanHeight = Math.round(dp2px(context, 70f));

        // 如果有自定义的值,使用它
        if (attributeSet != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ExpandView);
            unExpanHeight = typedArray.getDimensionPixelSize(R.styleable.ExpandView_unexpanHeight, unExpanHeight);
            typedArray.recycle();
        }

        scroller = new Scroller(context);

        // setOnClickListener(this);
    }

    /**
     * 根据最小高度的设定,计算出View应该占据的高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        /**
         * 测量规则:
         *  宽度直接使用子View的宽度,
         *  高度:
         *      如果子View的高度小于收索状态的高度 --> 使用View的高度
         *      如果子View的高度大于收缩状态的高度 --> 使用设定的最小高度
         */

        View child = getAndCheckChildView(); // 获取到子View.

        child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int childWidth = child.getMeasuredWidth();

        if (opening || closeing) {
            setMeasuredDimension(childWidth, layoutHeight);
            return;
        }

        childHeight = child.getMeasuredHeight();
        int height = unExpanHeight;
        if (!isExpan) {
            if (childHeight > unExpanHeight) {
                height = unExpanHeight;
            }
        } else {
            height = childHeight;
        }

        layoutHeight = height;

        setMeasuredDimension(childWidth, layoutHeight);
    }

    /**
     * 是否展开
     *
     * @return
     */
    public boolean isExpan() {
        return isExpan;
    }

    @Override
    protected void onLayout(boolean bo, int l, int t, int r, int b) {

        View childView = getAndCheckChildView();

        if (childView != null) {
            childView.layout(0, 0, r, t + getMeasuredHeight());
        } else {
            Log.w("ExpandView", "ExpandView has no child.");
        }
    }

    public void expan() {
        if (!isExpan) {
            toggle();
        }
    }

    public void unExpan() {
        if (isExpan) {
            toggle();
        }
    }

    public void toggle() {
        if (closeing || opening) {
            // Log.i("ExpandView", "正在动作中。。。");
            return; // 正在打开或者关闭的时候不进行操作
        }

        if (childHeight < unExpanHeight) {
            // Log.i("ExpandView", "子控件小于最小高度");
            // 子试图高度小于最小高度
            return;
        }

        if (isExpan) {
            // 如果是打开的,就要缩小,
            closeing = true;
            opening = false;
            scroller.startScroll(getMeasuredHeight(), 0, -(getMeasuredHeight() - unExpanHeight), 0,animTime);
            // Toast.makeText(getContext(), "收缩", Toast.LENGTH_SHORT).show();
        } else {
            closeing = false;
            opening = true;
            scroller.startScroll(getMeasuredHeight(), 0, childHeight - getMeasuredHeight(), 0,animTime);
            // Toast.makeText(getContext(), "展开", Toast.LENGTH_SHORT).show();
        }
        // Log.i("ExpandView", "开始i动作");
        isExpan = !isExpan;
        requestLayout();
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            layoutHeight = scroller.getCurrX();
            requestLayout();
            invalidate();
            if (scroller.isFinished()) {
                // Log.i("ExpandView", "完成22");
                closeing = false;
                opening = false;
            }
        } else {
            // Log.i("ExpandView", "完成");
            closeing = false;
            opening = false;
        }
        super.computeScroll();
    }

    /**
     * 获取并检查子View
     *
     * @return
     */
    private View getAndCheckChildView() {

        int childCount = getChildCount();

        if (childCount > 1) {
            throw new RuntimeException("ExpandView can only hold 1 view.");
        }

        View view = null;

        if (childCount == 1) {
            view = getChildAt(0);
        }

        return view;
    }


    private float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

}
