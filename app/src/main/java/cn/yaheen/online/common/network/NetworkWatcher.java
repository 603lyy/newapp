
package cn.yaheen.online.common.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author jbt
 * @data 2014-12-23
 * @desc 网络状态监视器
 */
public class NetworkWatcher {

    private Context context;

    private INetwordWatcherListener netlistener;

    public NetworkWatcher(Context context, INetwordWatcherListener listener) {
        this.context = context;
        this.netlistener = listener;
        registerNetWorkReceiver();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                ConnectivityManager connectivityManager;
                connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (netlistener != null) {
                    netlistener.networdNotifyChange(info);
                }
            }
        }
    };

    private void registerNetWorkReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mReceiver, mFilter);
    }

    private void unRegisterNetWorkReceiver() {
        if (mReceiver != null){
            context.unregisterReceiver(mReceiver);
        }
    }

    /**
     * 释放网络监听器资源
     */
    public void release() {
        unRegisterNetWorkReceiver();
        mReceiver = null;
    }
}
