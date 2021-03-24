package cl.benm.observable;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.Executor;

import cl.benm.observable.concrete.AsyncTransformObservable;
import cl.benm.observable.concrete.TransformObservable;

public abstract class AbstractObservable<T> implements Observable<T> {

    @Override
    public void observeOnce(Observer<T> observer, Executor executor) {
        observe(new Observer<T> () {
            @Override
            public void onChanged(ExceptionOrValue<T> value) {
                observer.onChanged(value);
                removeObserver(this::onChanged);
            }
        }, executor);
    }

    protected void emit(ExceptionOrValue<T> value) {}

    @Override
    public <R> Observable<R> transform(Transformation<T, R> transformation, Executor executor) {
        return new TransformObservable<>(this, transformation, executor);
    }

    @Override
    public <R> Observable<R> transformAsync(AsyncTransformation<T, R> transformation, Executor executor) {
        return new AsyncTransformObservable<>(this, transformation, executor);
    }

    protected boolean inEmittableState(LifecycleOwner owner) {
        Lifecycle.State s = owner.getLifecycle().getCurrentState();
        return s == Lifecycle.State.RESUMED || s == Lifecycle.State.STARTED;
    }

}
