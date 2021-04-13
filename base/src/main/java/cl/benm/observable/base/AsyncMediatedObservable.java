package cl.benm.observable.base;

import java.util.concurrent.Executor;

import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;

public abstract class AsyncMediatedObservable<T,R> extends MediatedObservable<T,R> {

    protected Observable<R> asyncDelegate;

    public AsyncMediatedObservable(Observable<T> delegate, Executor executor) {
        super(delegate, executor);
    }

    protected void emit(Observable<R> async) {
        if (asyncDelegate != null) {
            asyncDelegate.removeObserver(asyncObserver);
        }

        asyncDelegate = async;
        if (active) {
            asyncDelegate.observe(asyncObserver, executor);
        }
    }

    protected Observer<R> asyncObserver = new Observer<R>() {
        @Override
        public void onChanged(R value) {
            emit(new ExceptionOrValue.Value<>(value));
        }

        @Override
        public void onException(Throwable exception) {
            emit(new ExceptionOrValue.Exception<>(exception));
        }
    };

    @Override
    protected void onActive() {
        super.onActive();
        if (asyncDelegate != null) asyncDelegate.observe(asyncObserver, executor);
        delegate.observe(observer, executor);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (asyncDelegate != null) asyncDelegate.removeObserver(asyncObserver);
        delegate.removeObserver(observer);
    }

    // We don't know the type of the returned Observable, so always return MULTIPLE for consistency
    @Override
    public EmissionType getEmissionType() {
        return EmissionType.MULTIPLE;
    }

}
