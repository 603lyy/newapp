package cn.yaheen.online.app;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;

import com.alivc.player.AliVcMediaPlayer;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by linjingsheng on 17/2/15.
 */

public class OnlineApp extends Application {


    /**
     * 打开的activity
     **/
    private List<Activity> activities = new ArrayList<Activity>();
    /**
     * 应用实例
     **/
    private static OnlineApp instance;

    private static RefWatcher sRefWatcher;

    public static RefWatcher getRefWatcher() {
        return sRefWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        sRefWatcher = LeakCanary.install(this);

        x.Ext.init(this);
        x.Ext.setDebug(false); //输出debug日志，开启会影响性能
        // BaseManager.initOpenHelper(this);
        instance = this;

        //阿里云播放器
        AliVcMediaPlayer.init(getApplicationContext(), "");
    }


    public Intent getResultIntent() {
        return mResultIntent;
    }

    public void setResultIntent(Intent mResultIntent) {
        this.mResultIntent = mResultIntent;
    }

    public int getResultCode() {
        return mResultCode;
    }

    public void setResultCode(int mResultCode) {
        this.mResultCode = mResultCode;
    }

    private Intent mResultIntent = null;
    private int mResultCode = 0;

    public MediaProjectionManager getMpmngr() {
        return mMpmngr;
    }

    public void setMpmngr(MediaProjectionManager mMpmngr) {
        this.mMpmngr = mMpmngr;
    }

    private MediaProjectionManager mMpmngr;


    /**
     * 获得实例
     *
     * @return
     */
    public static OnlineApp getInstance() {
        return instance;
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 结束指定的Activity
     *
     * @param activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            this.activities.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 应用退出，结束所有的activity,没有考虑到多线程环境下的退出
     */
    public void exit() {
        for (Activity activity : activities) {
            if (activity != null) {
                activity.finish();
            }
        }
        System.exit(0);
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    public void finishActivities() {
        for (Activity activity : activities) {
            if (null != activity) {
                activity.finish();
            }
        }
        //杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
