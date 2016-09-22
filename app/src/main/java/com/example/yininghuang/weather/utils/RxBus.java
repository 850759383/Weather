package com.example.yininghuang.weather.utils;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Yining Huang on 2016/9/22.
 */

public class RxBus {

    private static RxBus INSTANCE = null;

    private final Subject<Object, Object> _bus = new SerializedSubject<>(PublishSubject.create());

    private RxBus() {

    }

    public static RxBus getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RxBus();
        }
        return INSTANCE;
    }

    public void send(Object o) {
        _bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return _bus;
    }

}
