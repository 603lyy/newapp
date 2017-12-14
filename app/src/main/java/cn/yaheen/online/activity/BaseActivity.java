package cn.yaheen.online.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import cn.yaheen.online.retrofit.BasePresenter;
import cn.yaheen.online.retrofit.BaseView;

public abstract class BaseActivity<P extends BasePresenter> extends Activity
        implements BaseView {

    protected P presenter;
    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        presenter = initPresenter();
    }

    @Override
    protected void onDestroy() {
        if (presenter != null) {
            presenter.detach();//在presenter中解绑释放view
            presenter = null;
        }
        super.onDestroy();
    }

    /**
     * 在子类中初始化对应的presenter
     *
     * @return 相应的presenter
     */
    public abstract P initPresenter();


    @Override
    public void dismissLoadingDialog() {

    }

    @Override
    public void showLoadingDialog(String msg) {

    }
}
