package cn.yaheen.online.model;

/**
 * Created by linjingsheng on 17/4/28.
 */

public class HttpResult {

    public boolean isResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private  boolean result;
    private  String msg;
}
