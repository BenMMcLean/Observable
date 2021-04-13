package cl.benm.observable.base;

import java.util.concurrent.Executor;

import cl.benm.observable.EmissionType;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;

public abstract class MediatedObservable<T,R> extends ValueObservable<R> {

    protected final Observable<T> delegate;
    protected final Executor executor;
    protected Observer<T> observer;

    public MediatedObservable(Observable<T> delegate, Executor executor) {
        this.delegate = delegate;
        this.executor = executor;
    }

    @Override
    protected void onActive() {
        super.onActive();
        delegate.observe(observer, executor);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        delegate.removeObserver(observer);
    }

    @Override
    public EmissionType getEmissionType() {
        return delegate.getEmissionType();
    }

}
