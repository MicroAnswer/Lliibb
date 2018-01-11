package answer.android.easyandroid.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * 工具类
 * Created by Microanswer on 2018/1/11.
 */

public class Utils {

    /**
     * 视图相关工具类
     */
    public static class UI {
        /**
         * 获取状态栏高度
         *
         * @param context
         * @return
         */
        public static int getStatusBarHeight(Context context) {
            int result = 0;
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
            return result;
        }

        /**
         * 将dp转化为px
         *
         * @param dp
         * @return
         */
        public static int dp2px(Context context, int dp) {
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()));
        }

        /**
         * 将px 转化为dp
         *
         * @param context
         * @param px
         * @return
         */
        public static int px2dp(Context context, int px) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }
    }

}
