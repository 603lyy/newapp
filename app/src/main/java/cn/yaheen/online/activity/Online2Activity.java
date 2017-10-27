package cn.yaheen.online.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import cn.nodemedia.NodePlayer;
import cn.nodemedia.NodePlayerDelegate;
import cn.yaheen.online.R;
import cn.yaheen.online.common.PlayerManager;
import cn.yaheen.online.interfaces.AndroidJSInterface;
import cn.yaheen.online.switchbutton.SwitchButton;
import cn.yaheen.online.utils.Constant;
import cn.yaheen.online.utils.WeiboDialogUtils;

public class Online2Activity extends Activity implements NodePlayerDelegate {

    private WebView message=null;
    private WebView content=null;
    private WebView print=null;
    private Dialog mWeiboDialog=null;
    NodePlayer np;

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
        setContentView(R.layout.activity_online2);
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
        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(Online2Activity.this, "加载中...");


        //创建NodePlayer实例
        np = new NodePlayer(this);

//        //查询播放视图
//        NodePlayerView npv = (NodePlayerView)findViewById(R.id.live_player_view);
//        //设置播放视图的渲染器模式,可以使用SurfaceView或TextureView. 默认SurfaceView
//        npv.setRenderType(NodePlayerView.RenderType.SURFACEVIEW);
//        //设置视图的内容缩放模式
//        npv.setUIViewContentMode(NodePlayerView.UIViewContentMode.ScaleAspectFill);
//
//
//        //将播放视图绑定到播放器
//        np.setPlayerView(npv);
//
//        //设置事件回调代理
//        np.setNodePlayerDelegate(this);
//
//        //开启硬件解码,支持4.3以上系统,初始化失败自动切为软件解码,默认开启.
//        np.setHWEnable(true);
//
//
//        /**
//         * 设置启动缓冲区时长,单位毫秒.此参数关系视频流连接成功开始获取数据后缓冲区存在多少毫秒后开始播放
//         */
//        int bufferTime = Integer.valueOf("300");
//        np.setBufferTime(bufferTime);
//
//        /**
//         * 设置最大缓冲区时长,单位毫秒.此参数关系视频最大缓冲时长.
//         * RTMP基于TCP协议不丢包,网络抖动且缓冲区播完,之后仍然会接受到抖动期的过期数据包.
//         * 设置改参数,sdk内部会自动清理超出部分的数据包以保证不会存在累计延迟,始终与直播时间线保持最大maxBufferTime的延迟
//         */
//        int maxBufferTime = Integer.valueOf("1000");
//        np.setMaxBufferTime(maxBufferTime);
//
//        /**
//         * 设置连接超时时长,单位毫秒.默认一直等待.
//         * 连接部分RTMP服务器,握手并连接成功后,当播放一个不存在的流地址时,会一直等待下去.
//         * 如需超时,设置该值.超时后返回1006状态码.
//         */
//        np.setConnectWaitTimeout(10*1000);
//
//
//        /**
//         * 设置播放直播视频url
//         */
//
//        np.setInputUrl(Constant.onlineurl);
//
//        /**
//         * 开始播放直播视频
//         */
//        np.start();
//        mHandler.sendEmptyMessageDelayed(1, 3000);

        Toast.makeText(Online2Activity.this,"拼命加载中....请耐心等待", Toast.LENGTH_LONG).show();

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
    public void onEventCallback(NodePlayer nodePlayer, int i, String s) {

    }
}
