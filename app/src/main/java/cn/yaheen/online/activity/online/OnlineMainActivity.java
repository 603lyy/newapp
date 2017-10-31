package cn.yaheen.online.activity.online;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.MediaActionSound;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.yaheen.online.Contral.BitmapUtil;
import cn.yaheen.online.R;
import cn.yaheen.online.activity.GradeActivity;
import cn.yaheen.online.activity.GridViewColorActivity;
import cn.yaheen.online.app.OnlineApp;
import cn.yaheen.online.bean.MsgBean;
import cn.yaheen.online.bean.ResponseEntity;
import cn.yaheen.online.bean.ResponseEntityResult;
import cn.yaheen.online.bean.TbChatMsg;
import cn.yaheen.online.chat.ChatMsgEntity;
import cn.yaheen.online.chat.ChatMsgViewAdapter;
import cn.yaheen.online.common.network.INetwordWatcherListener;
import cn.yaheen.online.common.network.NetworkWatcher;
import cn.yaheen.online.dao.UploadDAO;
import cn.yaheen.online.interfaces.OnSaveCallBack;
import cn.yaheen.online.interfaces.PageCallBack;
import cn.yaheen.online.model.UploadModel;
import cn.yaheen.online.receiver.Receiver;
import cn.yaheen.online.switchbutton.SwitchButton;
import cn.yaheen.online.utils.Constant;
import cn.yaheen.online.utils.DensityUtils;
import cn.yaheen.online.utils.DialogCallback;
import cn.yaheen.online.utils.DialogUtils;
import cn.yaheen.online.utils.IDialogCancelCallback;
import cn.yaheen.online.utils.ImgUtil;
import cn.yaheen.online.utils.sharepreferences.DefaultPrefsUtil;
import cn.yaheen.online.utils.sharepreferences.SharedPreferencesUtils;
import cn.yaheen.online.utils.SysUtils;
import cn.yaheen.online.utils.ToastUtils;
import cn.yaheen.online.utils.UUIDUtils;
import cn.yaheen.online.utils.WeiboDialogUtils;
import cn.yaheen.online.utils.XHttpUtils;
import cn.yaheen.online.view.CommonProgressDialog;
import cn.yaheen.online.view.CustomDialog;
import cn.yaheen.online.view.PopupMenu;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;
import me.panavtec.drawableview.draw.SerializablePath;
import me.panavtec.drawableview.utils.SerializeUtils;

import static cn.yaheen.online.R.id.textView2;

public class OnlineMainActivity extends Activity implements Receiver.Message, View.OnClickListener,
        PopupMenu.OnItemClickListener, INetwordWatcherListener {

    private final String TAG = "SosWebSocketClientService";

    // 表示打开照相机
    private final int IMAGE_RESULT_CODE = 2;
    // 选择图片库
    private final int PICK = 1;

    private Receiver myReceiver;
    private AliVcMediaPlayer mPlayer;
    private Timer timer = new Timer();
    private ChatMsgViewAdapter mAdapter;
    private NetworkWatcher networkWatcher;
    private final WebSocketConnection mConnection = new WebSocketConnection();

    private Button cut;
    private Context context;
    private Button mBtnSend;
    private ListView mListView;
    private EditText mEditTextContent;
    private CommonProgressDialog mDialog;
    /**
     * 消息对象数组
     */
    private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();

    public static int i = 1;
    public static int k = 0;
    /**
     * 总页数
     */
    private int page = 1;
    /**
     * 当前画笔大小
     */
    private int curVal = 2;
    /**
     * 当前页，用于翻页
     */
    private int curPage = 1;
    /**
     * 当前画笔颜色
     */
    private int curColor = Color.BLACK;

    /**
     * 画板
     */
    private DrawableView m_view;//bin:画板
    SharedPreferencesUtils preferencesUtils = null;
    Constant constant = null;
    private UploadDAO uploadDAO = null;
    String courseCode = "";
    String uuid = "";
    private DrawableViewConfig config = new DrawableViewConfig();
    private TextView pagetv;
    private TextView titleTV;//title

    /**
     * 5.0系统截图
     */
    private final int SCREENCUT = 101;// 选择图片库
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private ImageReader mImageReader;
    private LinearLayout mCaptureLl;
    private ImageView mCaptureIv;
    private WindowManager wm;

    private Intent mResultData = null;
    private int mResultCode = 0;
    private int screenDensity;
    private int windowHeight;
    private int windowWidth;
    private String mImageName;
    private String mImagePath;

    private boolean savingNative = false;
    private boolean isLogin = true;//是否在线模式
    private boolean isPingJiaoOpen = false;


    /**
     * 移动图片
     *
     * @param savedInstanceState
     */

    private RelativeLayout mRlMezi;
    private DisplayMetrics dm;
    private int lastX, lastY;
    private long mLastTime = 0, mCurTime = 0; //双击记录点击时间
    private boolean touchTwo = false, isFirstTouch = true;  //是否第二次点击
    private int height = 0, width = 0;  //记录第一次点击时的框框大小
    private int multiple = 2;  //放大倍数
    private int multi = 2;  //缩小倍数
    boolean isHeng = true;
    private PopupMenu popupMenu;  //右上角弹出框
    private ImageView rightBtn = null;
    SurfaceView npv = null;
    LinearLayout leftLayout = null;  //左边部分
    LinearLayout chatLayout = null;
    private ImageView backhome; //返回按钮
    private SurfaceView contentnpv;
    private Map<Integer, Bitmap> pics = new HashMap<>();
    private Map<Integer, Bitmap> bigpicsCacheMap = new HashMap<Integer, Bitmap>();//Edit by xszyou on 20170617:临时保存画板最终效果图
    private int canvaswidth, canvasheight;
    private SwitchButton sbDefault;//课件视讯切换
    private WebView webView = null;
    private boolean contentisPlaying = true; //内容区域是否在同屏
    private ImageView newbtn; //新建按钮
    private ImageView scrollerbtn; //模式变化按钮
    private ImageView eraserBtn; //橡皮按钮
    private ImageView colorBtn; //画板色板按钮
    private ImageView cutBtn; //画板截图按钮


    private LinearLayout toolsBar;

    private boolean isnew = false; //截图是否新建
    ArrayList<SerializablePath> paths = new ArrayList<SerializablePath>(); //当前画板轨迹
    Map<Integer, ArrayList<SerializablePath>> pathsMap = new HashMap<Integer, ArrayList<SerializablePath>>();//内存保存画板的轨迹
    private Dialog mWeiboDialog = null;

    File outputImage = null;//拍照的临时文件

    /**
     * Edit by xszyou on 20170625:组件全屏及还原按钮
     */
    private ImageView contentResize;
    private ImageView canvasResize;
    private ImageView msgResize;
    private ImageView kjResize;
    private Boolean isFullScreen = false;
    private Boolean isMsgFull = false;
    private Boolean isContentFull = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    pagetv.setText("(" + curPage + "/" + page + ")");
                    WeiboDialogUtils.closeDialog(mWeiboDialog);
                    break;
                case 2:
                    pagetv.setText("(" + curPage + "/" + page + ")");
                    break;
                //截屏
                case 6:
                    doNewPage(true);
                    pagetv.setText("(" + curPage + "/" + page + ")");
                    break;
                case 5://Edit by xszyou on 20170611：定时调用保存操作
                    cacheData(new OnSaveCallBack() {
                        @Override
                        public void saveSuccess() {
                            saveAllToNative(new OnSaveCallBack() {
                                @Override
                                public void saveSuccess() {
                                    ToastUtils.showMessage(OnlineMainActivity.this, "自动保存成功");
                                }

                                @Override
                                public void saveFail() {
                                    ToastUtils.showMessage(OnlineMainActivity.this, "自动保存失败");
                                }
                            }, false);
                        }

                        @Override
                        public void saveFail() {
                            ToastUtils.showMessage(OnlineMainActivity.this, "数据缓存失败，有可能造成数据掉失");
                        }
                    }, false, false);
                    break;
                default:
            }
        }
    };

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (!isPingJiaoOpen && isLogin) {
                        titleTV.setText("在线评教系统");
                    } else {
                        titleTV.setText("在线评教系统" + courseCode);
                    }
                    break;
                default:
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = OnlineMainActivity.this;
        OnlineApp.getInstance().addActivity(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        preferencesUtils = SharedPreferencesUtils.createSharedPreferences("online", OnlineMainActivity.this);
        isPingJiaoOpen = this.getIntent().getBooleanExtra("isPingJiaoOpen", false);
        networkWatcher = new NetworkWatcher(getApplicationContext(), this);
        constant = Constant.createConstant(OnlineMainActivity.this);
        Boolean screen = DefaultPrefsUtil.getIsHorizontalScreen();
        String token = DefaultPrefsUtil.getToken();

        courseCode = DefaultPrefsUtil.getCourseCode();
        uuid = DefaultPrefsUtil.getUUID();
        constant.setIsHeng(screen);
        isHeng = screen;
        if (token == null || "".equals(token.trim())) {
            isLogin = false;
        } else {
            isLogin = true;
        }

        initScreen();
        contentResize = (ImageView) findViewById(R.id.content_resize);
        canvasResize = (ImageView) findViewById(R.id.canvas_resize);
        msgResize = (ImageView) findViewById(R.id.msg_resize);
        kjResize = (ImageView) findViewById(R.id.kj_resize);
        pagetv = (TextView) this.findViewById(R.id.pagetv);
        titleTV = (TextView) findViewById(textView2);

        if (isLogin) {
            //Edit by xszyou on 20170611:IM
            initView();
            initData();// 初始化数据
            mListView.setSelection(mAdapter.getCount() - 1);
            initWebSocket();
            //Edit by xszyou on 20170611:播放共享桌面
            initSurfaceView();
            initVodPlayer(null, null, null);
            //初始化课件
            addWebView();
        } else {
            if (isHeng) {
                findViewById(R.id.contentLinear).setVisibility(View.GONE);
            } else {
                findViewById(R.id.main).setVisibility(View.GONE);
            }
            findViewById(R.id.sb_default).setVisibility(View.GONE);
            findViewById(R.id.kjtv).setVisibility(View.GONE);
            findViewById(R.id.sptv).setVisibility(View.GONE);
            resizeBtnShow(false);
        }
        uploadDAO = new UploadDAO();

        //Edit by xszyou on 20170611:初始化画板
        intCanvas();
        //Edit by xszyou on 20170611:初始化截屏，不再使用
        initCut();
        //Edit by xszyou on 20170611:弹出框
        initPopup();
        //Edit by xszyou on 20170611:返回健
        initBackHome();
        //Edit by xszyou on 20170611:载入数据
        initFirst();
        initNewPage();
        changeScroller();
        initSelectPage();
        initReceiverBost();

        pagetv.setText("(" + curPage + "/" + page + ")");
        if (isPingJiaoOpen || !isLogin) {
            titleTV.setText(titleTV.getText().toString() + courseCode);
        }

        contentResize.setOnClickListener(this);
        canvasResize.setOnClickListener(this);
        msgResize.setOnClickListener(this);
        kjResize.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.kj_resize:
                if (!isFullScreen) {
                    if (isHeng) {
                        findViewById(R.id.topPanel).setVisibility(View.GONE);
                        findViewById(R.id.print).setVisibility(View.GONE);
                    } else {
                        //原layout删除放到外面全屏显示
                        LinearLayout showView = (LinearLayout) findViewById(R.id.showview);
                        LinearLayout mainMain = (LinearLayout) findViewById(R.id.main_main);
                        mainMain.removeView(showView);
                        showView.getLayoutParams().width = constant.getDmWidth();
                        showView.getLayoutParams().height = constant.getDmHeight();
                        ((RelativeLayout) findViewById(R.id.activity_online)).addView(showView);
                        showView.bringToFront();
                    }
                    resizeBtnShow(false, view);
                    view.bringToFront();
                    kjResize.setImageResource(R.drawable.simple_player_icon_fullscreen_shrink_o);
                } else {
                    if (isHeng) {
                        findViewById(R.id.topPanel).setVisibility(View.VISIBLE);
                        findViewById(R.id.print).setVisibility(View.VISIBLE);
                    } else {
                        LinearLayout showView = (LinearLayout) findViewById(R.id.showview);
                        LinearLayout mainMain = (LinearLayout) findViewById(R.id.main_main);
                        ((RelativeLayout) findViewById(R.id.activity_online)).removeView(showView);
                        showView.getLayoutParams().width = constant.getDmWidth() / 2;
                        showView.getLayoutParams().height = (int) ((constant.getDmHeight() - DensityUtils.dip2px(context, 30)) * 0.382);
                        mainMain.addView(showView, 0);
                    }
                    resizeBtnShow(true, contentResize, msgResize);
                    kjResize.setImageResource(R.drawable.simple_player_icon_fullscreen_stretch_o);
                }
                isFullScreen = !isFullScreen;
                break;
            case R.id.canvas_resize:
                if (!isFullScreen) {
                    findViewById(R.id.sb_default).setVisibility(View.GONE);
                    findViewById(R.id.kjtv).setVisibility(View.GONE);
                    findViewById(R.id.sptv).setVisibility(View.GONE);

                    if (isHeng) {
                        //Edit by xszyou on 20170628:如果IM全屏，就先还原
                        if (isMsgFull) {
                            msgResize.callOnClick();
                        }
                        findViewById(R.id.contentLinear).setVisibility(View.GONE);
                    } else {
                        //如果视频已经放大，就先还原
                        if (isContentFull) {
                            contentResize.callOnClick();
                        }
                        findViewById(R.id.main).setVisibility(View.GONE);
                    }
                    //Edit by xszyou on 20170626:停止播放共享桌面
                    if (!sbDefault.isChecked()) {
                        stopContentPlay((LinearLayout) findViewById(R.id.showview));
                    }
                    resizeBtnShow(false, view);
                    canvasResize.setImageResource(R.drawable.simple_player_icon_fullscreen_shrink_o);
                } else {
                    //Edit by xszyou on 20170626:播放共享桌面
                    LinearLayout layout = (LinearLayout) findViewById(R.id.showview);
                    findViewById(R.id.sb_default).setVisibility(View.VISIBLE);
                    findViewById(R.id.kjtv).setVisibility(View.VISIBLE);
                    findViewById(R.id.sptv).setVisibility(View.VISIBLE);
                    if (isHeng) {
                        findViewById(R.id.contentLinear).setVisibility(View.VISIBLE);
                        resizeBtnShow(true, sbDefault.isChecked() ? contentResize : kjResize);
                    } else {
                        findViewById(R.id.main).setVisibility(View.VISIBLE);
                        resizeBtnShow(true, sbDefault.isChecked() ? contentResize : kjResize, msgResize);
                    }
                    if (!sbDefault.isChecked()) {
                        initVodPlayer(layout, layout.getLayoutParams().width, layout.getLayoutParams().height);
                    }
                    canvasResize.setImageResource(R.drawable.simple_player_icon_fullscreen_stretch_o);
                }
                isFullScreen = !isFullScreen;
                break;
            case R.id.msg_resize:
                if (!isMsgFull) {
                    if (isHeng) {
                        //Edit by xszyou on 20170626:停止播放共享桌面
                        LinearLayout layout = (LinearLayout) findViewById(R.id.showview);
                        stopContentPlay(layout);
                        layout.setVisibility(View.GONE);
                        //IM的高度顶满
                        RelativeLayout rl = (RelativeLayout) findViewById(R.id.msg_layout);
                        ViewGroup.LayoutParams lp = rl.getLayoutParams();
                        lp.height = constant.getDmWidth() - DensityUtils.dip2px(context, 30);
                        resizeBtnShow(false, view, canvasResize);
                        msgResize.setImageResource(R.drawable.simple_player_icon_fullscreen_shrink_o);
                    }
                } else {
                    if (isHeng) {
                        //Edit by xszyou on 20170626:播放共享桌面
                        LinearLayout layout = (LinearLayout) findViewById(R.id.showview);
                        int height = (int) ((constant.getDmWidth() - DensityUtils.dip2px(context, 30)) / 2);//top 30 dp

                        initVodPlayer(layout, (int) (constant.getDmHeight() * 0.382), height);
                        layout.getLayoutParams().height = height;
                        layout.setVisibility(View.VISIBLE);

                        RelativeLayout rl = (RelativeLayout) findViewById(R.id.msg_layout);
                        ViewGroup.LayoutParams lp = rl.getLayoutParams();
                        lp.height = height;
                        resizeBtnShow(true, kjResize);
                        msgResize.setImageResource(R.drawable.simple_player_icon_fullscreen_stretch_o);
                    }
                }
                isMsgFull = !isMsgFull;
                break;
            case R.id.content_resize:
                //竖屏时视频不会完全全屏
                if ((isHeng && !isFullScreen) || (!isHeng && !isContentFull)) {
                    //Edit by on 20170626:先停止和删除播放器
                    LinearLayout layout = (LinearLayout) findViewById(R.id.showview);
                    stopContentPlay(layout);
                    RelativeLayout rl = null;
                    int width = 0, height = 0;
                    if (isHeng) {
                        findViewById(R.id.linearLayout).setVisibility(View.GONE);
                        rl = (RelativeLayout) findViewById(R.id.activity_online);
                        width = constant.getDmHeight();
                        height = constant.getDmWidth();
                        resizeBtnShow(false, view);
                        isFullScreen = !isFullScreen;
                    } else {
                        rl = (RelativeLayout) findViewById(R.id.main);
                        width = constant.getDmWidth();
                        height = rl.getLayoutParams().height;
                        resizeBtnShow(false, view, canvasResize);
                        isContentFull = !isContentFull;
                    }

                    initVodPlayer(rl, width, height);
                    rl.bringChildToFront(view);
                    contentResize.setImageResource(R.drawable.simple_player_icon_fullscreen_shrink_o);
                } else {//还原
                    RelativeLayout rl = null;
                    int width = 0, height = 0;
                    if (isHeng) {
                        findViewById(R.id.linearLayout).setVisibility(View.VISIBLE);
                        rl = (RelativeLayout) findViewById(R.id.activity_online);
                        width = (int) (constant.getDmHeight() * 0.382);
                        height = (int) ((constant.getDmWidth() - DensityUtils.dip2px(context, 30)) / 2);
                        resizeBtnShow(true, kjResize);
                        isFullScreen = !isFullScreen;
                    } else {
                        rl = (RelativeLayout) findViewById(R.id.main);
                        width = constant.getDmWidth() / 2;
                        height = rl.getLayoutParams().height;
                        resizeBtnShow(true, kjResize, msgResize);
                        isContentFull = !isContentFull;
                    }
                    stopContentPlay(rl);
                    initVodPlayer((LinearLayout) findViewById(R.id.showview), width, height);
                    contentResize.setImageResource(R.drawable.simple_player_icon_fullscreen_stretch_o);
                }
                break;
            case R.id.rightbtn:
                // 设置弹出菜单弹出的位置
                popupMenu.showLocation(R.id.rightbtn);
                // 设置回调监听，获取点击事件
                popupMenu.setOnItemClickListener(this);
                break;
            default:
        }
    }

    @Override
    public void onClick(PopupMenu.MENUITEM item) {
        //保存按钮
        if (item == PopupMenu.MENUITEM.ITEM1) {
            cacheData(new OnSaveCallBack() {
                @Override
                public void saveSuccess() {
                    saveAllToNative(new OnSaveCallBack() {
                        @Override
                        public void saveSuccess() {
                            ToastUtils.showMessage(OnlineMainActivity.this, "保存成功");
                        }

                        @Override
                        public void saveFail() {
                            ToastUtils.showMessage(OnlineMainActivity.this, "保存失败");
                        }
                    }, false);

                }

                @Override
                public void saveFail() {
                    ToastUtils.showMessage(OnlineMainActivity.this, "数据缓存失败，无法保存");
                }
            }, true, false);
        } else if (item == PopupMenu.MENUITEM.ITEM2) {
            //上传按钮
            cacheData(new OnSaveCallBack() {
                @Override
                public void saveSuccess() {
                    saveAllToNative(new OnSaveCallBack() {
                        @Override
                        public void saveSuccess() {
                            ToastUtils.showMessage(OnlineMainActivity.this, "保存成功");
                            List<UploadModel> uploadModels = uploadDAO.findByStatusAndUID(UploadModel.STATUS_SAVED, uuid);
                            if (isLogin) {
                                if (uploadModels != null && uploadModels.size() > 0) {
                                    i = 1;
                                    mDialog = new CommonProgressDialog(OnlineMainActivity.this, new DialogListener());
                                    mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    mDialog.setCanceledOnTouchOutside(false);
                                    mDialog.setMessage("正在上传");
                                    mDialog.show();
                                    upload(uploadModels, 0);
                                }
                            } else {
                                dialog(uploadModels);
                            }
                        }

                        @Override
                        public void saveFail() {
                            ToastUtils.showMessage(OnlineMainActivity.this, "保存失败，终止上传");
                        }
                    }, false);
                }

                @Override
                public void saveFail() {
                    ToastUtils.showMessage(OnlineMainActivity.this, "数据缓存失败，终止上传");
                }
            }, true, false);
            // OnUpload();
        } else if (item == PopupMenu.MENUITEM.ITEM3) {
            //删除
            DialogUtils.showDialog(OnlineMainActivity.this, "你确定要删除本页吗？", new DialogCallback() {
                @Override
                public void callback() {
                    cacheData(new OnSaveCallBack() {
                        @Override
                        public void saveSuccess() {
                            saveAllToNative(new OnSaveCallBack() {
                                @Override
                                public void saveSuccess() {
                                    deletePage(curPage);
                                }

                                @Override
                                public void saveFail() {
                                    ToastUtils.showMessage(OnlineMainActivity.this, "保存失败，终止操作");
                                }
                            }, false);

                        }

                        @Override
                        public void saveFail() {
                            ToastUtils.showMessage(OnlineMainActivity.this, "数据缓存失败，终于操作");
                        }
                    }, true, false);
                }
            }, new IDialogCancelCallback() {
                @Override
                public void cancelCallback() {
                }
            });
        } else if (item == PopupMenu.MENUITEM.ITEM5) {
            //评分按钮
            UploadModel uploadModel = uploadDAO.findUploadByPageAndUID(curPage, uuid);
            if (uploadModel.getStatus() == UploadModel.STATUS_UPLOADED) {
                showNormalDialog("确定要结束课程进行评分吗？", new DialogCallback() {
                    @Override
                    public void callback() {
                        cacheData(new OnSaveCallBack() {
                            @Override
                            public void saveSuccess() {
                                saveAllToNative(new OnSaveCallBack() {
                                    @Override
                                    public void saveSuccess() {
                                        Toast.makeText(OnlineMainActivity.this, "保存成功，马上开始评分", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent();
                                        intent.setClass(OnlineMainActivity.this, GradeActivity.class);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void saveFail() {
                                        Toast.makeText(OnlineMainActivity.this, "保存失败，终止评分", Toast.LENGTH_SHORT).show();
                                    }
                                }, false);

                            }

                            @Override
                            public void saveFail() {
                                Toast.makeText(OnlineMainActivity.this, "数据缓存失败，终止评分", Toast.LENGTH_SHORT).show();
                            }
                        }, true, false);
                    }
                });
            } else {
                Intent intent = new Intent();
                intent.setClass(OnlineMainActivity.this, GradeActivity.class);
                startActivity(intent);
            }
        } else if (item == PopupMenu.MENUITEM.ITEM6) {
            //拍照按钮
            takePhoto();
        } else if (item == PopupMenu.MENUITEM.ITEM7) {
            //横竖屏
            cacheData(new OnSaveCallBack() {
                @Override
                public void saveSuccess() {
                    saveAllToNative(new OnSaveCallBack() {
                        @Override
                        public void saveSuccess() {
                            isHeng = !isHeng;
                            popupMenu.setHeng(isHeng);
                            DefaultPrefsUtil.setIsHorizontalScreen(isHeng);
                            if (isLogin) {
                                stopContentPlay(null);
                                mConnection.disconnect();
                            }
                            Intent intent = new Intent();
                            //Edit by xszyou on 20170706:切换时传递消息列表
                            if (mDataArrays != null && !mDataArrays.isEmpty()) {
                                Bundle bundle = new Bundle();
                                GsonBuilder setedBuilder = new GsonBuilder();
                                Gson requestConfig = setedBuilder.create();
                                requestConfig.toJsonTree(mDataArrays);
                                bundle.putSerializable("msgs", requestConfig.toJsonTree(mDataArrays).toString());
                                intent.putExtras(bundle);
                            }
                            intent.setClass(OnlineMainActivity.this, OnlineMainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void saveFail() {
                            ToastUtils.showMessage(OnlineMainActivity.this, "保存失败，终止操作");
                        }
                    }, false);

                }

                @Override
                public void saveFail() {
                    ToastUtils.showMessage(OnlineMainActivity.this, "数据缓存失败，终于操作");
                }
            }, true, false);
        } else if (item == PopupMenu.MENUITEM.ITEM8) {
            //撤消
            m_view.undo();
        }
    }

    private class DialogListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mDialog.dismiss();
        }
    }

    /**
     * Edit by xszyou on 20170626:改变大小按钮显示或隐藏
     */
    private void resizeBtnShow(boolean show, View... excludeViews) {
        contentResize.setVisibility(show ? View.VISIBLE : View.GONE);
        canvasResize.setVisibility(show ? View.VISIBLE : View.GONE);
        msgResize.setVisibility(show ? View.VISIBLE : View.GONE);
        kjResize.setVisibility(show ? View.VISIBLE : View.GONE);
        if (excludeViews != null && excludeViews.length > 0) {
            for (View view : excludeViews) {
                view.setVisibility(!show ? View.VISIBLE : View.GONE);
            }

        }
    }

    /**
     * 注册广播
     */
    private void initReceiverBost() {
        //动态注册广播
        myReceiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cn.yaheen.color");
        registerReceiver(myReceiver, intentFilter);
        //因为这里需要注入Message，所以不能在AndroidManifest文件中静态注册广播接收器
        myReceiver.setMessage(this);
    }

    /**
     * 选择页码
     */
    private void initSelectPage() {

        pagetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<UploadModel> list = uploadDAO.findByUID(uuid);
                if (list != null && list.size() > 0) {
                    String[] items = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        items[i] = "第" + (i + 1) + "页";
                    }

                    DialogUtils.LstiDialog(items, OnlineMainActivity.this, new PageCallBack() {
                        @Override
                        public void callback(Integer page) {
                            final int loadpage = page;
                            mWeiboDialog = WeiboDialogUtils.createLoadingDialog(OnlineMainActivity.this, "数据载入中...");
                            cacheData(new OnSaveCallBack() {
                                @Override
                                public void saveSuccess() {
                                    loadPage(loadpage);//loadpage 是数据下标，0开始
                                    closeDialog();
                                }

                                @Override
                                public void saveFail() {
                                    ToastUtils.showMessage(OnlineMainActivity.this, "数据缓存失败，有可能造成数据掉失");
                                    loadPage(loadpage);//loadpage 是数据下标，0开始
                                    closeDialog();
                                }
                            }, false, false);
                        }
                    });
                }
            }
        });
    }

    /**
     * 初始化课件模式下的操作按钮
     *
     * @param webView
     */
    private void initToolsBar(final WebView webView) {
        Button kejianList = (Button) findViewById(R.id.kjlist);
        Button nextBtn = (Button) findViewById(R.id.nextwebpage);
        Button preBtn = (Button) findViewById(R.id.perwebpage);
        kejianList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl("javascript:openSelectCourseware(afxCourseId)");
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl("javascript:nextPage()");

            }
        });
        preBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl("javascript:prePage()");
            }
        });

    }

    /**
     * 初始化新建页
     */
    void initNewPage() {
        newbtn = (ImageView) findViewById(R.id.imageView8);
        newbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cacheData(new OnSaveCallBack() {
                    @Override
                    public void saveSuccess() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doNewPage(false);
                                changeCurrentPage();
                            }
                        }, 300);
                    }

                    @Override
                    public void saveFail() {
                        ToastUtils.showMessage(OnlineMainActivity.this, "数据缓存失败，有可能造成数据掉失");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doNewPage(false);
                                changeCurrentPage();
                            }
                        }, 300);
                    }
                }, true, false);
            }
        });
    }

    /**
     * 初始画板工具
     */
    private void changeScroller() {
        scrollerbtn = (ImageView) findViewById(R.id.scroller);
        eraserBtn = (ImageView) findViewById(R.id.erasers);
        colorBtn = (ImageView) findViewById(R.id.color_colour);
        cutBtn = (ImageView) findViewById(R.id.cuttool);
        scrollerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!m_view.isOneFingerMode()) {
                    scrollerbtn.setImageResource(R.drawable.pen_icon);
                    config.setStrokeColor(Color.WHITE);
                    eraserBtn.setVisibility(View.GONE);
                    colorBtn.setVisibility(View.GONE);
                    m_view.setOneFingerMode(true);
                } else {
                    scrollerbtn.setImageResource(R.drawable.hand_point);
                    eraserBtn.setVisibility(View.VISIBLE);
                    colorBtn.setVisibility(View.VISIBLE);
                    config.setStrokeColor(curColor);
                    config.setStrokeWidth(curVal);
                    m_view.setOneFingerMode(false);
                }
            }
        });

        eraserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_view.setOneFingerMode(false);
                if (config.getStrokeColor() == Color.WHITE) {
                    eraserBtn.setImageResource(R.drawable.pen_btn);
                    config.setStrokeColor(curColor);
                    config.setStrokeWidth(curVal);
                    config.setEraserMode(false);
                    config.setTextMode(true);
                } else {
                    eraserBtn.setImageResource(R.drawable.eraser_btn);
                    config.setStrokeColor(Color.WHITE);
                    config.setStrokeWidth(curVal);
                    config.setEraserMode(true);
                    config.setTextMode(false);
                }
            }
        });

        colorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OnlineMainActivity.this, GridViewColorActivity.class);
                intent.putExtra("curval", curVal);
                intent.putExtra("isEraser", config.isEraserMode());
                //标记是在线模式还是离线模式
                if (isLogin) {
                    intent.putExtra("state", PopupMenu.TYPE.ONLINE);
                } else {
                    intent.putExtra("state", PopupMenu.TYPE.OFFLINE);
                }
                OnlineMainActivity.this.startActivity(intent);
            }
        });

        cutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Edit by xszyou on 20170628：在线截图，离线或者画板全屏时拍照
                if (!isLogin || isFullScreen || isMsgFull) {
                    takePhoto();
                } else {//离线时截图功能变成拍照
                    contentResize.setVisibility(View.GONE);
                    cacheData(new OnSaveCallBack() {
                        @Override
                        public void saveSuccess() {
                            doScreenCut();
                        }

                        @Override
                        public void saveFail() {
                            ToastUtils.showMessage(OnlineMainActivity.this, "数据缓存失败，有可能造成数据掉失");
                            doScreenCut();
                        }
                    }, true, false);
                }
            }
        });

        //Edit by xszyou on 20170705:初始化上下页
        ImageView nextPageBtn = (ImageView) findViewById(R.id.next_page);
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPage();
            }
        });

        ImageView prePageBtn = (ImageView) findViewById(R.id.pre_page);
        prePageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perPage();
            }
        });

    }

    /**
     * Cteated by xszyou on 20170628:拍照
     */
    private void takePhoto() {
        cacheData(new OnSaveCallBack() {
            @Override
            public void saveSuccess() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //TODO 检查权限
                        ActivityCompat.requestPermissions(OnlineMainActivity.this, new String[]{android
                                .Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.CAMERA}, 1);

                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = new Date(System.currentTimeMillis());
                        String filename = format.format(date);

                        outputImage = new File(path, filename + ".jpg");
                        if (outputImage.exists()) {
                            outputImage.delete();
                        }
                        try {
                            outputImage.createNewFile();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //将File对象转换为Uri并启动照相程序
                        Uri imageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", outputImage);
                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        //指定图片输出地址
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, IMAGE_RESULT_CODE);
                    }
                }, 300);
            }

            @Override
            public void saveFail() {
                ToastUtils.showMessage(OnlineMainActivity.this, "数据缓存失败，无法打开照相机");
            }
        }, true, false);
    }

    //Edit by xszyou on 20170615:新建空白页或配上截图
    private void doNewPage(boolean cutScreen) {
        String teacherName = DefaultPrefsUtil.getTeacherName();
        String coursecode = DefaultPrefsUtil.getCourseCode();
        UploadModel uploadModel = new UploadModel();
        page = page + 1;
        curPage = page;
        m_view.clear();

        uploadModel.setStatus(UploadModel.STATUS_NOT_SAVE);
        uploadModel.setCoursename(coursecode);
        uploadModel.setTeacher(teacherName);
        uploadModel.setPage(page);
        uploadModel.setUid(uuid);
        uploadDAO.save(uploadModel);
        if (cutScreen) {
            cutScreen();
        }
    }

    /**
     * Edit by xszyou on 20170611:拍照后传图新建一页
     */
    private void doNewPageByCamera(Bitmap bitmap) {
        String teacherName = DefaultPrefsUtil.getTeacherName();
        String coursecode = DefaultPrefsUtil.getCourseCode();
        UploadModel uploadModel = new UploadModel();
        page = page + 1;
        curPage = page;
        m_view.clear();

        uploadModel.setStatus(UploadModel.STATUS_NOT_SAVE);
        uploadModel.setCoursename(coursecode);
        uploadModel.setTeacher(teacherName);
        uploadDAO.save(uploadModel);
        uploadModel.setPage(page);
        uploadModel.setUid(uuid);

        pagetv.setText("(" + curPage + "/" + page + ")");
        m_view.setBitmap(bitmap);
        m_view.initff();
    }

    /**
     * Edit by xszyou on 20170611:初始化数据
     */
    void initFirst() {
        List<UploadModel> list = uploadDAO.findByUID(uuid);
        if (list != null && !list.isEmpty()) {
            page = list.size();
            curPage = 1;
            loadData(curPage);
        }
    }

    /**
     * 返回按钮
     */
    void initBackHome() {
        backhome = (ImageView) findViewById(R.id.backhome);
        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showDialog(OnlineMainActivity.this, "你确定要退出课堂吗？", new DialogCallback() {
                    @Override
                    public void callback() {
                        cacheData(new OnSaveCallBack() {
                            @Override
                            public void saveSuccess() {
                                saveAllToNative(new OnSaveCallBack() {
                                    @Override
                                    public void saveSuccess() {
                                        ToastUtils.showMessage(OnlineMainActivity.this, "保存成功");
                                        if (isLogin) {
                                            stopContentPlay(null);
                                            mConnection.disconnect();
                                        }
                                        finish();
                                    }

                                    @Override
                                    public void saveFail() {
                                        ToastUtils.showMessage(OnlineMainActivity.this, "保存失败");
                                    }
                                }, false);
                            }

                            @Override
                            public void saveFail() {
                                ToastUtils.showMessage(OnlineMainActivity.this, "数据缓存失败，无法保存数据");
                            }
                        }, true, false);

                    }
                }, new IDialogCancelCallback() {
                    @Override
                    public void cancelCallback() {
                    }
                });
            }
        });
    }

    /**
     * Edit by xszyou on 20170611:截屏或截课件图
     */
    private void cutScreen() {
        if (contentisPlaying) {
            String imagePath = Environment.getExternalStorageDirectory().getPath() + "/play_cap.png";
            Bitmap mBitmap = null;
            int bitmapX = 10;
            int bitmapY;

            if (!sbDefault.isChecked()) {
                bitmapY = findViewById(R.id.topPanel).getHeight() + 10;
            } else {
                bitmapY = findViewById(R.id.topPanel).getHeight() + toolsBar.getHeight() + 10;
            }
            mBitmap = ImgUtil.startCapture(mImageReader, imagePath, bitmapX, bitmapY,
                    contentnpv.getWidth() - 10, contentnpv.getHeight() - 15);

            if (mBitmap != null) {
                m_view.setBitmap(mBitmap);
                m_view.initff();
            }
            contentResize.setVisibility(View.VISIBLE);
        } else {
            evaluateJavascript(webView);
        }
    }

    void addWebView() {
        final String url = Constant.getBaseurl() + "/wPaint/student/coursewave/list.do?hardwareId="
                + SysUtils.android_id(context) + "&courseCode=" + courseCode;
        final LinearLayout layout = (LinearLayout) findViewById(R.id.showview);
        sbDefault = (SwitchButton) findViewById(R.id.sb_default);
        toolsBar = (LinearLayout) findViewById(R.id.toolsbar);
        webView = (WebView) findViewById(R.id.loadwebview);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setDrawingCacheEnabled(true);
        //设置 缓存模式
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient() {
            //覆盖shouldOverrideUrlLoading 方法
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }
        });

        toolsBar.setVisibility(View.GONE);
        sbDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /**
                 * Edit by xszyou on 20170618:视讯和课件模式切换
                 */
                if (isChecked) {
                    webView.setVisibility(View.VISIBLE);
                    toolsBar.setVisibility(View.VISIBLE);
                    initToolsBar(webView);
                    //Edity by xszyou on 20170612:横屏隐藏IM
                    if (isHeng) {
                        //Eiit by xszyou on 20170628：如果IM全屏需要先取消IM全屏
                        if (isMsgFull) {
                            msgResize.callOnClick();
                        }
                        //Edit by xszyou on 20170626：隐藏IM和部分全屏按钮
                        findViewById(R.id.msg_layout).setVisibility(View.GONE);
                    } else if (isContentFull) {
                        contentResize.callOnClick();
                    }
                    resizeBtnShow(false, canvasResize, kjResize);
                    //Edit by xszyou on 20170626:停止播放共享桌面
                    stopContentPlay(layout);
                } else {
                    toolsBar.setVisibility(View.GONE);
                    webView.setVisibility(View.GONE);
                    //Edity by xszyou on 20170612:横屏显示IM
                    if (isHeng) {
                        findViewById(R.id.msg_layout).setVisibility(View.VISIBLE);
                        resizeBtnShow(true, kjResize);
                    } else {
                        if (isContentFull) {
                            contentResize.callOnClick();
                        }
                        resizeBtnShow(true, msgResize, kjResize);
                    }
                    //Edit by xszyou on 20170626:播放共享桌面
                    initVodPlayer(layout, layout.getLayoutParams().width, layout.getLayoutParams().height);
                }
            }
        });
    }

    /**
     * 加载右上角弹出框
     **/
    private void initPopup() {
        if (isLogin) {
            popupMenu = new PopupMenu(this, PopupMenu.TYPE.ONLINE, isHeng);
        } else {
            popupMenu = new PopupMenu(this, PopupMenu.TYPE.OFFLINE, isHeng);
        }
        rightBtn = (ImageView) findViewById(R.id.rightbtn);
        rightBtn.setOnClickListener(this);
    }

    /**
     * Edit by xszyou on 20170611:截屏
     * 5.0系统截图
     */
    private void initCut() {
        mImagePath = Environment.getExternalStorageDirectory().getPath() + "/screenshort/";
        mMediaProjectionManager = (MediaProjectionManager) getApplicationContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        windowHeight = wm.getDefaultDisplay().getHeight();
        windowWidth = wm.getDefaultDisplay().getWidth();
        screenDensity = displayMetrics.densityDpi;
        mImageReader = ImageReader.newInstance(windowWidth, windowHeight, PixelFormat.RGBA_8888, 4);

        //申请截屏权限
        startActivityForResult(
                mMediaProjectionManager.createScreenCaptureIntent(), SCREENCUT);
    }

    private void intCanvas() {
        m_view = (DrawableView) this.findViewById(R.id.paintView);
        config.setStrokeColor(Color.BLACK);
        //设置背景边框
        config.setShowCanvasBounds(false);
        config.setStrokeWidth(2.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        canvasheight = 2400;
        canvaswidth = 2400;

        config.setCanvasHeight(canvasheight);
        config.setCanvasWidth(canvaswidth);
        m_view.setConfig(config);
    }

    private void nextPage() {
        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(OnlineMainActivity.this, "数据载入中...");
        cacheData(new OnSaveCallBack() {
            @Override
            public void saveSuccess() {
                doNextPage();
                closeDialog();
            }

            @Override
            public void saveFail() {
                ToastUtils.showMessage(OnlineMainActivity.this, "数据缓存失败，有可能造成数据掉失");
                closeDialog();
                doNextPage();
            }
        }, false, false);
    }

    private void perPage() {
        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(OnlineMainActivity.this, "数据载入中...");
        cacheData(new OnSaveCallBack() {
            @Override
            public void saveSuccess() {
                doPerPage();
                closeDialog();
            }

            @Override
            public void saveFail() {
                ToastUtils.showMessage(OnlineMainActivity.this, "数据缓存失败，有可能造成数据掉失");
                doPerPage();
                closeDialog();
            }
        }, false, false);
    }

    /**
     * Edit by xszyou on 20170610：下一页
     */
    private void doNextPage() {
        if (curPage == page) {
            Toast.makeText(OnlineMainActivity.this, "此页为最后一页", Toast.LENGTH_LONG).show();
            return;
        } else {
            curPage = curPage + 1;
            m_view.clear();
        }
        this.loadData(curPage);
        pagetv.setText("(" + curPage + "/" + page + ")");
    }

    /**
     * Edit by xszyou on 20170611:上一页
     */
    private void doPerPage() {
        if (curPage == 1) {
            Toast.makeText(OnlineMainActivity.this, "此页为第一页", Toast.LENGTH_LONG).show();
            return;
        } else {
            curPage = curPage - 1;
            m_view.clear();
        }
        this.loadData(curPage);
        pagetv.setText("(" + curPage + "/" + page + ")");
    }

    private void loadPage(int loadpage) {
        List<UploadModel> list = uploadDAO.findByUID(uuid);
        curPage = loadpage + 1;
        page = list.size();

        m_view.clear();
        loadData(curPage);
        pagetv.setText("(" + curPage + "/" + page + ")");
    }

    /**
     * 删除页数
     *
     * @param delpage 从1开始
     */
    private void deletePage(int delpage) {
//        if (page <= 1) {
//            m_view.clear();
//            return;
//        }

        UploadModel uploadModel = uploadDAO.findUploadByPageAndUID(delpage, uuid);
        String bitmapFileName = uploadModel.getMixpic();
        if (bitmapFileName != null && !"".equals(bitmapFileName.trim())) {
            File file = new File(bitmapFileName);
            if (file.exists()) {
                file.delete();
            }
        }

        String pathsFileName = uploadModel.getCanvaspic();
        if (pathsFileName != null && !"".equals(pathsFileName.trim())) {
            File file = new File(pathsFileName);
            if (file.exists()) {
                file.delete();
            }
        }

        String bitPicFileName = uploadModel.getBigpic();
        if (bitPicFileName != null && !"".equals(bitPicFileName.trim())) {
            File file = new File(bitPicFileName);
            if (file.exists()) {
                file.delete();
            }
        }

        uploadDAO.deleteByPageAndUID(delpage, uuid);
        if (delpage <= 1) {
            page = 0;
            doNewPage(false);
        } else {
            this.loadPage(page - 2);
        }
    }

    /**
     * Created by xszyou on 20170626:停止播入,并从layou 中删除
     */
    private void stopContentPlay(ViewGroup layout) {
        if (mPlayer != null) {
            mPlayer.stop();
        }
        if (layout != null) {
            layout.removeView(contentnpv);
        }
        contentisPlaying = false;
    }

    //阿里云播放器代码

    private void initSurfaceView() {
        //查询播放视图
        contentnpv = (SurfaceView) findViewById(R.id.content);
        contentnpv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
                holder.setKeepScreenOn(true);
                // Important: surfaceView changed from background to front, we need reset surface to mediaplayer.
                // 对于从后台切换到前台,需要重设surface;部分手机锁屏也会做前后台切换的处理
                if (mPlayer != null) {
                    mPlayer.setVideoSurface(contentnpv.getHolder().getSurface());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                if (mPlayer != null) {
                    mPlayer.setSurfaceChanged();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
    }

    private void initVodPlayer(ViewGroup layout, Integer contentWidth, Integer contentHeight) {
        if (layout != null) {
            layout.addView(contentnpv);
        }
        ViewGroup.LayoutParams layoutParams = contentnpv.getLayoutParams();
        if (contentWidth != null) {
            layoutParams.width = contentWidth;
        }
        if (contentHeight != null) {
            layoutParams.height = contentHeight;
        }

        //创建player对象
        mPlayer = new AliVcMediaPlayer(context, contentnpv);

        // 设置图像适配屏幕，适配最长边
        mPlayer.setVideoScalingMode(MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        // 设置图像适配屏幕，适配最短边，超出部分裁剪
//        mPlayer.setVideoScalingMode(MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);

        //设置缺省编码类型：0表示硬解；1表示软解；
        //如果缺省为硬解，在使用硬解时如果解码失败，会尝试使用软解
        //如果缺省为软解，则一直使用软解，软解较为耗电
        //由于android手机硬件适配性的问题，很多android手机的硬件解码会有问题，建议尽量使用软件解码。
        mPlayer.setDefaultDecoder(1);

        /**
         * 设置连接超时时长,单位毫秒.默认一直等待.
         * 连接部分RTMP服务器,握手并连接成功后,当播放一个不存在的流地址时,会一直等待下去.
         */
        mPlayer.setTimeout(30 * 1000);

        /**
         * 设置最大缓冲区时长,单位毫秒.此参数关系视频最大缓冲时长.
         * RTMP基于TCP协议不丢包,网络抖动且缓冲区播完,之后仍然会接受到抖动期的过期数据包.
         */
        mPlayer.setMaxBufferDuration(4000);
        mPlayer.setMediaType(MediaPlayer.MediaType.Live);
//        mPlayer.setVideoSizeChangeListener(new VideoSizeChangelistener());    //画面大小变化事件
        //播放器就绪事件
        mPlayer.setPreparedListener(new MediaPlayer.MediaPlayerPreparedListener() {
            @Override
            public void onPrepared() {
            }
        });

        //信息状态监听事件
        mPlayer.setFrameInfoListener(new MediaPlayer.MediaPlayerFrameInfoListener() {
            @Override
            public void onFrameInfoListener() {
                Map<String, String> debugInfo = mPlayer.getAllDebugInfo();
                long createPts = 0;
                if (debugInfo.get("create_player") != null) {
                    String time = debugInfo.get("create_player");
                    createPts = (long) Double.parseDouble(time);
                }
                if (debugInfo.get("open-url") != null) {
                    String time = debugInfo.get("open-url");
                    long openPts = (long) Double.parseDouble(time) + createPts;
                }
                if (debugInfo.get("find-stream") != null) {
                    String time = debugInfo.get("find-stream");
                    long findPts = (long) Double.parseDouble(time) + createPts;
                }
                if (debugInfo.get("open-stream") != null) {
                    String time = debugInfo.get("open-stream");
                    long openPts = (long) Double.parseDouble(time) + createPts;
                }
            }
        });

        //异常错误事件
        mPlayer.setErrorListener(new MediaPlayer.MediaPlayerErrorListener() {
            @Override
            public void onError(int i, String msg) {
            }
        });
        //播放结束事件
        mPlayer.setCompletedListener(new MediaPlayer.MediaPlayerCompletedListener() {
            @Override
            public void onCompleted() {
            }
        });
        //已经停止播放事件
        mPlayer.setStopedListener(new MediaPlayer.MediaPlayerStopedListener() {
            @Override
            public void onStopped() {
                Log.i("lin", "onStopped: stop");
            }
        });
        //缓冲信息更新事件
        mPlayer.setBufferingUpdateListener(new MediaPlayer.MediaPlayerBufferingUpdateListener() {
            @Override
            public void onBufferingUpdateListener(int percent) {
            }
        });
//        mPlayer.prepareAndPlay("rtmp://live.hkstv.hk.lxdns.com/live/hks");
//        mPlayer.prepareAndPlay("http://flv15.quanmin.tv/live/177_L4.flv");
        mPlayer.prepareAndPlay(Constant.getOnlineurl() + "screen_" + courseCode);
        contentisPlaying = true;
    }

    private int connetCount = 0;

    private void initWebSocket() {
        //注意连接和服务名称要一致
        /*final String wsuri = "ws://192.168.0.2：8080/st/sosWebSocketService?userCode="
                + spu.getValue(LoginActivity.STR_USERNAME);*/
        String wsuri = Constant.getWsurl() + "/ws/chat.do?hardwareId="
                + SysUtils.android_id(OnlineMainActivity.this) + "&courseCode=" + courseCode;
        try {
            mConnection.connect(wsuri, new mWebSocketHandler());
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    private class mWebSocketHandler extends WebSocketHandler {
        @Override
        public void onOpen() {
            connetCount = 0;
        }

        @Override
        public void onTextMessage(String text) {
            Gson gson = new Gson();
            ChatMsgEntity entity = new ChatMsgEntity();
            TbChatMsg chatMsg = gson.fromJson(text, TbChatMsg.class);
            entity.setName(chatMsg.getFromUserName());
            entity.setMessage(chatMsg.getMsg());
            entity.setDate(getDate());
            send(0, true, entity);
        }

        @Override
        public void onBinaryMessage(byte[] payload) {
            super.onBinaryMessage(payload);
        }

        @Override
        public void onClose(int code, String reason) {
            if (connetCount < 5) {
                connetCount++;
                initWebSocket();
            }
        }
    }

    public void sendXxx(String msg) {
        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setDate(getDate());
        entity.setMsgType(false);
        entity.setMessage(msg);
        entity.setName("我");
        if (mConnection.isConnected()) {
            String str = "{\"fromUserId\":\"" + SysUtils.android_id(OnlineMainActivity.this)
                    + "\",\"courseCode\":\"" + courseCode + "\",\"toUserId\":\"\",\"msg\":\""
                    + msg + "\"}";
            mConnection.sendTextMessage(str);
        }
        send(0, false, entity);
    }

    /***
     * 发送图片
     * @param picPath
     */
    public void sendPic(String picPath) {
        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setMessage(picPath);
        entity.setDate(getDate());
        entity.setMsgType(false);
        entity.setName("我");

        Bitmap bitmap = BitmapUtil.loadBitmapFromSDCard(picPath);
        if (mConnection.isConnected()) {
            if (bitmap != null) {
                byte[] msgByte = ImgUtil.getBitmapByte(bitmap);
                mConnection.sendBinaryMessage(msgByte);
            }
        }
        send(1, false, entity);
    }

    /**
     * 初始化view
     * Edty by xszyou on 20170618:初始化IM view
     */
    public void initView() {
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
        mListView = (ListView) findViewById(R.id.msglistview);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        try {
            mBtnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String contString = mEditTextContent.getText().toString();
                    if (contString.length() > 0) {
                        sendXxx(contString);
                    }
                    //   sendPic("/storage/emulated/0/pic/8ae9a52b103e4eef97379cc437ec7f97.jpg");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟加载消息历史，实际开发可以从数据库中读出
     */
    public void initData() {
        //Edit by xszyou on 20170706:横竖屏切换时还原消息列表
        Intent intent = this.getIntent();
        String msgs = (String) intent.getSerializableExtra("msgs");
        if (msgs != null && !msgs.isEmpty()) {
            GsonBuilder setedBuilder = new GsonBuilder();
            Gson gson = setedBuilder.create();
            mDataArrays = gson.fromJson(msgs,
                    new TypeToken<ArrayList<ChatMsgEntity>>() {
                    }.getType());
        }
        mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
        mListView.setAdapter(mAdapter);
    }

    /**
     * 发送消息
     *
     * @param type 消息类型
     * @param send 是否是接收，true接收，false发送
     */
    private void send(int type, boolean send, ChatMsgEntity msg) {
        ChatMsgEntity entity = new ChatMsgEntity();
        entity.setMessage(msg.getMessage());
        entity.setDate(msg.getDate());
        entity.setName(msg.getName());
        entity.setMsgType(send);
        entity.setType(type);

        mDataArrays.add(entity);
        // 清空编辑框数据
        mEditTextContent.setText("");
        // 通知ListView，数据已发生改变
        mAdapter.notifyDataSetChanged();
        // 发送一条消息时，ListView显示选择最后一项
        mListView.setSelection(mListView.getCount() - 1);
    }

    /**
     * 发送消息时，获取当前事件
     *
     * @return 当前时间
     */
    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    /**
     * Edit by xszyou on 20170616:缓存单页数据
     *
     * @param saveCallBack
     * @param showLoading
     */
    private void cacheData(final OnSaveCallBack saveCallBack, final boolean showLoading, final boolean isOutputToFile) {
        if (showLoading) {
            mWeiboDialog = WeiboDialogUtils.createLoadingDialog(OnlineMainActivity.this, "数据缓存中...");
        }
        new Handler().postDelayed(new Runnable() {  //子线程处理保存数据
            public void run() {
                try {
                    UploadModel upload = uploadDAO.findUploadByPageAndUID(curPage, uuid);
                    //照相、图片
                    Bitmap bmp = m_view.getBitmap();
                    if (bmp != null) {
                        String filename = null;
                        if (upload.getMixpic() != null) {
                            filename = upload.getMixpic();
                        } else {
                            filename = Environment.getExternalStorageDirectory()
                                    + "/mixpic/" + UUIDUtils.getUuid() + ".jpk";
                        }
                        if (isOutputToFile) {//要主动保存到本地
                            BitmapUtil.saveBitmapToSDCard(bmp, filename);
                        }
                        upload.setMixpic(filename);
                        pics.put(curPage, bmp);
                        upload.setStatus(UploadModel.STATUS_NOT_SAVE);//标记未保存到本地
                    }

                    //用户自己画
                    if (m_view.paths != null) {
                        String repath = null;
                        if (upload.getCanvaspic() != null) {
                            repath = upload.getCanvaspic();
                        } else {
                            repath = Constant.generateCanvaspicPath(UUIDUtils.getUuid(), ".online");
                        }
                        if (isOutputToFile) {
                            SerializeUtils.serialization(repath, m_view.paths);
                        }
                        ArrayList<SerializablePath> pathsTmp = new ArrayList<SerializablePath>();
                        pathsMap.put(curPage, pathsTmp);
                        pathsTmp.addAll(m_view.paths);
                        //标记未保存到本地
                        upload.setStatus(UploadModel.STATUS_NOT_SAVE);
                        upload.setCanvaspic(repath);
                    }

                    //Edit by xszyou on 20170617:临时保存画板最终效果图
                    Bitmap bigBitmap = m_view.obtainBitmapByP();
                    if (bigBitmap != null) {
                        String repath = null;
                        if (upload.getBigpic() != null) {
                            repath = upload.getBigpic();
                        } else {
                            repath = Environment.getExternalStorageDirectory() + "/bigpic/" + UUIDUtils.getUuid() + ".jpk";
                        }
                        if (isOutputToFile) {
                            BitmapUtil.saveBitmapToSDCard(bigBitmap, repath);
                        }
                        //标记未保存到本地
                        upload.setStatus(UploadModel.STATUS_NOT_SAVE);
                        bigpicsCacheMap.put(curPage, bigBitmap);
                        upload.setBigpic(repath);
                    }
                    upload.setPage(curPage);
                    uploadDAO.update(upload);

                    if (saveCallBack != null) {
                        saveCallBack.saveSuccess();
                    }
//                    if (showLoading) {
//                        closeDialog(); //发送消息通知主线程，只有主线程才能操作UI
//                    }
                } catch (Exception e) {
                    if (showLoading) {
                        closeDialog();
                    }
                    if (saveCallBack != null) {
                        saveCallBack.saveFail();
                    }
                    e.printStackTrace();
                }
            }
        }, 200);
    }

    /**
     * Edit by xszyou on 20170611:保存所有更新到本地
     *
     * @param saveCallBack
     * @param showLoading
     */
    private void saveAllToNative(final OnSaveCallBack saveCallBack, final boolean showLoading) {
        final String title = titleTV.getText().toString();
        titleTV.setText(title + "...");
        savingNative = true;
        if (showLoading) {
            mWeiboDialog = WeiboDialogUtils.createLoadingDialog(OnlineMainActivity.this, "永久保存中...");
        }
        new Handler().postDelayed(new Runnable() {  //子线程处理保存数据
            @Override
            public void run() {
                try {
                    //Edit by xszyou on 20170611:所有动过的从新bitmap 保存到本地
                    if (pics != null && !pics.isEmpty()) {
                        Set<Map.Entry<Integer, Bitmap>> entrySet = pics.entrySet();
                        Iterator<Map.Entry<Integer, Bitmap>> iterator = entrySet.iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Integer, Bitmap> entry = iterator.next();
                            UploadModel upload = uploadDAO.findUploadByPageAndUID(entry.getKey(), uuid);
                            if (upload != null) {
                                BitmapUtil.saveBitmapToSDCard(entry.getValue(), upload.getMixpic());
                            }
                        }
                        pics.clear();
                    }
                    //Edit by xszyou on 20170611:所有动过的页面的paths重新保存到本地
                    if (pathsMap != null && !pathsMap.isEmpty()) {
                        Set<Map.Entry<Integer, ArrayList<SerializablePath>>> entrySet = pathsMap.entrySet();
                        Iterator<Map.Entry<Integer, ArrayList<SerializablePath>>> iterator = entrySet.iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Integer, ArrayList<SerializablePath>> entry = iterator.next();
                            UploadModel upload = uploadDAO.findUploadByPageAndUID(entry.getKey(), uuid);
                            if (upload != null) {
                                SerializeUtils.serialization(upload.getCanvaspic(), entry.getValue());
                            }
                        }
                        pathsMap.clear();
                    }
                    //Edit by xszyou on 20170617:所有动过的新最终效果图保存到本地
                    if (bigpicsCacheMap != null && !bigpicsCacheMap.isEmpty()) {
                        Set<Map.Entry<Integer, Bitmap>> entrySet = bigpicsCacheMap.entrySet();
                        Iterator<Map.Entry<Integer, Bitmap>> iterator = entrySet.iterator();
                        while (iterator.hasNext()) {
                            Map.Entry<Integer, Bitmap> entry = iterator.next();
                            UploadModel upload = uploadDAO.findUploadByPageAndUID(entry.getKey(), uuid);
                            if (upload != null) {
                                BitmapUtil.saveBitmapToSDCard(entry.getValue(), upload.getBigpic());
                            }
                        }
                        bigpicsCacheMap.clear();
                    }
                    uploadDAO.updateAllStatus(UploadModel.STATUS_SAVED, uuid);//记录所有数据已经保存
                    if (saveCallBack != null) {
                        saveCallBack.saveSuccess();
                    }
                    closeDialog();
                    uiHandler.sendEmptyMessage(1);
                    savingNative = false;//保存中
                } catch (Exception e) {
                    if (showLoading) {
                        closeDialog();
                    }
                    if (saveCallBack != null) {
                        saveCallBack.saveFail();
                    }
                    e.printStackTrace();
                    savingNative = false;
                }
            }
        }, 300);

    }

    /**
     * Create by lingjingsheng
     * Edit by xszyou on 20170617:重构
     */
    public void upload(final List<UploadModel> uploadModels, final int index) {
        String pic = uploadModels.get(index).getBigpic();
        String uuid = DefaultPrefsUtil.getUUID();
        final int size = uploadModels.size();

        if (pic == null || "".equals(pic.trim())) {
            if (++i <= size) {
                upload(uploadModels, i - 1);
            }
        } else {
            File bpFile = new File(pic);
            //文件已经掉失
            if (!bpFile.exists()) {
                if (++i <= size) {
                    upload(uploadModels, i - 1);
                }
            } else {
                RequestParams requestParams = new RequestParams(Constant.getBaseurl() + "/eapi/note/submit.do"); // 默认编码UTF-8
                requestParams.addBodyParameter("note", new File(pic));
                mDialog.setMax(100);

                if (isLogin) {
                    requestParams.addBodyParameter("type", "ON");
                } else {
                    requestParams.addBodyParameter("type", "OFF");
                }
                requestParams.addBodyParameter("hardwareId", SysUtils.android_id(context));
                requestParams.addBodyParameter("coursewarePage",
                        String.valueOf(uploadModels.get(index).getPage()));
                requestParams.addBodyParameter("courseCode", courseCode);
                requestParams.addBodyParameter("uuid", uuid);

                XHttpUtils.uploadMethod(requestParams, new Callback.CacheCallback<ResponseEntity>() {
                    @Override
                    public boolean onCache(ResponseEntity result) {
                        return false;
                    }

                    @Override
                    public void onSuccess(ResponseEntity result) {
                        Gson gson = new Gson();
                        ResponseEntityResult resultEntry = gson.fromJson(result.getResult(), ResponseEntityResult.class);
                        if (resultEntry.getResult()) {
                            UploadModel uploadModel = uploadModels.get(i - 1);
                            uploadModel.setStatus(UploadModel.STATUS_UPLOADED);
                            uploadDAO.update(uploadModel);
                            ToastUtils.showMessage(OnlineMainActivity.this, "上传成功");
                        } else {
                            Toast.makeText(OnlineMainActivity.this, "第" + i + "页上传失败", Toast.LENGTH_SHORT).show();
                        }
                        int count = 100 / size;
                        if (i == size) {
                            mDialog.dismiss();
                            mDialog.setProgress(100);
                        } else {
                            mDialog.setProgress(i * count);
                        }
                        i++;
                        if (i <= size) {
                            upload(uploadModels, i - 1);
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(OnlineMainActivity.this, "上传失败:" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Toast.makeText(OnlineMainActivity.this, "上传取消", Toast.LENGTH_SHORT).show();
                        int count = 100 / size;
                        if (i == size) {
                            mDialog.setProgress(100);
                            mDialog.dismiss();
                        } else {
                            mDialog.setProgress(i * count);
                        }
                        i++;
                        if (i <= size) {
                            upload(uploadModels, i);
                        }
                        Toast.makeText(OnlineMainActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinished() {
                    }
                });
            }
        }
    }

    private void showNormalDialog(String msg, final DialogCallback callback) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(OnlineMainActivity.this);
        normalDialog.setTitle("提醒");
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.callback();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        normalDialog.show();
    }

    /**
     * Edit by xszyou on 20170616:调相同的后退操作
     */
    @Override
    public void onBackPressed() {
        backhome.callOnClick();
    }

    @Override
    public void getMsg(String str) {
        Gson gson = new Gson();
        MsgBean msgBean = gson.fromJson(str, MsgBean.class);
        if (msgBean.getColor() != 0) {
            curColor = msgBean.getColor();
            config.setStrokeColor(msgBean.getColor());
        } else {
            config.setStrokeColor(curColor);
        }
        if (msgBean.getSeekBarVal() != 0) {
            curVal = msgBean.getSeekBarVal();
            config.setStrokeWidth(msgBean.getSeekBarVal());
        } else {
            config.setStrokeWidth(curVal);
        }
    }

    /**
     * Edit by xszyou on 20170604:获取课件图
     */
    @TargetApi(Build.VERSION_CODES.ECLAIR_MR1)
    @SuppressLint("NewApi")
    private void evaluateJavascript(WebView webView) {
        webView.evaluateJavascript("getImgBase64Data()",
                new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        String[] data = value.split(",");
                        if (data != null && data.length > 1) {
                            Bitmap pic = ImgUtil.base64ToBitmap(data[1]);
                            if (pic != null) {
                                m_view.setBitmap(pic);
                                m_view.initff();
                            }
                        }
                    }
                });
    }

    /**
     * 调用别的页面后返回的调用方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 表示 调用照相机拍照
            case IMAGE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    if (outputImage != null && outputImage.exists()) {
                        Bitmap bitmap = BitmapUtil.loadBitmapFromSDCard(outputImage.getPath());
                        bitmap = ImgUtil.showBigByWidth(1280, bitmap);
                        doNewPageByCamera(bitmap);
                        outputImage.delete();
                    }
                } else {
                    //调用拍照失败
                    Toast.makeText(OnlineMainActivity.this, "拍照失败", Toast.LENGTH_LONG).show();
                }
                closeDialog();
                break;
            // 选择图片库的图片
            case PICK:
                break;
            case SCREENCUT:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }
                mResultCode = resultCode;
                mResultData = data;

                setUpMediaProjection();
                setUpVirtualDisplay();
                break;
            default:
        }

    }

    private void setUpMediaProjection() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData);
        }
    }

    private void setUpVirtualDisplay() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenCapture",
                    windowWidth, windowHeight, screenDensity,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);
        }
    }

    /**
     * Create by xszyou on 20170617:画板加载数据
     */
    private boolean loadData(Integer curPage) {
        UploadModel uploadModel = uploadDAO.findUploadByPageAndUID(curPage, uuid);
        //Edit by xszyou on 20170617:bitmap
        if (pics.get(curPage) != null) {
            m_view.setBitmap(pics.get(curPage));
        } else {
            if (uploadModel != null && uploadModel.getMixpic() != null && !"".equals(uploadModel.getMixpic())) {
                File file = new File(uploadModel.getMixpic());
                if (file.exists()) {
                    Bitmap mixpic = BitmapUtil.loadBitmapFromSDCard(uploadModel.getMixpic());
                    if (mixpic != null) {
                        m_view.setBitmap(mixpic);
                    }
                }
            }
        }
        //Edit by xszyou on 20170617:paths
        if (pathsMap.get(curPage) != null) {
            m_view.paths.addAll(pathsMap.get(curPage));
        } else {
            if (uploadModel != null && uploadModel.getCanvaspic() != null && !"".equals(uploadModel.getCanvaspic())) {
                File file = new File(uploadModel.getCanvaspic());
                if (file.exists()) {
                    ArrayList<SerializablePath> pvbaths = (ArrayList<SerializablePath>) SerializeUtils.deserialization(uploadModel.getCanvaspic());
                    ArrayList<SerializablePath> paths = SerializeUtils.TransPath(pvbaths);
                    if (paths != null) {
                        m_view.paths.addAll(paths);
                    }
                }
            }
        }
        m_view.initff();
        return true;
    }

    private void dialog(List<UploadModel> uploadModels) {
        final CustomDialog dialog = new CustomDialog(OnlineMainActivity.this);
        final EditText editText = (EditText) dialog.getEditText();
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 1;
                courseCode = editText.getText().toString().trim();
                List<UploadModel> uploadModels = uploadDAO.findByStatusAndUID(1, uuid);
                if (uploadModels != null && uploadModels.size() > 0) {
                    dialog.dismiss();
                    mDialog = new CommonProgressDialog(OnlineMainActivity.this, new DialogListener());
                    mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setMessage("正在上传");
                    mDialog.show();
                    upload(uploadModels, 0);
                }
            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void closeDialog() {
        mHandler.sendEmptyMessageDelayed(1, 100);
    }

    private void doScreenCut() {
        WeiboDialogUtils.closeDialog(mWeiboDialog);
        mHandler.sendEmptyMessageDelayed(6, 300);
    }

    private void changeCurrentPage() {
        mHandler.sendEmptyMessage(1);
    }

    private void initScreen() {
        //Edit by xszyou on 20170704：记录屏幕大小，按固定竖屏方向记录，小的是宽度，大的是高度
        dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        if (screenHeight < screenWidth) {
            constant.setDmWidth(screenHeight);
            constant.setDmHeight(screenWidth);
        } else {
            constant.setDmWidth(screenWidth);
            constant.setDmHeight(screenHeight);
        }

        if (isHeng) {
            setContentView(R.layout.activity_online_main);
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setContentView(R.layout.activity_online_main_v);
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 5;
            mHandler.sendMessage(message);
        }
    };

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void networdNotifyChange(NetworkInfo info) {
        boolean isNeedConnect = mConnection != null && !mConnection.isConnected();
        if (isLogin && isNeedConnect) {
            mConnection.disconnect();
            initWebSocket();
            mPlayer.stop();
//            mPlayer.prepareAndPlay("rtmp://live.hkstv.hk.lxdns.com/live/hks");
            mPlayer.prepareAndPlay(Constant.getOnlineurl() + "screen_" + courseCode);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //5分钟自动保存一次到本地
//        timer.schedule(task, 300000, 300000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopContentPlay(null);
        if (mImageReader != null) {
            mImageReader.close();
        }
        if (networkWatcher != null) {
            networkWatcher.release();
        }
    }
}
