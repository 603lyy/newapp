package cn.yaheen.online.utils.version;

import cn.yaheen.online.retrofit.BaseApiImpl;
import cn.yaheen.online.utils.sharepreferences.DefaultPrefsUtil;

/**
 * Created by Administrator on 2017/12/14.
 */

public class DownImpl extends BaseApiImpl {

    private static DownImpl api = new DownImpl();

    public DownImpl(String baseUrl) {
        super(baseUrl);
    }

    public DownImpl() {
        super("http://" + DefaultPrefsUtil.getIpUrl() + ":8080/");
    }

    public static DownloadService getInstance(DownloadListener listener) {
        api.setInterceptor(new DownloadInterceptor(listener));
        api.retrofitBuilder.client(api.httpBuilder.build());
        return api.getRetrofit().create(DownloadService.class);
    }
}
