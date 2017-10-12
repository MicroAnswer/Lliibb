package answer.android.llliibb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import answer.android.views.ExpandView;

public class MainActivity extends AppCompatActivity implements ExpandView.OnExpanListener {


    ExpandView expandView;
    private ProgressBar progressBar;
    Button ss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expandView = (ExpandView) findViewById(R.id.asdasd);
        expandView.setOnExpanListener(this);
        ss = (Button) findViewById(R.id.ss);
        progressBar = (ProgressBar) findViewById(R.id.progress);
    }

    public void togg(View a) {
        expandView.toggle();
    }

    @Override
    public void onToggled(ExpandView expandView, boolean isOpen) {
        if (isOpen) {
            ss.setText("关闭");
        } else {
            ss.setText("展开");
        }
    }

    @Override
    public void onToggleing(ExpandView expandView, float percent) {
        progressBar.setProgress(Math.round(percent * 100));
    }
}
