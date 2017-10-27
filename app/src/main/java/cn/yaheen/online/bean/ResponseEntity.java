package cn.yaheen.online.bean;

import org.xutils.http.annotation.HttpResponse;


/**
 * Created by linjingsheng on 17/2/20.
 */

@HttpResponse(parser = cn.yaheen.online.bean.ResultParser.class)
public class ResponseEntity {

    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
