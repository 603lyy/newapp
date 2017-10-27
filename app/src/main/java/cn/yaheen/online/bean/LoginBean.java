package cn.yaheen.online.bean;

/**
 * Created by linjingsheng on 17/3/15.
 */

public class LoginBean {

    public void setResult(Boolean result) {
        this.result = result;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    private Boolean result;
    private String msg;
    private String id;
    private String token;
    private String loginTime;

    public Boolean getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getLoginTime() {
        return loginTime;
    }



}
