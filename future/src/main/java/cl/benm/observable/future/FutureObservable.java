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
    private final Executor executor;

    public FutureObservable(ListenableFuture<T> delegate, Executor executor) {
        this.delegate = delegate;
        this.executor = executor;
    }

    @Override
    public EmissionType getEmissionType() {
        return EmissionType.SINGLE;
    }

    @Override
    public void observe(Observer<T> observer) {
        FluentFuture.from(delegate).addCallback(new FutureCallback<T>() {
            @Override
            public void onSuccess(@NullableDecl T result) {
                observer.onChanged(new ExceptionOrValue.Value<T>(result));
            }

            @Override
            public void onFailure(Throwable t) {
                observer.onChanged(new ExceptionOrValue.Exception<T>(t));
            }
        }, executor);
    }

    @Override
    public void observe(Observer<T> observer, LifecycleOwner lifecycleOwner) {
        observe(observer);
    }

    @Override
    public void observeOnce(Observer<T> observer) {
        observe(observer);
    }

    @Override
    public void removeObserver(Observer<T> observer) {}

    @Override
    public void removeObservers(LifecycleOwner lifecycleOwner) {}

}
