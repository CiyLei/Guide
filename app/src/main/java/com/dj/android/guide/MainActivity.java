package com.dj.android.guide;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dj.android.annotation.GuideBindView;
import com.dj.android.library.DefaultGuideView;
import com.dj.android.library.Guide;
import com.dj.android.library.GuideManager;
import com.dj.android.guide.MainActivity$$GuideInject;
import com.dj.android.library.GuideView;

import java.lang.reflect.InvocationTargetException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @GuideBindView(id = 1,value = "这是一个Textview12321213212121331224343243215132534534543543325324532454322532453245342532453245324534253245324")
    TextView tv;
    @GuideBindView(id = 2,value = "这是一个Button", guideView = "com.dj.android.guide.MyGuideView")
    Button btn;
    @GuideBindView(value = "这是一个Textview1", guideView = "com.dj.android.guide.MyGuideView")
    TextView tv1;
    @GuideBindView(value = "这是一个Textview2", guideView = "com.dj.android.guide.MyGuideView")
    TextView tv2;
    @GuideBindView(value = "这是一个Textview3", guideView = "com.dj.android.guide.MyGuideView")
    TextView tv3;
    @GuideBindView(value = "这是一个Textview4")
    TextView tv4;
    @GuideBindView(value = "这是一个Textview5")
    TextView tv5;
    @GuideBindView(value = "这是一个ContentView")
    View contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        tv = findViewById(R.id.tv);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        contentView = getWindow().getDecorView();

        tv5.post(new Runnable() {
            @Override
            public void run() {
                MainActivity$$GuideInject.show(MainActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity$$GuideInject.show(MainActivity.this, listener, 8, DefaultGuideView.class);
            }
        });
    }

    private GuideManager.Listener listener = new GuideManager.Listener() {
        @Override
        public void onBefore(int id) {
            if (id == 2) {
                tv1.setText(Math.random() + "");
            }
        }

        @Override
        public void onFinsh() {
            Toast.makeText(MainActivity.this, "onFinsh", Toast.LENGTH_SHORT).show();
        }
    };

    public void test(Class<? extends GuideView> guideViewClass) {
        try {
            GuideView guideView = guideViewClass.getConstructor(int.class, View.class, String.class).newInstance(0, null, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Guide.Builder().add()
    }
}
