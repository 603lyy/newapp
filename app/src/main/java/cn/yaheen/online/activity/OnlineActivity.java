package cn.yaheen.online.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import cn.yaheen.online.R;
import cn.yaheen.online.common.PlayerManager;
import cn.yaheen.online.interfaces.AndroidJSInterface;
import cn.yaheen.online.switchbutton.SwitchButton;
import cn.yaheen.online.utils.Constant;
import cn.yaheen.online.utils.WeiboDialogUtils;


public class OnlineActivity extends Activity implements PlayerManager.PlayerStateListener{


    private WebView message=null;
    private WebView content=null;
    private WebView print=null;
    private Dialog mWeiboDialog=null;
    private PlayerManager player;

    private Button preBtn,nextBtn;
   private  boolean sync=true;
    private SwitchButton syncbtn;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    WeiboDialogUtils.closeDialog(mWeiboDialog);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online);
        message=(WebView) findViewById(R.id.msg);
        content=(WebView) findViewById(R.id.content);
        print=(WebView) findViewById(R.id.print);
        preBtn=(Button)findViewById(R.id.button3);
        nextBtn=(Button)findViewById(R.id.button4);
        syncbtn=(SwitchButton) findViewById(R.id.sync);
        syncbtn.setChecked(true);
        playCramer();




        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perPage();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPage();
            }
        });

        message.loadUrl("file:///android_asset/html/chat.html"); //https://jdnichollsc.github.io/Ionic-ElastiChat-with-Images/www/index.html?ionicplatform=ios#/home"
        message.setWebViewClient(new WebViewClient() {
            //覆盖shouldOverrideUrlLoading 方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }


        });

        message.getSettings().setJavaScriptEnabled(true);
        message.getSettings().setAppCacheEnabled(true);
        //设置 缓存模式
        message.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        message.getSettings().setDomStorageEnabled(true);




        print.loadUrl(Constant.baseurl+"/wPaint/student/note/list.do");
        print.setWebViewClient(new WebViewClient() {
            //覆盖shouldOverrideUrlLoading 方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("file:///android_asset/html/404.html");
            };

            @Override
            public void onPageFinished(WebView view, String url)
            {

            }
        });

        print.getSettings().setJavaScriptEnabled(true);

        // 开启 DOM storage API 功能
        print.getSettings().setDomStorageEnabled(true);

        webViewSetting();


        //是否同步
        syncbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    sync=true;
                    content.loadUrl("javascript:setSycn(" +sync + ")");
                    webViewSetting();
                }else{
                    sync=false;
                    content.loadUrl("javascript:setSycn(" +sync + ")");

                }
               //

                // Toast.makeText(MainActivity.this, "Default style button, new state: " + (isChecked ? "on" : "off"), Toast.LENGTH_SHORT).show();
            }
        });




    }







    private  void nextPage(){
        syncbtn.setChecked(false);
        content.loadUrl("javascript:nextPage("+sync +")");
        content.loadUrl("javascript:setSycn(" +false + ")");

        print.loadUrl("javascript:nextPage("+sync +")");

    }


    private void perPage(){
        syncbtn.setChecked(false);

        content.loadUrl("javascript:prePage("+sync + ")");
        content.loadUrl("javascript:setSycn(" +false + ")");

        print.loadUrl("javascript:prePage(" +sync + ")");

    }

    private  void playCramer(){
        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(OnlineActivity.this, "加载中...");
        try {
            player = new PlayerManager(this);
            player.setFullScreenOnly(true);
            player.setScaleType(PlayerManager.SCALETYPE_FILLPARENT);
            player.playInFullScreen(true);
            player.setPlayerStateListener(this);
            player.play(Constant.onlineurl);

            player.onError(new PlayerManager.OnErrorListener() {
                @Override
                public void onError(int what, int extra) {
                    Toast.makeText(OnlineActivity.this,"播放失败", Toast.LENGTH_LONG).show();
                }
            });
            mHandler.sendEmptyMessageDelayed(1, 3000);
        }catch (Exception e){
            e.printStackTrace();
        }
        Toast.makeText(OnlineActivity.this,"拼命加载中....请耐心等待", Toast.LENGTH_LONG).show();

    }


    @SuppressLint({ "SetJavaScriptEnabled", "JavascriptInterface" })
    @JavascriptInterface
    private void webViewSetting() {
        content.getSettings().setJavaScriptEnabled(true);

        // 开启 DOM storage API 功能
        content.getSettings().setDomStorageEnabled(true);
        content.getSettings().setAllowFileAccess(true);
        content.getSettings().setDefaultTextEncodingName("utf-8");
        content.loadUrl(Constant.baseurl+"/wPaint/student/list.do");

        content.setWebViewClient(new WebViewClient() {
            //覆盖shouldOverrideUrlLoading 方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl("file:///android_asset/html/404.html");
            };

            @Override
            public void onPageFinished(WebView view, String url)
            {

            }
        });

        AndroidJSInterface ajsi = new AndroidJSInterface(this);
        content.addJavascriptInterface(ajsi, ajsi.getInterface());


    }


    @JavascriptInterface
    private void addJSInterface() {
        AndroidJSInterface ajsi = new AndroidJSInterface(this);
        content.addJavascriptInterface(ajsi, ajsi.getInterface());
    }


    @Override
    public void onComplete() {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onPlay() {

    }
}
