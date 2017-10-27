package cn.yaheen.online.utils;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by linjingsheng on 17/5/26.
 */

public class Util {
    /**
     * 得到设备屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到设备屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 得到设备的密度
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 把密度转换为像素
     */
    public static int dip2px(Context context, float px) {
        final float scale = getScreenDensity(context);
        return (int) (px * scale + 0.5);
    }


    /***
     * 获取屏幕是否横屏
     * @param context
     * @return
     */
    public static boolean isScreenChange(Context context) {

        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation ; //获取屏幕方向

        if(ori == mConfiguration.ORIENTATION_LANDSCAPE){

//横屏
            return true;
        }else if(ori == mConfiguration.ORIENTATION_PORTRAIT){

//竖屏
            return false;
        }
        return false;
    }



}
