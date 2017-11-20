package cn.yaheen.online.utils.version;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import cn.yaheen.online.bean.JBean;
import cn.yaheen.online.utils.DialogCallback;
import cn.yaheen.online.utils.DialogUtils;
import cn.yaheen.online.utils.IDialogCancelCallback;
import cn.yaheen.online.utils.sharepreferences.DefaultPrefsUtil;

/**
 * Created by Administrator on 2017/11/20.
 */

public class VersionUtils {

    private static int version;

    public static void checkVersion(final Context context, DialogCallback dialogCallback) {

        version = getVersionCode(context);

        RequestParams pa = new RequestParams("http://192.168.250.102:8180/aa.json");
        x.http().get(pa, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson g = new Gson();
                final JBean bean = g.fromJson(result, JBean.class);
                if (bean.getVersion() > version) {
                    DialogUtils.showDialog(context, "有新版本更新，请点击确定跳转至浏览器下载最新安装包", new DialogCallback() {
                        @Override
                        public void callback() {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setData(Uri.parse(bean.getUrl()));
                            context.startActivity(intent);
                        }
                    }, new IDialogCancelCallback() {
                        @Override
                        public void cancelCallback() {

                        }
                    });
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                Log.i("lin", "onError: ");
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {
//                Log.i("lin", "onCancelled: ");
            }

            @Override
            public void onFinished() {
//                Log.i("lin", "onFinished: ");
            }
        });
    }

    /**
     * 获取软件版本号
     *
     * @return
     */
    public static int getVersionCode(Context context) {
        final String packageName = context.getPackageName();
        int version = 1;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            version = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return version;
    }
}
