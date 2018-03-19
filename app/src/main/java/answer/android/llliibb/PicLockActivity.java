package answer.android.llliibb;

import android.os.Bundle;

import java.util.ArrayList;

import answer.android.easyandroid.activity.EasyAcctivity;
import answer.android.easyandroid.view.PicLock;

/**
 * Created by Microanswer on 2018/3/19.
 */

public class PicLockActivity extends EasyAcctivity {

    private PicLock picLock;
    private ArrayList<PicLock.Dot> trues;

    @Override
    protected void onCreat(Bundle savedInstanceState) {
        setContentView(R.layout.activity_piclock);
        picLock = findViewById(R.id.picLock);
        picLock.setOnResultListener(new OnPicLockResult());

        trues = new ArrayList<>();
        trues.add(picLock.newDot("00"));
        trues.add(picLock.newDot("10"));
        trues.add(picLock.newDot("01"));
        trues.add(picLock.newDot("11"));
        trues.add(picLock.newDot("21"));

    }

    private class OnPicLockResult extends PicLock.OnResultListener {

        public boolean onResult(ArrayList<PicLock.Dot> dots) {
            if (dots.size() != trues.size()) {
                return false;
            }
            for (int index = 0; index < dots.size(); index++) {
                PicLock.Dot d = dots.get(index);
                PicLock.Dot d2 = trues.get(index);
                boolean equals = d.getData().equals(d2.getData());
                if (!equals) {
                    return false;
                }
            }
            return true;
        }
    }
}
