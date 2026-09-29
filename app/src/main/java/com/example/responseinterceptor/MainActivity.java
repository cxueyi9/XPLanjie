package com.example.responseinterceptor;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setText("Response Interceptor 模块已安装。\n\n" +
                "请在 LSPosed 管理器中启用本模块，\n" +
                "并勾选目标应用的作用域。");
        tv.setPadding(48, 48, 48, 48);
        tv.setTextSize(16);
        setContentView(tv);
    }
}
