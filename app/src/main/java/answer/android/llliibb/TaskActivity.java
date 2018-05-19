package answer.android.llliibb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import answer.android.easyandroid.util.Task;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnTest;
    private LinearLayout linearLayoutConsole;

    private int taskid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        taskid = 0;

        btnTest = findViewById(R.id.btnTest);
        btnTest.setOnClickListener(this);
        linearLayoutConsole = findViewById(R.id.linearLayoutConsole);
    }

    @Override
    public void onClick(View v) {
        long t = System.currentTimeMillis();
        Task.TaskHelper.getInstance(30).run(new Task.ITask() {
            @Override
            public Object getParam() {
                TextView textView =  new TextView(TaskActivity.this);
                textView.setText("任务"+(++taskid)+"-开始");
                linearLayoutConsole.addView(textView);
                return textView;
            }

            @Override
            public Object run(Object param) {
                try {
                    Thread.sleep(Math.round(5000 * Math.random()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return param;
            }

            @Override
            public void afterRun(Object value) {
                TextView textView = (TextView) value;
                textView.setText(textView.getText().toString().replace("开始","完成"));
            }
        });
        // System.out.println("任务发起耗时：" + (System.currentTimeMillis() - t) + "毫秒");
    }
}
