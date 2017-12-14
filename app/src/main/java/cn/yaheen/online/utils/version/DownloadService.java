package cn.yaheen.online.utils.version;

import cn.yaheen.online.bean.JBean;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Administrator on 2017/12/13.
 * 网络请求service
 */

public interface DownloadService {

    String BASE_URL = "http://192.168.250.103:8080/";

    @GET
    Observable<JBean> getVersion(@Url String url);

    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}
