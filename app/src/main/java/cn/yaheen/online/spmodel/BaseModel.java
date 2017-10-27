package cn.yaheen.online.spmodel;


/**
 * Created by linjingsheng on 17/3/2.
 */

public class BaseModel {

    public SPError getSpError() {
        return spError;
    }

    public void setSpError(SPError spError) {
        this.spError = spError;
    }

    private SPError spError;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    private Integer code;  //返回实体状态，是否错误等
}
