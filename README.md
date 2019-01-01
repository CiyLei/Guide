# Guide
基于注解的引导图层框架

``` java
    @GuideBindView(value = "这是一个Textview1")
    TextView tv1;
    @GuideBindView(value = "这是一个Textview2")
    TextView tv2;
    @GuideBindView(value = "这是一个Textview3")
    TextView tv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);

        tv3.post(new Runnable() {
            @Override
            public void run() {
                MainActivity$$GuideInject.show(MainActivity.this);
            }
        });
     }
```
