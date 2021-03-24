package cl.benm.observable.concrete;

import java.util.concurrent.Executor;

import cl.benm.observable.EmissionType;
import cl.benm.observable.Observable;
import cl.benm.observable.Observer;
import cl.benm.observable.Transformation;

/**
 * Transforms and emits the emissions of a given Observable
 * @param <T> The input type
 * @param <R> The output type
 */
public class TransformObservable<T, R> extends ValueObservable<R> {

    private final Observable<T> delegate;
    private final Executor executor;
    private Transformation<T, R> transformation;

    /**
     * Instantiate the observable
     * @param delegate The Observable this Observable will be chained to
     * @param transformation The transformation to apply
     * @param executor The thread to execute the transformation on
     */
    public TransformObservable(Observable<T> delegate, Transformation<T, R> transformation, Executor executor) {
        this.delegate = delegate;
        this.transformation = transformation;
        this.executor = executor;
    }

    private final Observer<T> observer = value -> emit(transformation.transform(value));

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
