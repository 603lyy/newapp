package cn.yaheen.online.spmodel;

/**
 * Created by linjingsheng on 17/3/2.
 */

public class SPError {

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }

    private String msg; //错误信息
    private Integer code; //错误代码

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
