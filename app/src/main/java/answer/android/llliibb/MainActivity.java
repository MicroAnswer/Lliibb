package answer.android.llliibb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import answer.android.views.ExpandView;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void togg(View a) {
        ExpandView expandView = (ExpandView) findViewById(R.id.asdasd);
        expandView.toggle();
        Button ss = (Button) a;
        if(expandView.isExpan()) {
            ss.setText("关闭");
        } else {
            ss.setText("展开");
        }
    }
}
