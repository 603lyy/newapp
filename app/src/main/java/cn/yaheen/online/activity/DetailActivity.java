package cn.yaheen.online.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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

import cn.yaheen.online.Contral.BitmapUtil;
import cn.yaheen.online.R;
import cn.yaheen.online.activity.offline.EvaluationActivity;
import cn.yaheen.online.bean.ResponseEntity;
import cn.yaheen.online.bean.ResponseEntityResult;
import cn.yaheen.online.dao.UploadDAO;
import cn.yaheen.online.model.UploadModel;
import cn.yaheen.online.utils.Constant;
import cn.yaheen.online.utils.ImgUtil;
import cn.yaheen.online.utils.sharepreferences.DefaultPrefsUtil;
import cn.yaheen.online.utils.sharepreferences.SharedPreferencesUtils;
import cn.yaheen.online.utils.SysUtils;
import cn.yaheen.online.utils.UUIDUtils;
import cn.yaheen.online.utils.XHttpUtils;
import cn.yaheen.online.utils.kyloading.ProgersssDialog;
import cn.yaheen.online.view.CommonProgressDialog;
import cn.yaheen.online.view.CustomDialog;
import cn.yaheen.online.view.PopupMenu;
import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;
import me.panavtec.drawableview.draw.SerializablePath;
import me.panavtec.drawableview.utils.SerializeUtils;


public class DetailActivity extends Activity {


    private SeekBar seekBar;
    private DrawableView m_view;
    private UploadDAO uploadDAO = null;
    private final int IMAGE_RESULT_CODE = 2;// 表示打开照相机
    private final int PICK = 1;// 选择图片库
    private int curPage = 0; //当前页，用于翻页


    private DrawableViewConfig config = new DrawableViewConfig();
    private DisplayMetrics dm;
    private int canvasHight = 0;
    private PopupMenu popupMenu;  //右上角弹出框
    private int page = 1;
    CommonProgressDialog mDialog;
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
    List<UploadModel> uploadModelList;
    Boolean screen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        cn.yaheen.online.app.OnlineApp.getInstance().addActivity(this);

        curPage = 1;

        uuid = this.getIntent().getStringExtra("uuid");
        try {
            if (uuid != null && !"".equals(uuid)) {
                if (uploadDAO == null) {
                    uploadDAO = new UploadDAO();
                }
                uploadModelList = uploadDAO.findByUID(uuid);
                preferencesUtils = SharedPreferencesUtils.createSharedPreferences("online", DetailActivity.this);
                screen = DefaultPrefsUtil.getIsHorizontalScreen();

                if (screen) {

                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {

                    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    //
                }
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


                m_view = (DrawableView) this.findViewById(R.id.paintView);
                config.setStrokeColor(getResources().getColor(android.R.color.holo_red_light));
                config.setShowCanvasBounds(true);
                config.setStrokeWidth(5.0f);
                config.setMinZoom(1.0f);
                config.setMaxZoom(3.0f);
                config.setCanvasHeight(3200);
                config.setCanvasWidth(3200);
                config.setStrokeColor(Color.WHITE);
                m_view.setConfig(config);
                m_view.setOneFingerMode(true);
                LayoutInflater inflater = (LayoutInflater) this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                initPic();
                constant = Constant.createConstant(DetailActivity.this);


                if (preferencesUtils == null) {
                    preferencesUtils = SharedPreferencesUtils.createSharedPreferences("online", DetailActivity.this);
                }
                uuid = DefaultPrefsUtil.getUUID();


                pagetv = (TextView) findViewById(R.id.pagetv);
                if (uploadModelList != null && uploadModelList.size() > 0) {
                    pagetv.setText("(" + 1 + "/" + uploadModelList.size() + ")");

                } else {
                    pagetv.setText("(0/0)");

                }
                initbackhome();
                initDoPage();
                if (uploadModelList != null && uploadModelList.size() > 0) {
                    page = uploadModelList.size();
                }
                loadFirstPage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void loadFirstPage() {

        UploadModel uploadModel = uploadDAO.findUploadByPageAndUID(curPage, uuid);
        if (uploadModel != null && uploadModel.getBigpic() != null && !"".equals(uploadModel.getBigpic())) {
            Bitmap camerapic = BitmapUtil.loadBitmapFromSDCard(uploadModel.getBigpic());
            Bitmap bitmap = ImgUtil.showBigByWidth(dm.widthPixels - 200, camerapic);

            if (screen) {
                bitmap = ImgUtil.showBigByWidth(dm.widthPixels / 2, camerapic);
            } else {
                bitmap = ImgUtil.showBigByWidth(dm.widthPixels - 200, camerapic);

            }

            m_view.setBitmap(bitmap);

        }


        if (uploadModel != null && uploadModel.getCanvaspic() != null && !"".equals(uploadModel.getCanvaspic())) {

            ArrayList<SerializablePath> pvbaths = (ArrayList<SerializablePath>) SerializeUtils.deserialization(uploadModel.getCanvaspic());

            ArrayList<SerializablePath> paths = SerializeUtils.TransPath(pvbaths);
            m_view.paths.addAll(paths);
            /// drawableView.pathDrawer.obtainBitmap(one,paths);

        }


        m_view.initff();
    }


    /**
     * 初始化返回按钮
     */
    void initbackhome() {
        backhome = (ImageView) findViewById(R.id.backhome);
        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(DetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    /**
     * 加载浮动截图框
     */
    private void initPic() {


        dm = getResources().getDisplayMetrics();


    }


    void initDoPage() {

        LinearLayout nextpagebtn = (LinearLayout) findViewById(R.id.nextpage);
        LinearLayout perbtn = (LinearLayout) findViewById(R.id.prepage);
        LinearLayout upload = (LinearLayout) findViewById(R.id.upload);
        LinearLayout pingfen = (LinearLayout) findViewById(R.id.pingjiao);

        nextpagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nextPage();
            }
        });

        perbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perPage();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog(view);
            }
        });
        pingfen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("uuid", uuid);
                intent.setClass(DetailActivity.this, EvaluationActivity.class);
                startActivity(intent);
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

    private void OnSaveClick() {


        final ProgersssDialog dialog = new ProgersssDialog(DetailActivity.this);

        try {

            Bitmap bmp = m_view.obtainBitmapByP();

            String filename = Constant.MixpicPathPic;
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
                preferencesUtils = SharedPreferencesUtils.createSharedPreferences("online", DetailActivity.this);
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
            Toast.makeText(DetailActivity.this, "保存成功", Toast.LENGTH_SHORT).show();

            pagetv.setText("(" + curPage + "/" + page + ")");
            dialog.dismiss();

        } catch (Exception e) {
            dialog.dismiss();
            Toast.makeText(DetailActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }


    private void nextPage() {


        if (curPage == 0 || curPage == page) {
            curPage = page;
            Toast.makeText(DetailActivity.this, "此页为最后一页", Toast.LENGTH_LONG).show();
            return;
        } else {
            m_view.clear();
            curPage = curPage + 1;
        }

        Bitmap bitmap = null;
        if (pics.get(curPage) != null) {

            if (screen) {
                bitmap = ImgUtil.showBigByWidth(dm.widthPixels / 2, pics.get(curPage));
            } else {
                bitmap = ImgUtil.showBigByWidth(dm.widthPixels - 200, pics.get(curPage));

            }
            m_view.setBitmap(bitmap);
        } else {
            UploadModel uploadModel = uploadDAO.findUploadByPageAndUID(curPage, uuid);
            if (uploadModel != null && uploadModel.getBigpic() != null && !"".equals(uploadModel.getBigpic())) {
                Bitmap camerapic = BitmapUtil.loadBitmapFromSDCard(uploadModel.getBigpic());

                if (screen) {
                    bitmap = ImgUtil.showBigByWidth(dm.widthPixels / 2, camerapic);
                } else {
                    bitmap = ImgUtil.showBigByWidth(dm.widthPixels - 200, camerapic);

                }
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


    private void perPage() {

        if (curPage == 0) {
            curPage = page;
            m_view.clear();
        }
        if (curPage == 1 || curPage == 0 || page == 1) {
            curPage = 1;
            Toast.makeText(DetailActivity.this, "此页为第一页", Toast.LENGTH_LONG).show();
            return;
        } else {
            curPage = curPage - 1;
            m_view.clear();
        }

        List<UploadModel> list = uploadDAO.findByUID(uuid);
        Bitmap bitmap = null;

        if (pics.get(curPage) != null) {

            if (screen) {
                bitmap = ImgUtil.showBigByWidth(dm.widthPixels / 2, pics.get(curPage));
            } else {
                bitmap = ImgUtil.showBigByWidth(dm.widthPixels - 200, pics.get(curPage));

            }
            m_view.setBitmap(bitmap);
        } else {
            UploadModel uploadModel = uploadDAO.findUploadByPageAndUID(curPage, uuid);

            if (uploadModel != null && uploadModel.getBigpic() != null && !"".equals(uploadModel.getBigpic())) {
                Bitmap camerapic = BitmapUtil.loadBitmapFromSDCard(uploadModel.getBigpic());

                if (screen) {
                    bitmap = ImgUtil.showBigByWidth(dm.widthPixels / 2, camerapic);
                } else {
                    bitmap = ImgUtil.showBigByWidth(dm.widthPixels - 200, camerapic);

                }
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


    private void OnNewClick() {

        if (preferencesUtils == null) {
            preferencesUtils = SharedPreferencesUtils.createSharedPreferences("online", DetailActivity.this);
        }
        if (curPage == 0) {
            curPage = 1;
        }
        UploadModel uploadModelf = uploadDAO.findUploadByPageAndUID(curPage, uuid);
        if (uploadModelf != null && uploadModelf.getStatus() == 2) {
            Toast.makeText(DetailActivity.this, "还有新建课程未保存,请保存后再创建", Toast.LENGTH_SHORT).show();
        } else {
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
        }


    }


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

                    if (null != bitmap) {
                        m_view.setBitmap(bitmap);
                        m_view.initff();
                    }


                    // imageView1.setImageBitmap(bitmap);
                }
                break;
            // 选择图片库的图片
            case PICK:
//                if (resultCode == RESULT_OK) {
//                    Uri uri = data.getData();
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


    private int size = 0;
    private int i = 1;

    public void uplaod(final List<UploadModel> uploadModels, final int index) {


        size = uploadModels.size();
        String pic = uploadModels.get(index).getMixpic();

        if (pic == null || "".equals(pic)) {
            return;
        }
        RequestParams requestParams = new RequestParams(constant.getBaseurl() + "/eapi/note/submit.do"); // 默认编码UTF-8

        requestParams.addBodyParameter("note", new File(pic));
        requestParams.addBodyParameter("type", "off");
        requestParams.addBodyParameter("courseCode", courseCode);
        requestParams.addBodyParameter("uuid", uuid);
        requestParams.addBodyParameter("hardwareId", SysUtils.android_id(DetailActivity.this));
        requestParams.addBodyParameter("hardwareId", SysUtils.android_id(DetailActivity.this));


        // mDialog.setMax(100);
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
                    uploadModel.setStatus(0);
                    uploadDAO.update(uploadModel);
                } else {
                    Toast.makeText(DetailActivity.this, "第" + i + "页上传失败", Toast.LENGTH_SHORT).show();
                }


                int count = 100 * 1024 * 1024 / size;
                if (i == size) {
                    mDialog.setProgress(100);
                    mDialog.dismiss();
                } else {
                    mDialog.setProgress(i * count / 1024 / 1024);

                }

                i++;
                if (i <= size) {
                    uplaod(uploadModels, i - 1);

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(DetailActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Toast.makeText(DetailActivity.this, "上传取消", Toast.LENGTH_SHORT).show();
                int count = 100 * 1024 * 1024 / size;
                if (i == size) {
                    mDialog.setProgress(100);
                    mDialog.dismiss();
                } else {
                    mDialog.setProgress(i * count / 1024 / 1024);

                }

                i++;
                if (i <= size) {
                    uplaod(uploadModels, i);

                }
                Toast.makeText(DetailActivity.this, "上传失败", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFinished() {

            }

        });


    }


    private void dialog(View v) {
        final CustomDialog dialog = new CustomDialog(DetailActivity.this);
        final EditText editText = (EditText) dialog.getEditText();//方法在CustomDialog中实现
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dosomething youself
                i = 1;
                courseCode = editText.getText().toString().trim();
                List<UploadModel> uploadModels = uploadDAO.findByStatusAndUID(1, uuid);
                if (uploadModels != null && uploadModels.size() > 0) {
                    dialog.dismiss();
                    mDialog = new CommonProgressDialog(DetailActivity.this, null);
                    mDialog.setMessage("正在上传");
                    mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mDialog.show();

                    uplaod(uploadModels, 0);
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


    private void OnUploadClick() {

        List<UploadModel> uploadModels = uploadDAO.findByStatusAndUID(1, uuid);
        if (uploadModels != null && uploadModels.size() > 0) {
            mDialog = new CommonProgressDialog(DetailActivity.this, null);
            mDialog.setMessage("正在上传");
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {

                }
            });

            uplaod(uploadModels, 0);
            mDialog.show();
        } else {
            Toast.makeText(DetailActivity.this, "还没保存任何的课程", Toast.LENGTH_SHORT).show();
        }
    }

}
