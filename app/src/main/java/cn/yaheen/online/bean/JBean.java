package cn.yaheen.online.bean;

import android.util.Log;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by wun on 2017/11/17.
 */
public class JBean {

    private  String firstName;

    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


//    RequestParams pa = new RequestParams("http://192.168.1.6/aa.json");
//    x.http().get(pa, new Callback.CommonCallback<String>() {
//        @Override
//        public void onSuccess(String result) {
//            Log.i("lin", "onSuccess: "+result);
//            Gson g = new Gson();
//            JBean bean = g.fromJson(result, JBean.class);
//            Log.i("lin", "onSuccess: "+bean.getFirstName()+"-----"+bean.getLastName());
//        }
//
//        @Override
//        public void onError(Throwable ex, boolean isOnCallback) {
//            Log.i("lin", "onError: ");
//        }
//
//        @Override
//        public void onCancelled(Callback.CancelledException cex) {
//            Log.i("lin", "onCancelled: ");
//        }
//
//        @Override
//        public void onFinished() {
//            Log.i("lin", "onFinished: ");
//        }
//    });
}
