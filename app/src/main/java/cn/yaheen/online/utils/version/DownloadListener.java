package cn.yaheen.online.utils.version;

/**
 * Created by Administrator on 2017/12/13.
 */

public interface DownloadListener {

    void onStartDownload();

    void onProgress(int progress);

    void onFinishDownload(String filePath);

    void onFail(String errorInfo);
}
