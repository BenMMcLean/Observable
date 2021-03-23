package cl.benm.observable;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

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

    protected void emit(ExceptionOrValue<T> value) {

    }

    @Override
    public <R> Observable<R> transform(Transformation<T, R> transformation) {
        return null;
    }

    @Override
    public <R> Observable<R> transformAsync(AsyncTransformation<T, R> transformation) {
        return null;
    }

    protected boolean inEmittableState(LifecycleOwner owner) {
        Lifecycle.State s = owner.getLifecycle().getCurrentState();
        return s == Lifecycle.State.RESUMED || s == Lifecycle.State.STARTED;
    }

}
