package cn.yaheen.online.utils;

import java.lang.reflect.Field;

/**
 * Created by linjingsheng on 17/2/15.
 */

public class ObjectUtils {


    //将origin属性注入到destination中
    public static  <T> void mergeObject(T origin, T destination) {
        if (origin == null || destination == null)
            return;
        if (!origin.getClass().equals(destination.getClass()))
            return;

        Field[] fields = origin.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            try {
                fields[i].setAccessible(true);
                Object value = fields[i].get(origin);
                if (null != value&&!"".equals(value)) {
                    fields[i].set(destination, value);
                }
                fields[i].setAccessible(false);
            } catch (Exception e) {
            }
        }
    }
}
