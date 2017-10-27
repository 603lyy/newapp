package cn.yaheen.online.activity.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by linjingsheng on 17/4/6.
 */

public class ImgService extends Service {

//    private LocalBinder localBinder = new LocalBinder();
//    private CallBack callback;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
