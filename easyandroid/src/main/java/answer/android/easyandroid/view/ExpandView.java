package answer.android.easyandroid.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import answer.android.easyandroid.R;

/**
 * 可伸缩View, 这是一个ViewGroup,只能容纳一个子View
 * Created by Microanswer on 2017/10/12.
 */

public class ExpandView extends ViewGroup {
    public static final int STATUS_OPENED = 3;
    public static final int STATUS_OPENING = 2;
    public static final int STATUS_CLOSED = 1;
    public static final int STATUS_CLOSING = 0;

    private int status = STATUS_CLOSED; // 标记展开状态

    private int closedHeight; // 未展开的时候的显示高度

    private int openedHeight = -1; // 展开完成后的高度，通常不需要设置，自动通过子控件识别高度。

    private int layoutHeight; // 当前应该显示的高度，这个值会在展开收缩过程中变化的

    private int animTime; // 展开关闭的执行时间，单位（毫秒）

    private Scroller scroller; // 滚动器

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
        closedHeight = Math.round(dp2px(context, 70f));

        // 初始化默认动画时间
        animTime = 400;

        // 如果有自定义的值,使用它
        if (attributeSet != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.ExpandView);
            closedHeight = typedArray.getDimensionPixelSize(R.styleable.ExpandView_unexpanHeight, closedHeight);
            if (closedHeight <= 0) {
                // 传递的值过小,
                closedHeight = 0;
            }
            animTime = typedArray.getInteger(R.styleable.ExpandView_animTime, animTime);
            if (animTime < 0) { // 设置的值太小， 重新设置为0
                animTime = 0;
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
        // 获取控件应该的绘制宽度
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        if (status != STATUS_CLOSING && status != STATUS_OPENING) {

            /*
             * 测量规则:
             *  宽度直接使用子View的宽度,
             *  高度:
             *      如果子View的高度小于收索状态的高度 --> 使用View的高度
             *      如果子View的高度大于收缩状态的高度 --> 使用设定的最小高度
             */

            View child = getAndCheckChildView(); // 获取到子View.


            // 计算子控件的绘制高度
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            openedHeight = child.getMeasuredHeight();

            int height;
            if (status == STATUS_CLOSED) {
                // 在关闭的情况下，关闭时的高度如果大于了实际子控件的高度，此时让关闭时的高度等于实际子控件的高度
                if (closedHeight >= openedHeight) {
                    height = openedHeight;
                } else {
                    height = closedHeight;
                }
            } else {
                height = openedHeight;
            }
            layoutHeight = height;
        } else {

            // 原本这里可以什么都不用处理的， 但是有个问题
            // Android 在绘制的时候，你要是把Measure高度设置为了0， 后续就不会继续layout也不会draw了
            // 所以，在收缩完成后如果高度是0的时候，为了保证scroller流程的正常结束，
            // 这里把其值设为1

            if (layoutHeight == 0) {
                layoutHeight = 1;
            }
        }
        setMeasuredDimension(widthSize, layoutHeight);
    }

    @Override
    protected void onLayout(boolean bo, int l, int t, int r, int b) {
        // System.out.println("layout");

        // 检测子控件是否只是1个，然后返回这一个
        View childView = getAndCheckChildView();

        if (childView != null) {
            // 始终使用子控件能完全正常绘制的高度对子控件进行绘制，以免子控件在绘制过程中变形。
            childView.layout(getPaddingLeft(), getPaddingTop(), r - getPaddingRight(), openedHeight);
        } else {
            // 没有子控件，使用这个ExpandView是没有多大意义的，打印一个警告信息。
            Log.w("ExpandView", "ExpandView has no child.");
        }
    }

    /**
     * 是否展开
     *
     * @return true， 未展开 = false
     */
    public boolean isOpened() {
        return status == STATUS_OPENED;
    }

    /**
     * 是否展开
     *
     * @return true， 未展开 = false
     */
    public boolean isExpan() {
        return isOpened();
    }

    public int getStatus() {
        return status;
    }

    // 展开关闭操作
    public void toggle() {

        if (status == STATUS_CLOSING || status == STATUS_OPENING) {
            return; // 正在打开或者关闭的时候不进行操作
        }

        if (openedHeight < closedHeight) {
            Log.i("ExpandView", "子控件小于最小高度"); // 子控件高度小于最小高度，展开操作不存在了。
            return;
        }

        // 初始化滚动器
        if (scroller == null) scroller = new Scroller(getContext());

        // 结束上一次的滚动
        // scroller.forceFinished(true);
        // scroller.abortAnimation();

        if (status == STATUS_CLOSED) { // 当前状态为关闭，执行展开操作
            status = STATUS_OPENING;
            int distance = openedHeight - closedHeight;
            scroller.startScroll(0, closedHeight, 200, distance, animTime);
            // System.out.println("start opening:" + distance);
            _relayout();
        } else if (status == STATUS_OPENED) { // 当前状态时展开, 执行关闭操作
            status = STATUS_CLOSING;
            int distance = -(openedHeight - closedHeight);
            scroller.startScroll(0, openedHeight, -200, distance, animTime);
            // System.out.println("start closing:" + distance);
            _relayout();
        } else {
            System.out.println("非法状态");
        }
    }

    private void _relayout() {
        requestLayout(); // 请求视图大小重绘
        invalidate(); // 请求draw方法重绘，应为scroller的使用必须调起draw方法才能正常，所以这儿调起 invalidate 方法，让draw得到执行
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (null == scroller) {
            return;
        }

        boolean isScrolling = scroller.computeScrollOffset();
        boolean isFinished = scroller.isFinished();

        // System.out.println("layoutheight=" + layoutHeight + "\t, scroller:isFinished=" + isFinished + "\t, isScrolling=" + isScrolling);

        if (isScrolling) {
            // 在 scroller 中，通常的使用时对 x，y 坐标的滚动变化，这里我们是对高度的滚动，
            // 将 scroller 中的 y 值 替代为我们要使用的“高度”来使用。
            layoutHeight = scroller.getCurrY();

            _relayout();
            // 计算展开比率 0~1
            float p = (layoutHeight - closedHeight) / (float) (openedHeight - closedHeight);

            // 回调
            if (onExpanListener != null) {
                onExpanListener.onToggleing(this, p);
            }
        } else {
            // 滚动完成
            // System.out.println("scroll complete-----------------------------");
            if (status == STATUS_OPENING) {
                status = STATUS_OPENED;
            } else if (status == STATUS_CLOSING) {
                status = STATUS_CLOSED;

                layoutHeight = closedHeight;
                _relayout();
            }
            // 回调
            if (onExpanListener != null) {
                onExpanListener.onToggleing(this, isExpan() ? 1f : 0f);
            }
        }
    }

    /**
     * 获取并检查子View
     *
     * @return 返回子控件
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
