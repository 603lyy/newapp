package cn.yaheen.online.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    /**
     * 获取当前年月日的时间
     */
    public static String getsCurrentTimeYMD() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
}
