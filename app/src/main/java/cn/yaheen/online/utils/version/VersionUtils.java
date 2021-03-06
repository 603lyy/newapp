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

    public static void checkVersion(final Context context) {

        version = getVersionCode(context);

//        RequestParams pa = new RequestParams("http://192.168.250.103:8080/aa.json");
        RequestParams pa = new RequestParams("http://" + DefaultPrefsUtil.getIpUrl() + ":8080/loles/apk/version.json");
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
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * 获取软件版本号
     */
    public static int getVersionCode(Context context) {
        final String packageName = context.getPackageName();
        int version = 1;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            version = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获取软件版本名
     */
    public static String getVersionName(Context context) {
        final String packageName = context.getPackageName();
        String name = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }
}
