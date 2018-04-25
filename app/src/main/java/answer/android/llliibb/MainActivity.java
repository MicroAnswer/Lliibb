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
    private Cell cell;
    private Cell seeMoreCell;

    private ExpandView moreExpand;
    private ImageView seeMoreIconImageView;

    @Override
    protected void onCreat(Bundle savedInstanceState) {
        setStatusBarNice();
        setContentView(R.layout.activity_main);

        cell = findViewById(R.id.cell);
        cell.setOnClickListener(this);

        moreExpand = findViewById(R.id.moreSet);
        moreExpand.setOnExpanListener(this);

        seeMoreIconImageView = findViewById(R.id.seeMoreIconImageView);

        seeMoreCell = findViewById(R.id.seeMoreCell);
        seeMoreCell.setOnClickListener(this);


        picLock = findViewById(R.id.picLock);
        picLock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == seeMoreCell){
            moreExpand.toggle();
        } else if (v == picLock) {
            // 跳转九宫格解锁界面
            startActivity(new Intent(this,PicLockActivity.class));
        } else if (v == cell) {
            // 跳转到 cell 控件demo界面
            startActivity(new Intent(this, CellActivity.class));
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
