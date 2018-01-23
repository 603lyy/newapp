package cn.yaheen.online.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.yaheen.online.R;
import cn.yaheen.online.activity.MainActivity;
import cn.yaheen.online.switchbutton.SwitchButton;
import cn.yaheen.online.utils.sharepreferences.DefaultPrefsUtil;
import cn.yaheen.online.utils.sharepreferences.SharedPreferencesUtils;

/**
 * Created by linjingsheng on 17/3/18.
 */

public class SettingDialog {

    public static void showAddDialog(final Context context) {

        LayoutInflater factory = LayoutInflater.from(context);
        View textEntryView = factory.inflate(R.layout.setting, null);
        SwitchButton screen = (SwitchButton) textEntryView.findViewById(R.id.heng);
        final EditText editTextName = (EditText) textEntryView.findViewById(R.id.editTextName);
        final EditText editTextName2 = (EditText) textEntryView.findViewById(R.id.editTextName2);
        final EditText etPsd = (EditText) textEntryView.findViewById(R.id.et_psd);
        final EditText etNickName = (EditText) textEntryView.findViewById(R.id.et_nick_name);
        final TextView currentstate = (TextView) textEntryView.findViewById(R.id.currentstate);
        TextView tvCancel = (TextView) textEntryView.findViewById(R.id.tv_dialog_cancel);
        TextView tvSure = (TextView) textEntryView.findViewById(R.id.tv_dialog_sure);

        String ip = DefaultPrefsUtil.getIpUrl();
        String teacherName = DefaultPrefsUtil.getUserName();
        String teacherPsd = DefaultPrefsUtil.getUserPassword();
        String teacherNickName = DefaultPrefsUtil.getTeacherName();
        Boolean screennow = DefaultPrefsUtil.getIsHorizontalScreen();

        if (ip != null && !"".equals(ip.trim())) {
            editTextName.setText(ip);
        }

        if (teacherName != null && !"".equals(teacherName.trim())) {
            editTextName2.setText(teacherName);
        }

        if (!TextUtils.isEmpty(teacherPsd)) {
            etPsd.setText(teacherPsd);
        }

        if (!TextUtils.isEmpty(teacherNickName)) {
            etNickName.setText(teacherNickName);
        }

        screen.setChecked(screennow);
        if (screennow) {
            currentstate.setText("当前：横屏");
        } else {
            currentstate.setText("当前：竖屏");
        }

        AlertDialog.Builder ad1 = new AlertDialog.Builder(context);
        ad1.setView(textEntryView);

        screen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DefaultPrefsUtil.setIsHorizontalScreen(true);
                    currentstate.setText("当前：横屏");
                } else {
                    DefaultPrefsUtil.setIsHorizontalScreen(false);
                    currentstate.setText("当前：竖屏");
                }
            }
        });

        // 显示对话框
        final AlertDialog dialog = ad1.show();

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null) {
                    String IP = editTextName.getText().toString();
                    String teacherPsd = etPsd.getText().toString();
                    String teacherName = editTextName2.getText().toString();
                    String teacherNickName = etNickName.getText().toString();
                    if (IP != null && !"".equals(IP.trim()) && teacherName != null && !teacherName.trim().equals("")) {
                        DefaultPrefsUtil.setIpUrl(IP.trim());
                        DefaultPrefsUtil.setUserName(teacherName);
                        DefaultPrefsUtil.setUserPassword(teacherPsd);
                        DefaultPrefsUtil.setTeacherName(teacherNickName);
                        Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();
                        ((MainActivity) context).getmHandler().sendEmptyMessageDelayed(10, 200);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

    }
}
