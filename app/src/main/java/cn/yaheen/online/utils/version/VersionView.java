package cn.yaheen.online.utils.version;

import java.util.List;

import cn.yaheen.online.bean.JBean;
import cn.yaheen.online.retrofit.BaseView;

/**
 * Created by Administrator on 2017/12/14.
 */

public interface VersionView  extends BaseView{
    /**
     * 设置版本更新apk地址
     * @param url
     */
    void showVersionDialog(String url);

    /**
     * 最新版apk下载完成
     */
    void finishDownload(String fileParh);
}
