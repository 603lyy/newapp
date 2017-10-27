package cn.yaheen.online.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import cn.yaheen.online.activity.online.OnlineMainActivity;
import cn.yaheen.online.interfaces.PageCallBack;

/**
 * Created by linjingsheng on 17/5/1.
 */

public class DialogUtils {


    private  static  AlertDialog dialog;
    public static void showNormalDialog(Context context, String msg, final DialogCallback callback,final  IDialogCancelCallback cancelCallback,  String PositiveButton,String NegativeButton ){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setTitle("提醒");
        normalDialog.setMessage(msg);
        normalDialog.setPositiveButton(PositiveButton,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        callback.callback();
                    }
                });
        normalDialog.setNegativeButton(NegativeButton,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        cancelCallback.cancelCallback();
                    }
                });
        // 显示
        normalDialog.show();
    }


    public static void showDialog(Context context, String msg, final DialogCallback callback,final  IDialogCancelCallback cancelCallback ){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
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
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        cancelCallback.cancelCallback();
                    }
                });
        // 显示
        dialog=normalDialog.show();

    }



    public  static void  closeShowDialog(){
        if (dialog!=null){
            dialog.dismiss();
        }
    }


    public  static void LstiDialog(final String[] items, final Context context ,final PageCallBack callBack){


        AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
        AlertDialog dialog = normalDialog.create();

        normalDialog.setTitle("请选择页码")
                        .setItems(items, new DialogInterface.OnClickListener() {

        public void onClick(DialogInterface dialog,
        int which) {
            Integer page = new Integer(which);

            callBack.callback(page);
        }
    });


        normalDialog.show();
        dialog.getWindow().setLayout(SysUtils.getsWindowWidth(context)/10, SysUtils.getsWindowHeight(context)/2);
        }


}
