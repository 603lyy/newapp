package cn.yaheen.online.utils.version;

import cn.yaheen.online.retrofit.BasePresenter;

/**
 * Created by Administrator on 2017/12/14.
 */

public interface VersionPresent extends BasePresenter {
    /**
     * 获取最新版本号
     */
    void getVersion(int currentVersion);
}
