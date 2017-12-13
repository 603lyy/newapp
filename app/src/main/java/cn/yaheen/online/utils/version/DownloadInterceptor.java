package cn.yaheen.online.utils.version;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/12/13.
 */

public class DownloadInterceptor implements Interceptor {

    private DownloadListener downloadListener;

    public DownloadInterceptor(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //获取request里面的response
        Response response = chain.proceed(chain.request());
        //拦截器更换response里面自己定义的responseBody，获取下载的进度
        return response.newBuilder().body(
                new DownloadResponseBody(response.body(), downloadListener)).build();
    }
}
