package cn.yaheen.online.utils.version;

import cn.yaheen.online.retrofit.BaseApi;
import cn.yaheen.online.retrofit.BaseApiImpl;
import cn.yaheen.online.utils.sharepreferences.DefaultPrefsUtil;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/12/14.
 */

public class ApiImpl extends BaseApiImpl {

//    private static ApiImpl api = new ApiImpl(DownloadService.BASE_URL);

    private static ApiImpl api = new ApiImpl();

    public ApiImpl(String baseUrl) {
        super(baseUrl);
    }

    public ApiImpl() {
        super("http://" + DefaultPrefsUtil.getIpUrl() + ":8080/");
    }

    public static DownloadService getInstance() {
        return api.getRetrofit().create(DownloadService.class);
    }
}
