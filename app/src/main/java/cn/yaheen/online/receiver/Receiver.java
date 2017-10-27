package cn.yaheen.online.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import cn.yaheen.online.activity.PingJiaoActivity;
import cn.yaheen.online.utils.GridViewAdapter;
import cn.yaheen.online.utils.ToastUtils;

/**
 * Created by linjingsheng on 17/5/1.
 */

public class Receiver extends BroadcastReceiver {

    private Message message;

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getExtras().getString("msg");
        message.getMsg(msg);


    }

    public interface Message {
        public void getMsg(String str);
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
