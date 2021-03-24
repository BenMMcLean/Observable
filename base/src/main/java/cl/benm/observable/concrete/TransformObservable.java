package cl.benm.observable.concrete;

import java.util.concurrent.Executor;

import cl.benm.observable.EmissionType;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;
import cl.benm.observable.Transformation;

public class TransformObservable<T, R> extends ValueObservable<R> {

    private final Observable<T> delegate;
    private final Executor executor;
    private Transformation<T, R> transformation;

    public TransformObservable(Observable<T> delegate, Transformation<T, R> transformation, Executor executor) {
        this.delegate = delegate;
        this.transformation = transformation;
        this.executor = executor;
    }

    private Observer<T> observer = value -> emit(transformation.transform(value));

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
