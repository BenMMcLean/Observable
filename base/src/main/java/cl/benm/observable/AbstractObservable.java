package cl.benm.observable;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.Executor;

import cl.benm.observable.concrete.AsyncTransformObservable;
import cl.benm.observable.concrete.TransformObservable;

/**
 * A class providing common implementations for some Observable methods
 * @param <T> The type of the Observable
 */
public abstract class AbstractObservable<T> implements Observable<T> {

    @Override
    public void observeOnce(Observer<T> observer, Executor executor) {
        /* Wrap the provided observer in an anonymous one that removes itself
           after a single emission*/
        observe(new Observer<T> () {
            @Override
            public void onChanged(ExceptionOrValue<T> value) {
                observer.onChanged(value);
                removeObserver(this);
            }
        }, executor);
    }

    /**
     * Emit a value to the observable
     * @param value The value to emit
     */
    protected abstract void emit(ExceptionOrValue<T> value);

    @Override
    public <R> Observable<R> transform(Transformation<T, R> transformation, Executor executor) {
        return new TransformObservable<>(this, transformation, executor);
    }

    @Override
    public <R> Observable<R> transformAsync(AsyncTransformation<T, R> transformation, Executor executor) {
        return new AsyncTransformObservable<>(this, transformation, executor);
    }

    /**
     * Returns true if the lifecycle owner is in an emittable state
     * @param owner The owner to check
     * @return True if the lifecycle owner is in an emittable state
     */
    protected boolean inEmittableState(LifecycleOwner owner) {
        Lifecycle.State s = owner.getLifecycle().getCurrentState();
        return s == Lifecycle.State.RESUMED || s == Lifecycle.State.STARTED;
    }

}
