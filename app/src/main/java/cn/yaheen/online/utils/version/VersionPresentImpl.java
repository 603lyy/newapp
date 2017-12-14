package cn.yaheen.online.utils.version;

import cn.yaheen.online.bean.JBean;
import cn.yaheen.online.retrofit.BasePresenterImpl;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/12/14.
 */

public class VersionPresentImpl extends BasePresenterImpl<VersionView> implements VersionPresent {

    public VersionPresentImpl(VersionView view) {
        super(view);
    }

    @Override
    public void getVersion(final int currentVersion) {
        ApiImpl.getInstance().download("loles/apk/version.json")
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        addDisposable(disposable);
                        view.showLoadingDialog("");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JBean>() {
                    @Override
                    public void accept(JBean jBean) throws Exception {
                        view.dismissLoadingDialog();
                        if (jBean.getVersion() > currentVersion) ;
                        {
                            view.showVersionDialog(jBean.getUrl());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        view.dismissLoadingDialog();
                        //toast...
                    }
                });
    }
}
