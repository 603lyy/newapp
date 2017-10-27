package cn.yaheen.online.activity;

import android.app.Application;

import org.xutils.x;

/**
 * Created by linjingsheng on 17/2/15.
 */

public class OnlineApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(false); //输出debug日志，开启会影响性能
    }
}
