package cn.yaheen.online.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.daimajia.swipe.util.Attributes;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.List;

import cn.yaheen.online.R;
import cn.yaheen.online.activity.online.OnlineMainActivity;
import cn.yaheen.online.dao.UploadDAO;
import cn.yaheen.online.model.UploadModel;
import cn.yaheen.online.receiver.Receiver;
import cn.yaheen.online.utils.GridViewAdapter;
import cn.yaheen.online.utils.sharepreferences.DefaultPrefsUtil;
import cn.yaheen.online.utils.sharepreferences.SharedPreferencesUtils;
import cn.yaheen.online.utils.ToastUtils;
import cn.yaheen.online.view.LazyGridView;

public class PingJiaoActivity extends Activity implements Receiver.Message {

    private GridViewAdapter adapter;

    UploadDAO uploadDAO = null;
    Receiver myReceiver;

    private List<UploadModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cn.yaheen.online.app.OnlineApp.getInstance().addActivity(this);
        if (uploadDAO == null) {
            uploadDAO = new UploadDAO();
        }
        List<DbModel> dblist = uploadDAO.findAllByGruopUUID();
        if (dblist != null && dblist.size() != 0) {
            setContentView(R.layout.activity_ping_jiao);
            hasDataView();
        } else {
            setContentView(R.layout.pj_nodata_show);
        }
    }

    private void hasDataView() {
        LazyGridView gridView = (LazyGridView) findViewById(R.id.gridview);
        List<DbModel> dblist = uploadDAO.findAllByGruopUUID();
        adapter = new GridViewAdapter(this);
        adapter.setMode(Attributes.Mode.Multiple);

        if (dblist != null && dblist.size() > 0) {
            for (int i = 0; i < dblist.size(); i++) {
                DbModel dbModel = dblist.get(i);
                String uid = dbModel.getString("fx_uid");
                List<UploadModel> uploadModel = uploadDAO.findByUID(uid);
                UploadModel upload = new UploadModel();
                upload.setMixpic(uploadModel.get(0).getMixpic());
                upload.setTeacher(uploadModel.get(0).getTeacher());
                upload.setUid(uid);
                upload.setCoursename(uploadModel.get(0).getCoursename());
                list.add(upload);
            }
        }
        adapter.setList(list);
        gridView.setSelected(false);
        gridView.setAdapter(adapter);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UploadModel uploadModel = (UploadModel) parent.getAdapter().getItem(position);
                gotoCourse(uploadModel);
            }
        });

        gridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        myReceiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("cn.yaheen.online");
        registerReceiver(myReceiver, intentFilter);

        //因为这里需要注入Message，所以不能在AndroidManifest文件中静态注册广播接收器
        myReceiver.setMessage(this);
    }

    /**
     * Create by xszyou on 20170621:像离线课那样查看评教
     * @param uploadModel
     */
    private void gotoCourse(UploadModel uploadModel) {
        String uuid = uploadModel.getUid();

        DefaultPrefsUtil.setCourseUid(uploadModel.getCoursename(), uuid);
        DefaultPrefsUtil.setCourseCode(uploadModel.getCoursename());
        DefaultPrefsUtil.setTeacherName(uploadModel.getTeacher());
        DefaultPrefsUtil.setLoginTime("");
        DefaultPrefsUtil.setToken("");
        DefaultPrefsUtil.setUUID(uuid);

        Intent intent = new Intent();
        intent.setClass(PingJiaoActivity.this, OnlineMainActivity.class);
        intent.putExtra("isPingJiaoOpen", true);
        startActivity(intent);
    }

    @Override
    public void getMsg(String str) {
        if ("ok".equals(str)) {
            finish();
            Intent intent = new Intent();
            intent.setClass(PingJiaoActivity.this, PingJiaoActivity.class);
            this.startActivity(intent);
        } else {
            ToastUtils.showMessage(PingJiaoActivity.this, "删除失败");
        }
    }
}
