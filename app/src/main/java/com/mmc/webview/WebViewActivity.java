package com.mmc.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = WebViewActivity.class.getSimpleName();

    private String mUrl;
    private WebView mWebView;

    private Button vLoadUrl;
    private Button vEval;
    private Button vFree;

    private boolean loadingPageOver;

    public Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mWebView = findViewById(R.id.webview);
        vLoadUrl = findViewById(R.id.call_loadurl);
        vEval = findViewById(R.id.call_eval);
        vFree = findViewById(R.id.call_free);
        vLoadUrl.setOnClickListener(this);
        vEval.setOnClickListener(this);
        vFree.setOnClickListener(this);
        setupWebView();
        mUrl = getIntent().getData().toString();
        mWebView.loadUrl(mUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            mWebView.loadUrl(mUrl);
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.removeAllViews();
            try {
                mWebView.destroy();
            } catch (Throwable t) {
                // ignore
            }
            mWebView = null;
        }
    }

    /**
     * 初始化WebView
     */
    @SuppressWarnings("all")
    public void setupWebView() {
        final WebSettings settings = mWebView.getSettings();
        String userAgent = settings.getUserAgentString();
        // TODO: 2018/2/25 ua 处理
        userAgent = userAgent + " linghit ljms cn";
        Log.i(TAG, "user-agent----->" + userAgent);
        settings.setUserAgentString(userAgent);
        settings.setSupportZoom(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setLoadWithOverviewMode(true);
        settings.setAppCacheMaxSize(1024 * 1024 * 5);
        //设置缓存模式,无缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        initJsJava1();
        initJsJava2();
        initJsJava3();
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

    /**
     * todo js 调用安卓的第一种方式addJavascriptInterface
     */
    private void initJsJava1() {
        mWebView.addJavascriptInterface(new AndroidtoJs(), "test");
        JsCallJavaCallBackImpl callBack = new JsCallJavaCallBackImpl(this);
        mWebView.addJavascriptInterface(new JsJava(callBack), "lingjiWebApp");
        mWebView.addJavascriptInterface(new JsJava2(callBack), "MMCWKEventHandler");
    }

    private class JsJava implements Serializable, IJsCallJavaCallBack {

        private Handler mHandler;
        private IJsCallJavaCallBack mCallBack;

        public JsJava(IJsCallJavaCallBack callBack) {
            mCallBack = callBack;
            mHandler = new Handler(Looper.getMainLooper());
        }

        private void runOnUiThread(Runnable runnable) {
            mHandler.post(runnable);
        }

        @JavascriptInterface
        @Override
        public String getUserInfo() {
            return mCallBack.getUserInfo();
        }

        @JavascriptInterface
        @Override
        public void MMCLogin() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCallBack.MMCLogin();
                }
            });
        }

        @JavascriptInterface
        @Override
        public void MMCOnlinePay(final String data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCallBack.MMCOnlinePay(data);
                }
            });
        }
    }

    private class JsJava2 implements Serializable, IJsCallJavaCallBackV2 {

        private Handler mHandler;
        private IJsCallJavaCallBackV2 mCallBack;

        public JsJava2(IJsCallJavaCallBackV2 callBack) {
            mCallBack = callBack;
            mHandler = new Handler(Looper.getMainLooper());
        }

        private void runOnUiThread(Runnable runnable) {
            mHandler.post(runnable);
        }

        @JavascriptInterface
        @Override
        public void postMessage(final String data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCallBack.postMessage(data);
                }
            });
        }
    }

    /**
     * 所有的java类都是默认继承Object的
     */
    public class AndroidtoJs {

        /**
         * 定义JS需要调用的方法，被JS调用的方法必须加入@JavascriptInterface注解(为毛要加)
         * <p>
         * 在API Level < 17时(即4.2版本之前)，JS通过WebView暴露的对象，可以访问其任意的public fields(静态方法和实例方法都可以被访问)。
         * <p>
         * API Level >= 17的设备，那google已经解决了这个问题，通过加上”@JavascriptInterface”注解暴露给JS指定的方法。
         * <p>
         * 所以意思是如果不加的话，安卓4.2以后的手机不能调用到这个方法
         *
         * @param msg 参数，此参数只能是简单的整数，浮点数等基本类型，所以其实一般建议传json字符串
         */
        @JavascriptInterface
        public void hello(String msg) {
            showDialog(getRt("JS调用了Android的hello方法，并传递了参数:", msg));
        }

        @JavascriptInterface
        public String hello2(String msg) {
            showDialog(getRt("JS调用了Android的hello2方法，并传递了参数:", msg));
            JSONObject object = new JSONObject();
            try {
                object.put("name", "周杰伦");
                object.put("age", 28);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return object.toString();
        }
    }

    /**
     * todo js 调用安卓的第二种方式addJavascriptInterface
     */
    private void initJsJava2() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                String scheme = uri.getScheme();
                String host = uri.getHost();
                String query = uri.getQuery();
                String quParams = uri.getQueryParameter("params");
                // 判断是不是我们自己制定的协议
                if ("js".equals(scheme)) {
                    // 自己处理
                    StringBuilder s = new StringBuilder();
                    s.append("scheme：").append(scheme).append("\n");
                    s.append("host：").append(host).append("\n");
                    s.append("query：").append(query).append("\n");
                    s.append("quParams：").append(quParams);
                    showDialog(getRt("使用shouldOverrideUrlLoading加上对应的协议拦截url:\n", s.toString()));
                    return true;
                } else if (!scheme.startsWith("http")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadingPageOver = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadingPageOver = true;
            }
        });
    }

    /**
     * todo js 调用安卓的第三种方式addJavascriptInterface
     */
    private void initJsJava3() {
        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                // message ===> js://m.linghit.com?params={"name":"小小","age":18,"sex":0}
                Uri uri = Uri.parse(message);
                String scheme = uri.getScheme();
                String host = uri.getHost();
                String query = uri.getQuery();
                String quParams = uri.getQueryParameter("params");
                // 判断是不是我们自己制定的协议
                if ("js".equals(scheme)) {
                    // 自己处理
                    StringBuilder s = new StringBuilder();
                    s.append("scheme：").append(scheme).append("\n");
                    s.append("host：").append(host).append("\n");
                    s.append("query：").append(query).append("\n");
                    s.append("quParams：").append(quParams);
                    showDialog(getRt("使用onJsPrompt加上对应的协议:\n", s.toString()));
                    // 返回值
                    JSONObject object = new JSONObject();
                    try {
                        object.put("name", "周杰伦");
                        object.put("age", 28);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    result.confirm(object.toString());
                    return true;
                }
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }

        });
    }

    @Override
    public void onClick(View v) {
        if (!loadingPageOver) {
            showDialog("请等待页面加载完成!!!!");
        }
        if (v == vLoadUrl) {
            loadurl();
        } else if (v == vEval) {
            eval();
        } else if (v == vFree) {
            if (Build.VERSION.SDK_INT >= 19) {
                eval();
            } else {
                loadurl();
            }
        }
    }

    private void loadurl() {
        // javascript:callJS('周杰伦')
        String name = "周杰伦";
        mWebView.loadUrl("javascript:callJSLoadurl('" + name + "')");
    }

    @TargetApi(19)
    private void eval() {
        String name = "王力宏";
        mWebView.evaluateJavascript("javascript:callJSEval('" + name + "')", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                showDialog(getRt("使用evaluateJavascript()调用js代码并收到返回:\n", value));
            }
        });
    }

}
