package cn.yaheen.online.bean;

import org.xutils.http.app.ResponseParser;
import org.xutils.http.request.UriRequest;

import java.lang.reflect.Type;

/**
 * Created by linjingsheng on 17/2/20.
 */

public class ResultParser implements ResponseParser {


    @Override
    public void checkResponse(UriRequest request) throws Throwable {

    }

    @Override
    public Object parse(Type resultType, Class<?> resultClass, String result) throws Throwable {

        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setResult(result);
        //返回ResponseEntity对象
        return responseEntity;
    }
}
