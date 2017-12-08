package cn.yaheen.online.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import cn.yaheen.online.*;
import cn.yaheen.online.app.OnlineApp;
import cn.yaheen.online.utils.Constant;
import cn.yaheen.online.utils.activity.AndroidBug5497Workaround;
import cn.yaheen.online.utils.sharepreferences.DefaultPrefsUtil;
import cn.yaheen.online.utils.sharepreferences.SharedPreferencesUtils;
import cn.yaheen.online.utils.SysUtils;

public class GradeActivity extends Activity {


    private WebView webView;
    Constant constant;
    private ImageView close;
    SharedPreferencesUtils preferencesUtils = null;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnlineApp.getInstance().addActivity(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_grade);
        AndroidBug5497Workaround.assistActivity(this);

        webView = (WebView) findViewById(R.id.grade);
        close = (ImageView) findViewById(R.id.close);
        constant = Constant.createConstant(GradeActivity.this);
        preferencesUtils = SharedPreferencesUtils.createSharedPreferences("online", GradeActivity.this);
        String code = DefaultPrefsUtil.getCourseCode();
        url = Constant.getBaseurl() + "/score/score.do?hardwareId=" + SysUtils.android_id(GradeActivity.this) + "&courseCode=" + code;


        // String url="HTTP://192.168.199.145:8080/loles/score/score.do?token="+token;
        webView.loadUrl(url); //https://jdnichollsc.github.io/Ionic-ElastiChat-with-Images/www/index.html?ionicplatform=ios#/home"
        webView.setWebViewClient(new WebViewClient() {
            //覆盖shouldOverrideUrlLoading 方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                view.loadUrl("file:///android_asset/html/404.html");
            }


        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        //设置 缓存模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
