package answer.android.easyandroid.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import answer.android.easyandroid.R;
import answer.android.easyandroid.util.Utils;

/**
 * 别问这个activity有啥用。 它就是一个基类Activity。继承它就好。
 * Created by Microanswer on 2018/1/10.
 */

public abstract class EasyAcctivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreat(savedInstanceState);
    }

    /**
     * 实现此方法， 就像你实现onCreate一样的去写代码。
     *
     * @param savedInstanceState
     */
    protected abstract void onCreat(Bundle savedInstanceState);

    /**
     * 适配沉浸式状态栏
     */
    protected void setStatusBarNice() {
        Window window = getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // android 版本大于 19， 使用透明状态栏和导航栏，view会自动填充到全屏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // android 版本大于 21，透明变成了半透明，想别的办法让导航栏透明。

                // 先不让状态栏和导航栏透明了， 让它可以画颜色，后台再画一个透明颜色上去
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

                // 让view全屏
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

                // 绘制导航栏和状态栏颜色
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                // 绘制透明色，以达到导航栏和状态栏透明的目的
                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);
            }
        }
    }

    /**
     * 弹出警告框
     *
     * @param msg
     * @return
     */
    protected AlertDialog alert(String msg) {
        return alert("提示", msg);
    }

    /**
     * 弹出警告框
     *
     * @param title
     * @param msg
     * @return
     */
    protected AlertDialog alert(String title, String msg) {
        return alert(title, msg, null);
    }

    /**
     * 弹出警告框
     *
     * @param title
     * @param msg
     * @param onClickListener
     * @return
     */
    protected AlertDialog alert(String title, String msg, DialogInterface.OnClickListener onClickListener) {
        return Utils.UI.alert(this, title, msg, onClickListener);
    }
}
