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
        setStatusBarNice();
        super.onCreate(savedInstanceState);
        onCreat(savedInstanceState);
        ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
        View parentView = contentFrameLayout.getChildAt(0);
        if (parentView != null && Build.VERSION.SDK_INT >= 15) {
            parentView.setFitsSystemWindows(true);
        }
        setToolBar();
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
    private void setStatusBarNice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //Android5.0版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //设置状态栏颜色
                getWindow().setStatusBarColor(Color.TRANSPARENT);
                //设置导航栏颜色
                getWindow().setNavigationBarColor(Color.TRANSPARENT);
            } else {
                //透明状态栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //透明导航栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                // 创建状态栏的管理实例
                // tintManager = new SystemBarTintManager(this);
                // 激活状态栏设置
                // tintManager.setStatusBarTintEnabled(true);

                // 激活导航栏设置
                // tintManager.setNavigationBarTintEnabled(false);
                // 设置状态栏颜色
                // tintManager.setTintResource(Color.TRANSPARENT);
                // 设置导航栏颜色
                // tintManager.setNavigationBarTintResource(Color.TRANSPARENT);
            }
        }
    }

    /**
     * 将布局文件中的ToolBar设置到界面。布局文件中的ToolBar请设置id为toolbar。
     * 可以不在布局文件中使用Toolbar
     */
    private void setToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // 设置toolbar的paddTop让Toolbar不变形
            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //     int padingLeft = toolbar.getPaddingLeft();
            //     int padingTop = getStatusBarHeight();
            //     int padingRight = toolbar.getPaddingRight();
            //     int padingBottom = toolbar.getPaddingBottom();
            //     toolbar.setPadding(padingLeft, padingTop, padingRight, padingBottom);
            // }
        }
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    protected int getStatusBarHeight() {
        return Utils.UI.getStatusBarHeight(this);
    }

    /**
     * 获取导航栏高度
     *
     * @return
     */
    protected int getNativationHeight() {
        return Utils.UI.getNavigationBarHeight(this);
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
     * @param title
     * @param msg
     * @param onClickListener
     * @return
     */
    protected AlertDialog alert(String title, String msg, DialogInterface.OnClickListener onClickListener) {
        return Utils.UI.alert(this, title, msg, onClickListener);
    }
}
