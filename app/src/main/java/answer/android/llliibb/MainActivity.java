package answer.android.llliibb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import answer.android.easyandroid.activity.EasyAcctivity;
import answer.android.easyandroid.util.Utils;
import answer.android.easyandroid.view.Cell;

public class MainActivity extends EasyAcctivity implements View.OnClickListener {
    private Cell picLock;
    private Cell seeNavigationHeightCell;

    @Override
    protected void onCreat(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        seeNavigationHeightCell = findViewById(R.id.seeNavigationHeightCell);
        seeNavigationHeightCell.setOnClickListener(this);

        picLock = findViewById(R.id.picLock);
        picLock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == seeNavigationHeightCell) {
            alert("" + Utils.UI.getNavigationBarHeight(this));
        } else if (v == picLock) {
            // 跳转九宫格解锁界面
            startActivity(new Intent(this,PicLockActivity.class));
        }
    }

}
