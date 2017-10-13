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

    private boolean fromuser; // 标记是用户操作

    private Scroller scroller;

    private OnExpanListener onExpanListener;

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
            if (unExpanHeight <= 0) {
                // 传递的值过小,
                unExpanHeight = 0;
            }
            animTime = typedArray.getInteger(R.styleable.ExpandView_animTime, animTime);
            if (animTime < 1) {
                animTime = 400;
            }
            typedArray.recycle();
        }

    }

    /**
     * 根据最小高度的设定,计算出View应该占据的高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 测量规则:
         *  宽度直接使用子View的宽度,
         *  高度:
         *      如果子View的高度小于收索状态的高度 --> 使用View的高度
         *      如果子View的高度大于收缩状态的高度 --> 使用设定的最小高度
         */

        View child = getAndCheckChildView(); // 获取到子View.

        if (opening || closeing) {
            setMeasuredDimension(getMeasuredWidth(), layoutHeight);
            if (layoutHeight == 0 && closeing && scroller != null) {
                // 当0被设置为测量结果时，不会调用draw方法, 也就不会再computescroll什么的了。此处手动调用
                closeing = false;
                // Log.i("ExpandView", "sd" + String.valueOf(layoutHeight));
                opening = false;
                fromuser = false;
                scroller.abortAnimation();
                scroller.forceFinished(true);
                computeScroll();
            }
            return;
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        child.measure(MeasureSpec.makeMeasureSpec(widthSize - getPaddingLeft() - getPaddingRight(), MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        int childWidth = widthSize;

        childHeight = child.getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
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
        // Log.i("ExpandView", "l=" + l + ",t=" + t + ",r=" + r + ",b=" + b);

        if (childView != null) {
            int h = getMeasuredHeight() + getPaddingBottom() + getPaddingTop();
            childView.layout(getPaddingLeft(), getPaddingTop(), r - getPaddingRight(), h);
        } else {
            Log.w("ExpandView", "ExpandView has no child.");
        }
    }

    /**
     * 展开，只有在控件是关闭的状态有用
     */
    public boolean expan() {
        if (!isExpan) {
            return toggle();
        }
        return false;
    }

    /**
     * 收缩， 只有在控件是展开状态可用
     */
    public boolean unExpan() {
        if (isExpan) {
            return toggle();
        }
        return false;
    }

    public boolean close() {
        return isExpan && toggle(false);
    }

    public boolean open() {
        return !isExpan && toggle(false);
    }

    public boolean toggle() {
        return toggle(true);
    }

    public boolean toggle(boolean b) {
        // 如果不是用动画，直接变化
        if(!b) {
            if(isExpan) {
                closeing = true;
                opening = false;
                layoutHeight = unExpanHeight;
            } else {
                closeing = false;
                opening = true;
                layoutHeight = childHeight;
            }
            isExpan = !isExpan;
            requestLayout();
            invalidate();
            if(onExpanListener!=null){
                onExpanListener.onToggled(this, isExpan);
            }
            return true;
        }

        if (closeing || opening) {
            // Log.i("ExpandView", "正在动作中。。。");
            return false; // 正在打开或者关闭的时候不进行操作
        }

        if (childHeight < unExpanHeight) {
            Log.i("ExpandView", "子控件小于最小高度");
            // 子试图高度小于最小高度
            return false;
        }
        if (scroller == null) {
            scroller = new Scroller(getContext());
        }
        scroller.forceFinished(true);
        scroller.abortAnimation();
        if (isExpan) {
            // 如果是打开的,就要缩小,
            closeing = true;
            opening = false;
            scroller.startScroll(getMeasuredHeight(), 0, -(getMeasuredHeight() - unExpanHeight), 0, animTime);
            // Toast.makeText(getContext(), "收缩", Toast.LENGTH_SHORT).show();
        } else {
            closeing = false;
            opening = true;
            scroller.startScroll(getMeasuredHeight(), 0, childHeight - getMeasuredHeight(), 0, animTime);
            // Log.i("ExpandView", String.valueOf("" + getMeasuredHeight() + 0 + (childHeight - getMeasuredHeight()) + 0 + animTime));
            // Toast.makeText(getContext(), "展开", Toast.LENGTH_SHORT).show();
        }
        // Log.i("ExpandView", "开始动作");
        isExpan = !isExpan;

        fromuser = true;
        forceLayout();
        requestLayout();
        invalidate();
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        // Log.i("ExpandView", "computeScroll:" + scroller);
        if (null == scroller) {
            return;
        }
        if (fromuser) {
            boolean isScrolling = scroller.computeScrollOffset();
            // Log.i("ExpandView", "isflish:" + String.valueOf(scroller.getFinalX() + " : " + scroller.getCurrX()));
            if (scroller.getFinalX() == scroller.getCurrX()) {
                // Log.i("ExpandView", "完成");
                fromuser = false;
                closeing = false;
                opening = false;
                if (onExpanListener != null) {
                    onExpanListener.onToggled(this, isExpan);
                }
            } else {
                layoutHeight = scroller.getCurrX();
                forceLayout();
                invalidate();
                requestLayout();
                float p = (layoutHeight - unExpanHeight) / (float) (childHeight - unExpanHeight);
                // Log.i("ExpandView", "p"+String.valueOf(p));
                if (onExpanListener != null) {
                    onExpanListener.onToggleing(this, p);
                }
            }
        } else {
            opening = false;
            closeing = false;
        }
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

    public OnExpanListener getOnExpanListener() {
        return onExpanListener;
    }

    public void setOnExpanListener(OnExpanListener onExpanListener) {
        this.onExpanListener = onExpanListener;
    }

    private float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }


    public interface OnExpanListener {
        /**
         * 控件从关闭状态改变到开启状态，开启完成后回调
         * 控件从开启状态改变到关闭状态，关闭完成后回调
         * <p>
         * 总之， 展开完成调，收缩完成也回调
         *
         * @param expandView
         * @param isOpen     展开、收拢状态
         */
        void onToggled(ExpandView expandView, boolean isOpen);

        /**
         * 控件在打开或关闭的过程中回调。
         *
         * @param expandView
         * @param percent    当前打开的高度 除以 总高度
         */
        void onToggleing(ExpandView expandView, float percent);
    }
}
