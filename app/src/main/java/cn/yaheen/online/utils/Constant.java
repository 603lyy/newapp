package cn.yaheen.online.utils;

import android.content.Context;

import cn.yaheen.online.utils.sharepreferences.DefaultPrefsUtil;
import cn.yaheen.online.utils.sharepreferences.SharedPreferencesUtils;

/**
 * Created by linjingsheng on 17/2/23.
 */

public class Constant {

    private static Constant instance;

    private int dmWidth = 0;
    private int dmHeight = 0;

    /**
     * 是否横屏
     */
    private boolean isheng = true;

    private Constant(Context context) {
    }


    public static Constant createConstant(Context context) {
        if (instance == null) {
            synchronized (Constant.class) {
                if (instance == null) {
                    instance = new Constant(context);
                }
            }
        }
        return instance;
    }

    public static String getBaseurl() {
        String Ip = DefaultPrefsUtil.getIpUrl();
        return "http://" + Ip + ":8080/loles";
    }

    public static String getOnlineurl() {
        String Ip = DefaultPrefsUtil.getIpUrl();
        return "rtmp://" + Ip + "/red5Demo/";
    }

    public static String getWsurl() {
        String Ip = DefaultPrefsUtil.getIpUrl();
        return "ws://" + Ip + ":8080/loles";
    }

    public static String baseurl = "http://192.168.199.220:8080/loles";
    public static String onlineurl = "rtmp://192.168.199.244/red5Demo/screen_123456"; //rtmp://192.168.199.244/red5Demo/livestream
    public static String wsurl = "ws://192.168.199.220:8080/loles"; //rtmp://192.168.199.244/red5Demo/livestream

    public static String generateCanvaspicPath(String fileName, String extension) {
        String result;
        if (extension != null && !"".equals(extension) && fileName != null && !"".equals(fileName)) {
            result = android.os.Environment.getExternalStorageDirectory() + "/canvas/" + fileName + extension;
        } else {
            result = android.os.Environment.getExternalStorageDirectory() + "/canvas/";
        }
        return result;
    }


    public static String generateMixpicPath(String fileName, String extension) {
        String result;
        if (extension != null && !"".equals(extension) && fileName != null && !"".equals(fileName)) {
            result = android.os.Environment.getExternalStorageDirectory() + "/mixpic/" + fileName + extension;
        } else {
            result = android.os.Environment.getExternalStorageDirectory() + "/mixpic/";
        }
        return result;
    }

    public static String generateCamerapicPath(String fileName, String extension) {
        String result;
        if (extension != null && !"".equals(extension) && fileName != null && !"".equals(fileName)) {
            result = android.os.Environment.getExternalStorageDirectory() + "/pic/" + fileName + extension;
        } else {
            result = android.os.Environment.getExternalStorageDirectory() + "/pic/";
        }

        return result;
    }

    public static String CanvaspicPathPic = android.os.Environment.getExternalStorageDirectory() + "/canvas/" + UUIDUtils.getUuid() + ".jpg";
    public static String MixpicPathPic = android.os.Environment.getExternalStorageDirectory() + "/mixpic/" + UUIDUtils.getUuid() + ".jpg";
    public static String CamerapicPathPic = android.os.Environment.getExternalStorageDirectory() + "/pic/" + UUIDUtils.getUuid() + ".jpg";

    public static String CanvaspicPathJPK = android.os.Environment.getExternalStorageDirectory() + "/canvas/" + UUIDUtils.getUuid() + ".jpk";
    public static String MixpicPathJPK = android.os.Environment.getExternalStorageDirectory() + "/mixpic/" + UUIDUtils.getUuid() + ".jpk";
    public static String CamerapicPathJPK = android.os.Environment.getExternalStorageDirectory() + "/pic/" + UUIDUtils.getUuid() + ".jpk";

    public void setDmWidth(int dmWidth) {
        this.dmWidth = dmWidth;
    }

    public void setDmHeight(int dmHeight) {
        this.dmHeight = dmHeight;
    }

    public int getDmWidth() {
        return dmWidth;
    }

    public int getDmHeight() {
        return dmHeight;
    }

    public boolean isHeng() {
        return isheng;
    }

    public void setIsHeng(boolean isheng) {
        this.isheng = isheng;
    }
}
