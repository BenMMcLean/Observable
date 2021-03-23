package cl.benm.observable;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import cl.benm.observable.concrete.AsyncTransformObservable;
import cl.benm.observable.concrete.TransformObservable;

public abstract class AbstractObservable<T> implements Observable<T> {

    @Override
    public void observeOnce(Observer<T> observer) {
        observe(new Observer<T> () {
            @Override
            public void onChanged(ExceptionOrValue<T> value) {
                observer.onChanged(value);
                removeObserver(this::onChanged);
            }
        });
    }

    protected void emit(ExceptionOrValue<T> value) {}

    @Override
    public <R> Observable<R> transform(Transformation<T, R> transformation) {
        return new TransformObservable<>(this, transformation);
    }

    @Override
    public <R> Observable<R> transformAsync(AsyncTransformation<T, R> transformation) {
        return new AsyncTransformObservable<>(this, transformation);
    }

    protected boolean inEmittableState(LifecycleOwner owner) {
        Lifecycle.State s = owner.getLifecycle().getCurrentState();
        return s == Lifecycle.State.RESUMED || s == Lifecycle.State.STARTED;
    }

}
