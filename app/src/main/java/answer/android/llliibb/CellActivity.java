package answer.android.llliibb;

import android.os.Bundle;

import answer.android.easyandroid.activity.EasyAcctivity;

public class CellActivity extends EasyAcctivity{
    @Override
    protected void onCreat(Bundle savedInstanceState) {
        setStatusBarNice();
        setContentView(R.layout.activity_cell);
    }
}
