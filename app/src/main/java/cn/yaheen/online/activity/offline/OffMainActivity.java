package cn.yaheen.online.activity.offline;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.yaheen.online.Contral.BitmapUtil;
import cn.yaheen.online.R;
import cn.yaheen.online.activity.GridViewColorActivity;
import cn.yaheen.online.activity.MainActivity;
import cn.yaheen.online.app.OnlineApp;
import cn.yaheen.online.bean.MsgBean;
import cn.yaheen.online.bean.ResponseEntity;
import cn.yaheen.online.bean.ResponseEntityResult;
import cn.yaheen.online.dao.UploadDAO;
import cn.yaheen.online.interfaces.OnSaveCallBack;
import cn.yaheen.online.model.UploadModel;
import cn.yaheen.online.receiver.Receiver;
import cn.yaheen.online.utils.Constant;
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
import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;
import me.panavtec.drawableview.draw.SerializablePath;
import me.panavtec.drawableview.utils.SerializeUtils;

public class OffMainActivity extends Activity implements Receiver.Message {

    private DrawableView m_view;
    private UploadDAO uploadDAO = null;
    private final int IMAGE_RESULT_CODE = 2;// 表示打开照相机
    private final int PICK = 1;// 选择图片库
    private int curPage = 0; //当前页，用于翻页
    private  int curColor=Color.BLACK; //当前画笔颜色
    private  int curVal=2; //当前画笔大小
    Receiver myReceiver;

    private DrawableViewConfig config = new DrawableViewConfig();
    private DisplayMetrics dm;
    private int canvasHight = 0;
    private ImageView addMoreBtn;
    private PopupMenu popupMenu;  //右上角弹出框
    private int page = 1;
    CommonProgressDialog mDialog;
    private  ImageView scrollerbtn; //模式变化按钮
    private  int i=1;
    private Map<Integer, Bitmap> pics = new HashMap<>();

    /*
    移动图片

     */
    private int lastX, lastY;
    private long mLastTime = 0, mCurTime = 0; //双击记录点击时间
    private boolean touchTwo = false, isFirstTouch = true;  //是否第二次点击
    private int height = 0, width = 0;  //记录第一次点击时的框框大小
    private int multiple = 2;  //放大倍数
    private int multi = 2;  //缩小倍数
    String uuid = "";
    private Constant constant = null;

    private ImageView backhome;

    SharedPreferencesUtils preferencesUtils = null;

    private TextView pagetv;
    private String courseCode; //课程编号

    private boolean isNew = false; //是否新建
    private Dialog mWeiboDialog=null;  //加载中的弹框
    Timer timer = new Timer();


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    pagetv.setText("("+curPage+"/"+page+")");
                    WeiboDialogUtils.closeDialog(mWeiboDialog);
                    break;
                case 2:
                    pagetv.setText("("+curPage+"/"+page+")");

                    break;
                case 6:
                    OnSaveClick(new OnSaveCallBack() {
                        @Override
                        public void saveSuccess() {

                        }

                        @Override
                        public void saveFail() {
                            ToastUtils.showMessage(OffMainActivity.this,"保存失败");
                        }
                    }, true);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off_main);

        try {
            OnlineApp.getInstance().addActivity(this);

            preferencesUtils = SharedPreferencesUtils.createSharedPreferences("online", OffMainActivity.this);
            Boolean screen = DefaultPrefsUtil.getIsHorizontalScreen();
            timer.schedule(task, 300000, 300000); // 5分钟后执行task,经过5分钟再次执行

            DefaultPrefsUtil.setIpUrl("");
            if (screen) {

                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {

                this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                //
            }
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            m_view = (DrawableView) this.findViewById(R.id.paintView);
            config.setStrokeColor(Color.BLACK);
            config.setShowCanvasBounds(true);
            config.setStrokeWidth(5.0f);
            config.setMinZoom(1.0f);
            config.setStrokeWidth(2);
            config.setMaxZoom(3.0f);
            config.setCanvasHeight(3200);
            config.setCanvasWidth(3200);
            m_view.setBackgroundColor(Color.WHITE);
            m_view.setConfig(config);
            LayoutInflater inflater = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            initPic();
            ReceiverBost();
            constant = Constant.createConstant(OffMainActivity.this);

            if (uploadDAO == null) {
                uploadDAO = new UploadDAO();
            }

            if (preferencesUtils == null) {
                preferencesUtils = SharedPreferencesUtils.createSharedPreferences("online", OffMainActivity.this);
            }
            uuid = DefaultPrefsUtil.getUUID();

            initPopup();
            initPage();
            initbackhome();
            changeScroller();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 注册广播
     */
    private void ReceiverBost() {
        myReceiver = new Receiver();  //动态注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cn.yaheen.color");
        registerReceiver(myReceiver, intentFilter);

        //因为这里需要注入Message，所以不能在AndroidManifest文件中静态注册广播接收器
        myReceiver.setMessage(this);
    }


    /**
     * 初始化返回按钮
     */
    void initbackhome() {
        backhome = (ImageView) findViewById(R.id.backhome);
        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showNormalDialog("确定要退出本页码？", new DialogCallback() {
                    @Override
                    public void callback() {
                        OnSaveClick(new OnSaveCallBack() {
                            @Override
                            public void saveSuccess() {
                                Intent intent = new Intent();
                                intent.setClass(OffMainActivity.this, MainActivity.class);
                                startActivity(intent);

                            }

                            @Override
                            public void saveFail() {
                                ToastUtils.showMessage(OffMainActivity.this, "保存失败");
                                Intent intent = new Intent();
                                intent.setClass(OffMainActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        },true);

                    }
                });

            }
        });
    }

    void initPage() {
        pagetv = (TextView) findViewById(R.id.pagetv);
        pagetv.setText("(" + page + "/" + page + ")");
    }


    private  void changeScroller(){
        scrollerbtn=(ImageView)findViewById(R.id.scroller);
        scrollerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!m_view.isOneFingerMode()){

                    m_view.setOneFingerMode(true);
                    config.setStrokeColor(Color.WHITE);
                    scrollerbtn.setImageResource(R.drawable.hand_point);
                }else {
                    m_view.setOneFingerMode(false);
                    config.setStrokeColor(curColor);
                    config.setStrokeWidth(curVal);
                    scrollerbtn.setImageResource(R.drawable.pen_icon);

                }


            }
        });

    }


    /**
     * 加载浮动截图框
     */
    private void initPic() {


        dm = getResources().getDisplayMetrics();

        addMoreBtn = (ImageView) findViewById(R.id.buttonnew);
        addMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnSaveClick(new OnSaveCallBack() {
                    @Override
                    public void saveSuccess() {

                            new Handler().postDelayed(new Runnable(){
                                public void run(){
                                    OnNewClick(null);
                                }
                            }, 1000);


                    }

                    @Override
                    public void saveFail() {
                        new Handler().postDelayed(new Runnable(){
                            public void run(){
                                OnNewClick(null);
                            }
                        }, 1000);
                    }
                },true);

            }
        });


    }


    /*
       加载右上角弹出框
        */
    private void initPopup() {
        try {
            popupMenu = new PopupMenu(this, PopupMenu.TYPE.OFFLINE, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ImageView rightBtn = (ImageView) findViewById(R.id.rightbtn);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                popupMenu.showLocation(R.id.rightbtn);// 设置弹出菜单弹出的位置
                // 设置回调监听，获取点击事件
                popupMenu.setOnItemClickListener(new PopupMenu.OnItemClickListener() {

                    @Override
                    public void onClick(PopupMenu.MENUITEM item) {
                        if (item == PopupMenu.MENUITEM.ITEM1) {
                            OnSaveClick(new OnSaveCallBack() {
                                @Override
                                public void saveSuccess() {
                                    ToastUtils.showMessage(OffMainActivity.this, "保存成功");
                                }

                                @Override
                                public void saveFail() {
                                    ToastUtils.showMessage(OffMainActivity.this, "保存失败");

                                }
                            },true);
                        } else if (item == PopupMenu.MENUITEM.ITEM2) {
                            OnSaveClick(null,true);
                            dialog(view);
                            // upload();
                        } else if (item == PopupMenu.MENUITEM.ITEM3) {

//                            if (config.getStrokeColor()==Color.WHITE){
//                                config.setStrokeColor(Color.BLACK);
//                            }else {
//                                config.setStrokeColor(Color.WHITE);
//                            }


                            if (config.getStrokeColor() == Color.WHITE) {
                                config.setStrokeColor(curColor);
                                config.setStrokeWidth(curVal);
                                popupMenu.setEraserText("工具/笔");
                            } else {
                                config.setStrokeColor(Color.WHITE);
                                config.setStrokeWidth(curVal);
                                popupMenu.setEraserText("工具/橡皮");
                            }
                            //m_view.undo();
                        } else if (item == PopupMenu.MENUITEM.ITEM5) {

                            Intent intent = new Intent();
                            intent.putExtra("uuid", uuid);
                            intent.setClass(OffMainActivity.this, EvaluationActivity.class);
                            startActivity(intent);
                        } else if (item == PopupMenu.MENUITEM.ITEM6) {

                            if (m_view.getBitmap() != null) {

                                OnSaveClick(new OnSaveCallBack() {
                                    @Override
                                    public void saveSuccess() {
                                        new Handler().postDelayed(new Runnable(){
                                            public void run(){
                                                Intent intent = new Intent(
                                                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                                startActivityForResult(intent, IMAGE_RESULT_CODE);// 打开照相机

                                            }
                                        }, 1000);

                                    }

                                    @Override
                                    public void saveFail() {
                                        new Handler().postDelayed(new Runnable(){
                                            public void run(){
                                                Intent intent = new Intent(
                                                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                                startActivityForResult(intent, IMAGE_RESULT_CODE);// 打开照相机

                                            }
                                        }, 1000);

                                    }
                                },true);


                            } else {
                                Intent intent = new Intent(
                                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, IMAGE_RESULT_CODE);// 打开照相机
                            }


                        } else if (item == PopupMenu.MENUITEM.ITEM7) {

                            perPage();
                        } else if (item == PopupMenu.MENUITEM.ITEM8) {

                            nextPage();
                        } else if (item == PopupMenu.MENUITEM.ITEM9) {
                            Intent intent = new Intent(OffMainActivity.this, GridViewColorActivity.class);
                            intent.putExtra("curval",curVal);
                            intent.putExtra("state","offline"); //标记是在线模式还是离线模式
                            OffMainActivity.this.startActivity(intent);
                            config.setStrokeWidth(curVal);
//                            Random random = new Random();
//                            config.setStrokeColor(
//                                    Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
                        }


                    }
                });
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    2);
        }

    }

    private void OnSaveClick(final  OnSaveCallBack saveCallBack,final boolean showLoading) {


        if (showLoading) {
            mWeiboDialog = WeiboDialogUtils.createLoadingDialog(OffMainActivity.this, "保存中...");
        }
            new Handler().postDelayed(new Runnable(){  //子线程处理保存数据
            public void run(){
        //builder.setOutsideTouchable(false);
        try {

            //builder.setBackTouchable(true);

            Bitmap bmp = m_view.obtainBitmapByP();

            String filename = Constant.generateMixpicPath(UUIDUtils.getUuid(),"jpg");
            String picName = Constant.CamerapicPathJPK;
            UploadModel uploadModel = new UploadModel();
            if (null != bmp) {
                BitmapUtil.saveBitmapToSDCard(bmp, filename);
                uploadModel.setMixpic(filename);

            }

            if (null != m_view.getBitmap()) {
                BitmapUtil.saveBitmapToSDCard(m_view.getBitmap(), picName);
                uploadModel.setBigpic(picName);

            }
            String repath = Constant.generateCanvaspicPath(UUIDUtils.getUuid(), ".online");

            SerializeUtils.serialization(repath, m_view.paths);


            if (preferencesUtils == null) {
                preferencesUtils = SharedPreferencesUtils.createSharedPreferences("online", OffMainActivity.this);
            }


            uploadModel.setCanvaspic(repath);
            uploadModel.setStatus(1);
            if (curPage == 0) {
                curPage = 1;
            }
            uploadModel.setPage(curPage);

            UploadModel upload = uploadDAO.findUploadByPageAndUID(curPage, uuid);
            uploadModel.setId(upload.getId());
            uploadDAO.update(uploadModel);


            if (showLoading) {
                mHandler.sendEmptyMessageDelayed(1, 1000);  //发送消息通知主线程，只有主线程才能操作UI
            }
            if (saveCallBack != null) {
                saveCallBack.saveSuccess();
            }

        } catch (Exception e) {
            if (showLoading) {
                mHandler.sendEmptyMessageDelayed(1, 1000);  //发送消息通知主线程，只有主线程才能操作UI
            }
            if (saveCallBack != null) {
                saveCallBack.saveFail();
            }
            e.printStackTrace();
        }
         }
        }, 1000);

    }


    private void closeLoad() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //等待10000毫秒后销毁此页面，并提示登陆成功
                Intent intt = new Intent();
                intt.setAction("cn.yaheen.load");
                intt.putExtra("msg", "ok");
                OffMainActivity.this.sendBroadcast(intt); //发送广播更新界面
            }
        }, 1000);

    }


    private void nextPage() {

        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(OffMainActivity.this, "数据载入中...");
        OnSaveClick(new OnSaveCallBack() {
            @Override
            public void saveSuccess() {
                doNextPage();


            }

            @Override
            public void saveFail() {
                doNextPage();

            }
        },false);
        mHandler.sendEmptyMessageDelayed(2, 1000);



    }


    private void perPage() {


        mWeiboDialog = WeiboDialogUtils.createLoadingDialog(OffMainActivity.this, "数据载入中...");
        OnSaveClick(new OnSaveCallBack() {
            @Override
            public void saveSuccess() {
                doPerPage();

            }

            @Override
            public void saveFail() {
                doPerPage();
            }
        },false);

        mHandler.sendEmptyMessageDelayed(2, 1000);




    }

    private void doNextPage() {
        if (curPage == 0 || curPage == page) {
            curPage = page;
            Toast.makeText(OffMainActivity.this, "此页为最后一页", Toast.LENGTH_LONG).show();
            return;
        } else {
            m_view.clear();
            curPage = curPage + 1;
        }

        if (pics.get(curPage) != null) {
            Bitmap bitmap = ImgUtil.showBigByWidth(dm.widthPixels / 2, pics.get(curPage));

            m_view.setBitmap(bitmap);
        } else {
            UploadModel uploadModel = uploadDAO.findUploadByPageAndUID(curPage, uuid);
            if (uploadModel != null && uploadModel.getBigpic() != null && !"".equals(uploadModel.getBigpic())) {
                Bitmap camerapic = BitmapUtil.loadBitmapFromSDCard(uploadModel.getBigpic());
                Bitmap bitmap = ImgUtil.showBigByWidth(dm.widthPixels / 2, camerapic);

                m_view.setBitmap(bitmap);
            }
        }


        UploadModel uploadModel = uploadDAO.findUploadByPageAndUID(curPage, uuid);
        if (uploadModel != null && uploadModel.getCanvaspic() != null && !"".equals(uploadModel.getCanvaspic())) {

            ArrayList<SerializablePath> pvbaths = (ArrayList<SerializablePath>) SerializeUtils.deserialization(uploadModel.getCanvaspic());

            ArrayList<SerializablePath> paths = SerializeUtils.TransPath(pvbaths);
            m_view.paths.addAll(paths);
            /// drawableView.pathDrawer.obtainBitmap(one,paths);

        }

        m_view.initff();
        if (m_view.getBitmap() != null) {
            pics.put(curPage, m_view.getBitmap());

        }
        pagetv.setText("(" + curPage + "/" + page + ")");
    }


    private void doPerPage() {
        if (curPage == 0) {
            curPage = page;
            m_view.clear();
        }
        if (curPage == 1 || curPage == 0 || page == 1) {
            curPage = 1;
            Toast.makeText(OffMainActivity.this, "此页为第一页", Toast.LENGTH_LONG).show();
            return;
        } else {
            curPage = curPage - 1;
            m_view.clear();
        }

        List<UploadModel> list = uploadDAO.findByUID(uuid);
        if (pics.get(curPage) != null) {
            Bitmap bitmap = ImgUtil.showBigByWidth(dm.widthPixels / 2, pics.get(curPage));

            m_view.setBitmap(bitmap);
        } else {
            UploadModel uploadModel = uploadDAO.findUploadByPageAndUID(curPage, uuid);

            if (uploadModel != null && uploadModel.getBigpic() != null && !"".equals(uploadModel.getBigpic())) {
                Bitmap camerapic = BitmapUtil.loadBitmapFromSDCard(uploadModel.getBigpic());
                Bitmap bitmap = ImgUtil.showBigByWidth(dm.widthPixels / 2, camerapic);
                m_view.setBitmap(bitmap);
            }
        }

        UploadModel uploadModel = uploadDAO.findUploadByPageAndUID(curPage, uuid);

        if (uploadModel != null && uploadModel.getCanvaspic() != null && !"".equals(uploadModel.getCanvaspic())) {
            ArrayList<SerializablePath> pvbaths = (ArrayList<SerializablePath>) SerializeUtils.deserialization(uploadModel.getCanvaspic());

            ArrayList<SerializablePath> paths = SerializeUtils.TransPath(pvbaths);
            m_view.paths.addAll(paths);

        }
        m_view.initff();
        if (m_view.getBitmap() != null) {
            pics.put(curPage, m_view.getBitmap());
        }
        pagetv.setText("(" + curPage + "/" + page + ")");
    }


    private void OnNewClick(final Bitmap bitmap) {
        if (preferencesUtils == null) {
            preferencesUtils = SharedPreferencesUtils.createSharedPreferences("online", OffMainActivity.this);
        }
        if (curPage == 0) {
            curPage = 1;
        }

        page = page + 1;
        curPage = page;
        m_view.clear();
        UploadModel uploadModel = new UploadModel();
        String coursecode = DefaultPrefsUtil.getCourseCode();
        String teacherName = DefaultPrefsUtil.getTeacherName();
        uploadModel.setPage(page);
        uploadModel.setStatus(2);
        uploadModel.setCoursename(coursecode);
        uploadModel.setTeacher(teacherName);
        uploadModel.setUid(uuid);
        uploadDAO.save(uploadModel);
        pagetv.setText("(" + curPage + "/" + page + ")");
        if (null != bitmap) {  //如果不为空
            m_view.setBitmap(bitmap);
            m_view.initff();
        }


    }


//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        dm = getResources().getDisplayMetrics();
//
//        config.setCanvasHeight(canvasHight);
//        config.setCanvasWidth(dm.widthPixels);
//        m_view.setConfig(config);
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 表示 调用照相机拍照
            case IMAGE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");



                        float height=bitmap.getHeight()*(dm.widthPixels/2*1f/bitmap.getWidth());
                        Bitmap thum = ImgUtil.big(bitmap,dm.widthPixels/2,height);
                        OnNewClick(thum);






                    // imageView1.setImageBitmap(bitmap);
                }
                break;
            // 选择图片库的图片
            case PICK:
//                if (resultCode == RESULT_OK) {
//                    Uri uri = data.getVersion();
//                    imageView1.setImageURI(uri);
//                }
                break;

        }

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 1:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //创建文件夹
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File file = new File(Environment.getExternalStorageDirectory() + "/pic/");
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        String path = Environment.getExternalStorageDirectory() + "/mixpic/";
                        File canvasfile = new File(path);
                        if (!canvasfile.exists()) {
                            canvasfile.mkdirs();
                        }
                    }
                    // Bitmap bmp = m_view.getCanvasSnapshot();

                }
                break;


        }
    }









    private  int size=0;


    public void uplaod(final List<UploadModel> uploadModels, final int index){


        size=uploadModels.size();
        String pic=uploadModels.get(index).getMixpic();

        if (pic==null||"".equals(pic)){
            return;
        }
        RequestParams requestParams = new RequestParams(constant.getBaseurl()+"/eapi/note/submit.do"); // 默认编码UTF-8

        requestParams.addBodyParameter("note",new File(pic));
        requestParams.addBodyParameter("type","off");
        requestParams.addBodyParameter("courseCode",courseCode);
        requestParams.addBodyParameter("uuid",uuid);
        requestParams.addBodyParameter("coursewarePage",String.valueOf(uploadModels.get(index).getPage()));
        requestParams.addBodyParameter("hardwareId", SysUtils.android_id(OffMainActivity.this));


       // mDialog.setMax(100);
        XHttpUtils.uploadMethod( requestParams, new Callback.CacheCallback<ResponseEntity>() {
            @Override
            public boolean onCache(ResponseEntity result) {
                return false;
            }

            @Override
            public void onSuccess(ResponseEntity result) {

                Gson gson=new Gson();
                ResponseEntityResult resultEntry=gson.fromJson(result.getResult(),ResponseEntityResult.class);
                if (resultEntry.getResult()){
                    UploadModel uploadModel=uploadModels.get(i-1);
                    uploadModel.setStatus(0);
                    uploadDAO.update(uploadModel);
                }else {
                    Toast.makeText(OffMainActivity.this,"第"+i+"页上传失败", Toast.LENGTH_SHORT).show();
                }



                int count=100*1024*1024/size;
                if(i==size){
                    mDialog.setProgress(100);
                    mDialog.dismiss();
                }else {
                  mDialog.setProgress(i*count/1024/1024);

                }

                i++;
                if (i<=size){
                    uplaod(uploadModels,i-1);

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(OffMainActivity.this,"上传失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(OffMainActivity.this,"上传取消", Toast.LENGTH_SHORT).show();
                int count=100*1024*1024/size;
                if(i==size){
                    mDialog.setProgress(100);
                    mDialog.dismiss();
                }else {
                    mDialog.setProgress(i*count/1024/1024);

                }

                i++;
                if (i<=size){
                    uplaod(uploadModels,i);

                }
                Toast.makeText(OffMainActivity.this,"上传失败", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFinished() {

            }

        });



    }



    private void dialog(View v) {
        final CustomDialog dialog = new CustomDialog(OffMainActivity.this);
        final EditText editText = (EditText) dialog.getEditText();//方法在CustomDialog中实现
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dosomething youself
                i=1;
                courseCode=editText.getText().toString().trim();
                List<UploadModel> uploadModels=uploadDAO.findByStatusAndUID(1,uuid);
                if (uploadModels!=null&&uploadModels.size()>0){
                    dialog.dismiss();
                    mDialog = new CommonProgressDialog(OffMainActivity.this,null);
                    mDialog.setMessage("正在上传");
                    mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mDialog.show();

                    uplaod(uploadModels,0);
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



    private void OnUploadClick(View v){
        i=1;
        List<UploadModel> uploadModels=uploadDAO.findByStatusAndUID(1,uuid);
        if (uploadModels!=null&&uploadModels.size()>0){
            mDialog = new CommonProgressDialog(OffMainActivity.this,null);
            mDialog.setMessage("正在上传");
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });

            uplaod(uploadModels,0);
            mDialog.show();
        }else{
            Toast.makeText(OffMainActivity.this,"还没保存任何的课程", Toast.LENGTH_SHORT).show();
        }
    }


    private void showNormalDialog(String msg,final DialogCallback callback){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(OffMainActivity.this);
        normalDialog.setTitle("提醒");
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        callback.callback();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        DialogUtils.showDialog(OffMainActivity.this, "是否退出当前页面？", new DialogCallback() {
            @Override
            public void callback() {
                OnSaveClick(new OnSaveCallBack() {
                    @Override
                    public void saveSuccess() {
                        Intent intent=new Intent();
                        intent.setClass(OffMainActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void saveFail() {
                        ToastUtils.showMessage(OffMainActivity.this,"保存失败");
                        Intent intent=new Intent();
                        intent.setClass(OffMainActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },true);

            }
        }, new IDialogCancelCallback() {
            @Override
            public void cancelCallback() {

            }
        });
    }

    @Override
    public void getMsg(String str) {
        Gson gson=new Gson();
        MsgBean msgBean=gson.fromJson(str,MsgBean.class);
        if (msgBean.getColor()!=0){
            curColor=msgBean.getColor();
            config.setStrokeColor(msgBean.getColor());

        }else {
            config.setStrokeColor(curColor);
        }

        if (msgBean.getSeekBarVal()!=0){
            curVal=msgBean.getSeekBarVal();
            config.setStrokeWidth(msgBean.getSeekBarVal());
        }else {
            config.setStrokeWidth(curVal);
        }

    }

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 6;
            mHandler.sendMessage(message);
        }
    };

}
