package answer.android.llliibb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import answer.android.easyandroid.activity.EasyAcctivity;
import answer.android.easyandroid.util.Utils;
import answer.android.easyandroid.view.Cell;
import answer.android.easyandroid.view.ExpandView;

public class MainActivity extends EasyAcctivity implements View.OnClickListener, ExpandView.OnExpanListener {

    private Cell picLock;
    private Cell seeNavigationHeightCell;
    private Cell aboutCell;
    private Cell seeMoreCell;

    private ExpandView moreSet;
    private ImageView seeMoreIconImageView;

    @Override
    protected void onCreat(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        moreSet = findViewById(R.id.moreSet);
        moreSet.setOnExpanListener(this);
        seeMoreIconImageView = findViewById(R.id.seeMoreIconImageView);
        seeMoreCell = findViewById(R.id.seeMoreCell);
        seeNavigationHeightCell = findViewById(R.id.seeNavigationHeightCell);
        aboutCell = findViewById(R.id.aboutCell);
        seeMoreCell.setOnClickListener(this);
        seeNavigationHeightCell.setOnClickListener(this);
        aboutCell.setOnClickListener(this);

        picLock = findViewById(R.id.picLock);
        picLock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == seeNavigationHeightCell) {
            alert("" + Utils.UI.getNavigationBarHeight(this));
        } else if (aboutCell == v) {
            alert("你好，这是一个案列App。不是设置");
        } else if (v == seeMoreCell){
            moreSet.toggle();
        } else if (v == picLock) {
            // 跳转九宫格解锁界面
            startActivity(new Intent(this,PicLockActivity.class));
        }
    }

    @Override
    public void onToggled(ExpandView expandView, boolean isOpen) {
        if (isOpen) {
            seeMoreIconImageView.setRotation(90);
        } else {
            seeMoreIconImageView.setRotation(0);
        }
    }

    @Override
    public void onToggleing(ExpandView expandView, float percent) {
        seeMoreIconImageView.setRotation(90 * percent);
    }
}
