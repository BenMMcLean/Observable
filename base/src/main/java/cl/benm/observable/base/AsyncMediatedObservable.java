package cl.benm.observable.base;

import java.util.concurrent.Executor;

import cl.benm.observable.EmissionType;
import cl.benm.observable.ExceptionOrValue;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;

/**
 * An Observable that mediates between a delegated Observable and some function's
 * outputted Observable.
 * @param <T> The input type of the Observable
 * @param <R> The output type
 * @see cl.benm.observable.base.MediatedObservable
 */
public abstract class AsyncMediatedObservable<T,R> extends MediatedObservable<T,R> {

    protected Observable<R> asyncDelegate;

    public AsyncMediatedObservable(Observable<T> delegate, Executor executor) {
        super(delegate, executor);
    }

    /**
     * Provide a new Observable to mediate
     * @param async The new observable
     */
    protected void emit(Observable<R> async) {
        if (asyncDelegate != null) {
            asyncDelegate.removeObserver(asyncObserver);
        }

        asyncDelegate = async;
        if (active) {
            asyncDelegate.observe(asyncObserver, executor);
        }
    }

    /**
     * Handle the output of the async Observable
     */
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
        if (asyncDelegate != null) asyncDelegate.observe(asyncObserver, executor);
        super.onActive();
    }

    @Override
    protected void onInactive() {
        if (asyncDelegate != null) asyncDelegate.removeObserver(asyncObserver);
        super.onInactive();
    }

    // We don't know the type of the returned Observable, so always return MULTIPLE for consistency
    @Override
    public EmissionType getEmissionType() {
        return EmissionType.MULTIPLE;
    }

}
