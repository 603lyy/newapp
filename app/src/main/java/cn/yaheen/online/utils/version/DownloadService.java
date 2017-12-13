package cn.yaheen.online.utils.version;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2017/12/13.
 * 网络请求service
 */

public interface DownloadService {

    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}
