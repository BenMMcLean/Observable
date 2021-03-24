package cl.benm.observable.future;

import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.concurrent.Executor;

import cl.benm.observable.AbstractObservable;
import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observer;

public class FutureObservable<T> extends AbstractObservable<T> {

    private final ListenableFuture<T> delegate;

    public FutureObservable(ListenableFuture<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public EmissionType getEmissionType() {
        return EmissionType.SINGLE;
    }

    @Override
    public void observe(Observer<T> observer, Executor executor) {
        FluentFuture.from(delegate).addCallback(new FutureCallback<T>() {
            @Override
            public void onSuccess(@NullableDecl T result) {
                observer.onChanged(new ExceptionOrValue.Value<>(result));
            }

            @Override
            public void onFailure(Throwable t) {
                observer.onChanged(new ExceptionOrValue.Exception<>(t));
            }
        }, executor);
    }

    @Override
    public void observe(Observer<T> observer, LifecycleOwner lifecycleOwner, Executor executor) {
        observe(observer, executor);
    }

    @Override
    public void observeOnce(Observer<T> observer, Executor executor) {
        observe(observer, executor);
    }

    @Override
    public void removeObserver(Observer<T> observer) {}

    @Override
    public void removeObservers(LifecycleOwner lifecycleOwner) {}

}
