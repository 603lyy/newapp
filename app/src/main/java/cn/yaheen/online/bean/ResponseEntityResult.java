package cn.yaheen.online.bean;

/**
 * Created by linjingsheng on 17/3/18.
 */

public class ResponseEntityResult {



    public String getState() {
        return state;
    }

    public String getMsg() {
        return msg;
    }

    public String getStatus() {
        return status;
    }


    public void setState(String state) {
        this.state = state;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    private  boolean result;
    private  String state;
    private  String msg;
    private String status;
}
