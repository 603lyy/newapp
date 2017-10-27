package cn.yaheen.online.utils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import cn.yaheen.online.bean.ResponseEntity;


/**
 * Created by linjingsheng on 17/2/20.
 */

public class XHttpUtils {

    public static void uploadMethod(RequestParams params, Callback.CommonCallback<ResponseEntity> callback) {

        params.setMultipart(true);
        Callback.Cancelable cancelable
                = x.http().post(params, callback);

    }

}
