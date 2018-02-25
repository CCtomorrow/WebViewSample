package com.mmc.webview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleSchemeLaunch(getIntent());

        Button vlocal = findViewById(R.id.local_html);
        vlocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.setData(Uri.parse("file:///android_asset/java_js.html"));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleSchemeLaunch(intent);
    }

    /**
     * 这里处理使用scheme打开的情况
     * <p>
     * 说明:
     * 1.使用scheme可以打开任意在Manifest里面配置了一些相关信息的页面，那个页面不一定需要有WebView
     * 2.一般在把首页配置成可以使用scheme打开的页面，然后在首页统一分发处理
     * <p>
     * 当前页面在Manifest里面的配置:
     * <p>
     * <p>
     * <intent-filter>
     * <action android:name="android.intent.action.VIEW"/>
     * <p>
     * <category android:name="android.intent.category.DEFAULT"/>
     * <category android:name="android.intent.category.BROWSABLE"/>
     * <p>
     * <data
     * android:host="m.linghit.com"
     * android:scheme="linghit"/>
     * </intent-filter>
     */
    private void handleSchemeLaunch(Intent intent) {
        if (intent == null || intent.getData() == null) {
            return;
        }
        Uri uri = intent.getData();
        String scheme = uri.getScheme();
        String host = uri.getHost();
        String query = uri.getQuery();
        String quParams = uri.getQueryParameter("params");
        // 判断是不是我们自己制定的协议
        if ("linghit".equals(scheme)) {
            // 自己处理
            StringBuilder s = new StringBuilder();
            s.append("scheme：").append(scheme).append("\n");
            s.append("host：").append(host).append("\n");
            s.append("query：").append(query).append("\n");
            s.append("quParams：").append(quParams);
            showDialog(getRt("利用scheme打开主页:\n", s.toString()));
        }
    }

    private void showDialog(CharSequence msg) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示(这个对话框是我调用代码弹出)")
                .setMessage(msg)
                .create();
        dialog.show();
    }

    private SpannableStringBuilder getRt(String p, String e) {
        SpannableStringBuilder sb = new SpannableStringBuilder(p + e);
        int start = p.length();
        sb.setSpan(new ForegroundColorSpan(Color.RED), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    public Activity getActivity() {
        return this;
    }
}
