package cn.yaheen.online.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import cn.yaheen.online.utils.sharepreferences.DefaultPrefsUtil;
import cn.yaheen.online.utils.sharepreferences.SharedPreferencesUtils;

/**
 * Created by linjingsheng on 17/5/4.
 */

public class SysUtils {

    private static DisplayMetrics dm;

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getsWindowWidth(Context context) {
        if (dm == null) {
            dm = context.getResources().getDisplayMetrics();
        }
        return dm.widthPixels;
    }

    /***
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getsWindowHeight(Context context) {
        if (dm == null) {
            dm = context.getResources().getDisplayMetrics();
        }
        return dm.heightPixels;
    }

    /***
     * 获取机器码
     * @return
     */
    public static String android_id(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(android_id)) { //若获取不到则生成一个机器码
            String machineKey = DefaultPrefsUtil.getMachineCode();
            if (machineKey == null || "".equals(machineKey)) {
                android_id = UUIDUtils.getUuid();
                DefaultPrefsUtil.setMachineCode(android_id);
            } else {
                android_id = machineKey;
            }

        }
        return android_id;
    }
}
