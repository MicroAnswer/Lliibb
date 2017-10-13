package answer.android.llliibb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import answer.android.views.ExpandView;

public class MainActivity extends AppCompatActivity {


    private ListView listView;
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listview);
        layoutInflater = LayoutInflater.from(this);
        listView.setAdapter(new Adap());
    }


    private class Adap extends BaseAdapter {


        @Override
        public int getCount() {
            return 200;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (null == view) {
                view = layoutInflater.inflate(R.layout.list_item, null);
            }

            TextView v = view.findViewById(R.id.textivew);
            v.setText("条目" + i);
            final Button btn = view.findViewById(R.id.btn);
            final ExpandView expandView = view.findViewById(R.id.expandview);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View views) {
                    // btn.setEnabled(false);
                    boolean toggle = expandView.toggle();
                    Log.i("ExpandView", String.valueOf("开始：" + toggle));
                }
            });
            expandView.setOnExpanListener(new ExpandView.OnExpanListener() {
                @Override
                public void onToggled(ExpandView expandView, boolean isOpen) {
                    btn.setEnabled(true);
                    Log.i("ExpandView", "完成");
                }

                @Override
                public void onToggleing(ExpandView expandView, float percent) {
                    btn.setRotation((-180 * percent));
                }
            });
            return view;
        }
    }
}
