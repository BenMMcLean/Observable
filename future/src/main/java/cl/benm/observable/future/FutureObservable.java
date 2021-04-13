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

/**
 * Wrap a future in an Observable
 * @param <T> The type of the Observable
 */
public class FutureObservable<T> extends AbstractObservable<T> {

    private final ListenableFuture<T> delegate;
    private boolean emittedFirst = false;
    private ExceptionOrValue<T> value = null;

    /**
     * Instantiate the Observable
     * @param delegate The internal future
     */
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
                emittedFirst = true;
                value = new ExceptionOrValue.Value<>(result);
                observer.onChanged(result);
            }

            @Override
            public void onFailure(Throwable t) {
                emittedFirst = true;
                value = new ExceptionOrValue.Exception<>(t);
                observer.onException(t);
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

    @Override
    public boolean hasEmittedFirst() {
        return emittedFirst;
    }

    @Override
    public ExceptionOrValue<T> value() {
        return value;
    }
}
