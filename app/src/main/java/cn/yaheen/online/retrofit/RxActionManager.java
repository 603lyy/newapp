package cn.yaheen.online.retrofit;

import org.reactivestreams.Subscription;

import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2017/12/15.
 */

public interface RxActionManager<T> {

    void add(T tag, Disposable subscription);
    void remove(T tag);

    void cancel(T tag);

    void cancelAll();
}
