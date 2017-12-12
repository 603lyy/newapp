package cn.yaheen.online.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.List;

import cn.yaheen.online.R;
import cn.yaheen.online.activity.offline.OffMainActivity;
import cn.yaheen.online.activity.online.OnlineMainActivity;
import cn.yaheen.online.app.*;
import cn.yaheen.online.app.OnlineApp;
import cn.yaheen.online.bean.LoginBean;
import cn.yaheen.online.dao.UploadDAO;
import cn.yaheen.online.model.UploadModel;
import cn.yaheen.online.switchbutton.SwitchButton;
import cn.yaheen.online.utils.Constant;
import cn.yaheen.online.utils.DialogCallback;
import cn.yaheen.online.utils.DialogUtils;
import cn.yaheen.online.utils.IDialogCancelCallback;
import cn.yaheen.online.utils.SettingDialog;
import cn.yaheen.online.utils.sharepreferences.DefaultPrefsUtil;
import cn.yaheen.online.utils.sharepreferences.SharedPreferencesUtils;
import cn.yaheen.online.utils.SysUtils;
import cn.yaheen.online.utils.ToastUtils;
import cn.yaheen.online.utils.UUIDUtils;
import cn.yaheen.online.utils.WeiboDialogUtils;
import cn.yaheen.online.utils.version.VersionUtils;


public class MainActivity extends Activity {

    private SwitchButton sbDefault;
    private Button pingJiao;
    private Button login;
    private TextView code;
    private EditText courseNameEdit, teacher, usernameEdit, passwordEdit;
    private Dialog mWeiboDialog = null;

    private TextView textView;
    private Button setingBtn;
    private boolean isLogin = true;
    UploadDAO uploadDAO = null;

    public Handler getmHandler() {
        return mHandler;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 10:
                    teacher.setText(DefaultPrefsUtil.getTeacherName());
                    ToastUtils.showMessage(MainActivity.this, "保存成功");
                    break;
                case 3:
                    if (mWeiboDialog != null) {
                        WeiboDialogUtils.closeDialog(mWeiboDialog);
                    }
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cn.yaheen.online.app.OnlineApp.getInstance().addActivity(this);
        setContentView(R.layout.activity_main);

        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_main);
        usernameEdit = (EditText) findViewById(R.id.usernameText);
        passwordEdit = (EditText) findViewById(R.id.passwordText);
        courseNameEdit = (EditText) findViewById(R.id.editText2);
        sbDefault = (SwitchButton) findViewById(R.id.sb_default);
        textView = (TextView) findViewById(R.id.textView);
        teacher = (EditText) findViewById(R.id.editText);
        setingBtn = (Button) findViewById(R.id.setting);
        pingJiao = (Button) findViewById(R.id.button2);
        login = (Button) findViewById(R.id.button);
        code = (TextView) findViewById(R.id.code);

        checkVersion();
        initPermission();
        if (uploadDAO == null) {
            uploadDAO = new UploadDAO();
        }
        sbDefault.setChecked(true);
        layout.getBackground().setAlpha(200);
        teacher.setText(DefaultPrefsUtil.getTeacherName());
        usernameEdit.setText(DefaultPrefsUtil.getUserName());
        passwordEdit.setText(DefaultPrefsUtil.getUserPassword());

        sbDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /**
                 * Edit by xszyou on 20170620:离线模式功能对齐在线模式
                 */
                if (isChecked) {
                    isLogin = true;
                    login.setText("登录");
                    code.setText("课程编码");
                    textView.setText("在线模式");
                    findViewById(R.id.teacherLayout).setVisibility(View.GONE);
                    findViewById(R.id.passwordLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.usernameLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.courseCodeLayout).setVisibility(View.VISIBLE);
                } else {
                    isLogin = false;
                    code.setText("课程名称");
                    login.setText("开始上课");
                    textView.setText("离线模式");
                    findViewById(R.id.usernameLayout).setVisibility(View.GONE);
                    findViewById(R.id.passwordLayout).setVisibility(View.GONE);
                    findViewById(R.id.teacherLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.courseCodeLayout).setVisibility(View.VISIBLE);
                }
            }
        });

        setingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingDialog.showAddDialog(MainActivity.this);
            }
        });

        //Edit by xszyou on 20170620:查看评教按钮
        pingJiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, PingJiaoActivity.class);
                startActivity(intent);// 启动Activity
            }
        });

        //Edit by xszyou on 20170620:在线模式，登录按钮
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        CrashReport.initCrashReport(this.getApplicationContext(), "e539b4d77d", false);
    }

    private void checkVersion() {
        VersionUtils.checkVersion(this);
    }

    private void gotoschool() {
        String courseName = courseNameEdit.getText().toString().trim();
        String teacherName = teacher.getText().toString().trim();
        if (teacherName == null || "".equals(teacherName.trim())) {
            Toast.makeText(MainActivity.this, "评教老师不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        if (courseName == null || "".equals(courseName.trim())) {
            Toast.makeText(MainActivity.this, "课程不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        SharedPreferences sp = getSharedPreferences("online", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String uuid = UUIDUtils.getUuid();
        editor.putInt("page", 1);
        editor.putString("uuid", uuid);
        editor.putString("courseName", courseName);
        editor.putString("teacherName", teacherName);
        editor.putBoolean("newpage", false);

        editor.commit();


        UploadModel upload = new UploadModel();
        upload.setPage(1);
        upload.setStatus(UploadModel.STATUS_NOT_SAVE);  //0已保存但未提交，1已提交 ，2未保存未提交
        upload.setCoursename(courseName);
        upload.setTeacher(teacherName);
        upload.setUid(uuid);
        uploadDAO.save(upload);

        try {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, OffMainActivity.class);
            startActivity(intent);// 启动Activity
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPermission() {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion > 22) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android
                    .Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, 1);
        }
        createDir();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //创建文件夹
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File canvas = new File(Environment.getExternalStorageDirectory() + "/canvas/");
                        File pic = new File(Environment.getExternalStorageDirectory() + "/pic/");
                        File mixpic = new File(Environment.getExternalStorageDirectory() + "/mixpic/");
                        File screenshort = new File(Environment.getExternalStorageDirectory() + "/screenshort/");
                        if (!canvas.exists()) {
                            canvas.mkdirs();
                        }
                        if (!pic.exists()) {
                            pic.mkdirs();
                        }
                        if (!mixpic.exists()) {
                            mixpic.mkdirs();
                        }
                        if (!screenshort.exists()) {
                            screenshort.mkdirs();
                        }
                    }
                }
                break;
            default:
        }
    }

    private void createDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File canvas = new File(Environment.getExternalStorageDirectory() + "/canvas/");
            File mixpic = new File(Environment.getExternalStorageDirectory() + "/mixpic/");
            File bigpic = new File(Environment.getExternalStorageDirectory() + "/bigpic/");
            if (!canvas.exists()) {
                canvas.mkdirs();
            }
            if (!mixpic.exists()) {
                mixpic.mkdirs();
            }
            if (!bigpic.exists()) {
                bigpic.mkdirs();
            }
        }
    }

    /**
     * 创建课程
     *
     * @param loginBean
     */
    private void createCourse(LoginBean loginBean) {
        String courseName = courseNameEdit.getText().toString().trim();
        String username = usernameEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String teacherName = teacher.getText().toString().trim();
        UploadModel uploadModel = new UploadModel();
        UploadDAO uploadDAO = new UploadDAO();
        String uuid = UUIDUtils.getUuid();

        DefaultPrefsUtil.setCourseCode(isLogin ? courseName : null);
        DefaultPrefsUtil.setLoginTime(loginBean.getLoginTime());
        DefaultPrefsUtil.setCourseUid(courseName, uuid);
        DefaultPrefsUtil.setToken(loginBean.getToken());
        DefaultPrefsUtil.setTeacherName(teacherName);
        DefaultPrefsUtil.setUserPassword(password);
        DefaultPrefsUtil.setUserName(username);
        DefaultPrefsUtil.setUUID(uuid);

        uploadModel.setPage(1);
        uploadModel.setUid(uuid);
        uploadModel.setTeacher(teacherName);
        uploadModel.setCoursename(courseName);
        uploadModel.setStatus(UploadModel.STATUS_NOT_SAVE);
        //
        uploadDAO.save(uploadModel);

        if (mWeiboDialog != null) {
            WeiboDialogUtils.closeDialog(mWeiboDialog);
        }
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, OnlineMainActivity.class);
        startActivity(intent);
    }


    /**
     * 在线模式上课
     *
     * @param uuid
     * @param loginBean
     */
    private void goONlineActivity(String uuid, LoginBean loginBean) {
        String courseName = courseNameEdit.getText().toString().trim();
        String username = usernameEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString().trim();
        String teacherName = teacher.getText().toString().trim();

        DefaultPrefsUtil.setCourseCode(isLogin ? courseName : null);
        DefaultPrefsUtil.setLoginTime(loginBean.getLoginTime());
        DefaultPrefsUtil.setCourseUid(courseName, uuid);
        DefaultPrefsUtil.setToken(loginBean.getToken());
        DefaultPrefsUtil.setTeacherName(teacherName);
        DefaultPrefsUtil.setUserPassword(password);
        DefaultPrefsUtil.setUserName(username);
        DefaultPrefsUtil.setUUID(uuid);

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, OnlineMainActivity.class);
        startActivity(intent);
    }

    /**
     * Edit by xszyou on 20170620:在线登录和离线上课
     */
    private void login() {
        final String courseName = courseNameEdit.getText().toString().trim();
        final String password = passwordEdit.getText().toString().trim();
        final String username = usernameEdit.getText().toString().trim();
        final String teacherName = teacher.getText().toString().trim();

        //在线模式
        if (isLogin) {
            /**
             * Edit by xszyou on 20170704:在线模式使用账号登录
             */
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(MainActivity.this, "评教老师账号不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "密码不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            mWeiboDialog = WeiboDialogUtils.createLoadingDialog(MainActivity.this, "登录中...");

            RequestParams params = new RequestParams(Constant.getBaseurl() + "/eapi/student/login.do");
            params.addBodyParameter("hardwareId", SysUtils.android_id(MainActivity.this));
            params.addBodyParameter("courseCode", courseName);
            params.addParameter("teacherName", username);
            params.addParameter("password", password);

            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Gson gson = new Gson();
                    if (!TextUtils.isEmpty(result)) {
                        LoginBean loginBean = gson.fromJson(result, LoginBean.class);
                        if (loginBean.getResult()) {
                            String courseUdi = DefaultPrefsUtil.getCourseUid(courseName);
                            //如果不存在课程编号，则创建新的课程
                            if (TextUtils.isEmpty(courseUdi.trim())) {
                                createCourse(loginBean);
                            } else {
                                //如果存在课程编号
                                List<UploadModel> uploadModel = uploadDAO.findByUID(courseUdi);
                                if (uploadModel != null && uploadModel.size() > 0) {
                                    goONlineActivity(courseUdi, loginBean);
                                } else {
                                    createCourse(loginBean);
                                }
                            }
                        } else {
                            mHandler.sendEmptyMessageDelayed(3, 1000);
                            Toast.makeText(MainActivity.this, "登录失败:" + loginBean.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mHandler.sendEmptyMessageDelayed(3, 1000);
                        Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    mHandler.sendEmptyMessageDelayed(3, 1000);
                    Toast.makeText(MainActivity.this, "登录失败，请检查网络是否通畅", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                    mHandler.sendEmptyMessageDelayed(3, 1000);
                }

                @Override
                public void onFinished() {
                }
            });

//            强制登录
//            String courseUdi = DefaultPrefsUtil.getCourseUid(courseName);
//            LoginBean lb = new LoginBean();
//            lb.setToken("xxxxx");
//            goONlineActivity(courseUdi, lb);
//
        } else {
            /**
             * Edit by xszyou on 20170620：离线上课每次都是开新课堂，如果要继续原课常需要到查看评教处
             */
            if (TextUtils.isEmpty(teacherName)) {
                Toast.makeText(MainActivity.this, "评教老师不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(courseName)) {
                Toast.makeText(MainActivity.this, "课程不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            mWeiboDialog = WeiboDialogUtils.createLoadingDialog(MainActivity.this, "载入中...");
            LoginBean loginBean = new LoginBean();
            createCourse(loginBean);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWeiboDialog != null) {
            WeiboDialogUtils.closeDialog(mWeiboDialog);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OnlineApp.getRefWatcher().watch(this);
    }

    @Override
    public void onBackPressed() {
        DialogUtils.showDialog(MainActivity.this, "确定要退出该APP吗？", new DialogCallback() {
            @Override
            public void callback() {
                cn.yaheen.online.app.OnlineApp.getInstance().exit();
            }
        }, new IDialogCancelCallback() {
            @Override
            public void cancelCallback() {
            }
        });
    }

}
